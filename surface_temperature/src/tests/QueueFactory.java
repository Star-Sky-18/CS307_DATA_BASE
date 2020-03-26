package tests;

import service.QueryableQueue;
import service.jdbctester.JdbcQueryHandler;
import service.jdbctester.JdbcQueryQueue;
import service.rmiserver.ServiceQueue;

import java.rmi.registry.LocateRegistry;

public class QueueFactory {
    static final String BASE = "tester";
    static final String PASS = "123456";
    static int count = 1;
    static void init(){
        count = 1;
    }
    static JdbcQueryHandler handler = new JdbcQueryHandler();
    static QueryableQueue getRMIQueue(){
        try {
            var registry = LocateRegistry.getRegistry("localhost", 7718);
            return (ServiceQueue) registry.lookup("queue");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    static QueryableQueue getJDBCQueue(boolean autoClose){
        if(count==51) count=1;
        return new JdbcQueryQueue(BASE+count++,PASS,autoClose,handler);
    }
}
