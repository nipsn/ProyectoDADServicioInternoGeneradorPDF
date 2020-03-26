import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main  {

    public static void main( String[] args ){
        ThreadPooledServer server = new ThreadPooledServer(10000);
        System.out.println(System.getProperty("user.home"));
        System.out.println("Servicio interno inicializado");
        new Thread(server).start();
    }

}
