import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main  {

    public static void main( String[] args ){
        ThreadPooledServer server = new ThreadPooledServer(10000);
        new Thread(server).start();
    }

}
