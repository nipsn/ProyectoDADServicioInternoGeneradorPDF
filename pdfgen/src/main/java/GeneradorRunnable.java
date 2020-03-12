import org.apache.fop.apps.*;
import pedido.Pedido;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.Socket;
import java.sql.*;

public class GeneradorRunnable implements Runnable{

    protected Socket clientSocket;
    protected int pedidoId;

    public static final String RESOURCES_DIR;
    public static final String OUTPUT_DIR;

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/db_marketplace";
    static final String USER = "admin";
    static final String PASS = "rooturjc";

    static {
        RESOURCES_DIR = "src/main/resources/";
        OUTPUT_DIR = "src/main/resources/output/";
    }

    public GeneradorRunnable(Socket clientSocket, int pedidoId){
        this.clientSocket = clientSocket;
        this.pedidoId = pedidoId;
    }

    public GeneradorRunnable(){

    }

    @Override
    public void run() {
        boolean existe = false;
        File[] plantillas = new File(RESOURCES_DIR).listFiles();
        for(File file : plantillas){
            if(file.getName().contains(Integer.toString(pedidoId))){
                existe = true;
                break;
            }
        }
        if(!existe) {
            Pedido pedido = buscaEnBBDD();
            objetoAXml(pedido);
        }
        try {
            convertirAPDF();
            File pdf = new File(OUTPUT_DIR + "factura" + this.pedidoId + ".pdf");

            InputStream is = new FileInputStream(pdf);
            OutputStream os = clientSocket.getOutputStream();

            copy(is,os);

            os.close();
            is.close();
            clientSocket.close();

            System.out.println("Factura enviada, eliminando de local...");
            if(pdf.delete()){
                System.out.println("Factura eliminada");
            } else {
                System.err.println("No se ha podido eliminar");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FOPException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

    static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[512];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    private Pedido buscaEnBBDD(){
        Connection conn = null;
        Statement stmt = null;
        int idProd = -1;
        int idDestinatario = -1;
        Pedido pedido = new Pedido();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            System.out.println("Creating statement for id " + this.pedidoId);
            stmt = conn.createStatement();

            String sql = "SELECT * FROM pedido WHERE id="+this.pedidoId;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                pedido.setId(rs.getLong("id"));
                pedido.setPrecio(rs.getDouble("precio"));
                pedido.setDireccionDestino(rs.getString("direccion_destino"));
                pedido.setFecha(rs.getDate("fecha"));

                idProd = rs.getInt("producto_id");
                idDestinatario = rs.getInt("destinatario_id");
            }
            rs.close();

            sql ="SELECT * FROM usuario WHERE id=" + idDestinatario;
            rs = stmt.executeQuery(sql);
            while (rs.next()){
                pedido.setDestinatario(rs.getString("nombre"));//TODO: fix to new naming scheme
            }
            rs.close();

            sql ="SELECT * FROM producto WHERE id=" + idProd;
            rs = stmt.executeQuery(sql);
            while (rs.next()){
                pedido.setNombreProducto(rs.getString("nombre"));//TODO: fix to new naming scheme
            }
            rs.close();
        }catch(SQLException se){
            se.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        return pedido;
    }

    private void objetoAXml(Pedido pedido) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Pedido.class);

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            File file = new File(RESOURCES_DIR + "input" + this.pedidoId + ".xml");

            jaxbMarshaller.marshal(pedido, file);
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void convertirAPDF() throws IOException, FOPException, TransformerException {
        File xsltFile = new File(RESOURCES_DIR + "template.xsl");
        StreamSource xmlSource = new StreamSource(new File(RESOURCES_DIR + "input" + this.pedidoId + ".xml"));
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        File yourFile = new File(OUTPUT_DIR + "factura" + this.pedidoId + ".pdf");
        yourFile.createNewFile();
        FileOutputStream out = new FileOutputStream(yourFile, false);

        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

            Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(xmlSource, res);
        } finally {
            out.close();
        }
    }

}
