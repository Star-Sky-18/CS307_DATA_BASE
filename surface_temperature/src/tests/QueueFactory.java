package tests;

import client.callbacks.PrintCallBack;
import service.TestableQueue;
import service.jdbctester.JdbcTester;
import service.rmiserver.ServiceQueue;

import java.rmi.registry.LocateRegistry;
import java.util.List;

public class QueueFactory {
    static final String BASE = "tester";
    static final String PASS = "123456";
    static int count = 1;
    static void init(){
        count = 1;
    }
    static TestableQueue getRMIQueue(){
        try {
            var registry = LocateRegistry.getRegistry("localhost", 7718);
            return (ServiceQueue) registry.lookup("queue");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    static TestableQueue getJDBCQueue(boolean autoClose){
        if(count==51) count=1;
        return new JdbcTester(BASE+count++,PASS,autoClose);
    }
}
