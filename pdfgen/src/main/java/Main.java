import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main  {

    public static void main( String[] args ){
        //TODO: implement propper thread pool
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        threadPool.execute(new GeneradorRunnable());
        threadPool.shutdown();
    }

}
