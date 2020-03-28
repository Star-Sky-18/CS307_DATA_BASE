package service.jdbctester;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JdbcQueryExecutor {
    private static JdbcQueryExecutor executor = new JdbcQueryExecutor();
    private static final int CORE_POOL_SIZE =50;
    private static final int MAX_POOL_SIZE =50;
    private static final int KEEP_ALIVE_TIME =60;

    private ThreadPoolExecutor poolExecutor;
    private JdbcQueryExecutor(){
        poolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE
                ,KEEP_ALIVE_TIME, TimeUnit.SECONDS,new ArrayBlockingQueue<>(5000));
    }

    protected static JdbcQueryExecutor getExecutor(){
        return executor;
    }

    protected void execute(Runnable r){
        this.poolExecutor.execute(r);
    }

    public static void close(){
        executor.poolExecutor.shutdown();
    }
}
