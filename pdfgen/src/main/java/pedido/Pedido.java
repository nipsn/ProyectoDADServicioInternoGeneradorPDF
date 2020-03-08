package pedido;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Pedido implements Serializable {

	private long id;
	private double precio;
	private String direccionDestino;
	private Date fecha;
	private String destinatario;
	private String nombreProducto;

	public Pedido() {
		this.id = -1;
		this.precio = -1;
		this.direccionDestino = "";
		this.fecha = null;
		this.destinatario = "";
		this.nombreProducto = "";
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public void setDireccionDestino(String direccionDestino) {
		this.direccionDestino = direccionDestino;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public long getId() {
		return id;
	}

	public double getPrecio() {
		return precio;
	}

	public String getDireccionDestino() {
		return direccionDestino;
	}

	public Date getFecha() {
		return fecha;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}
}
