package tests;

import client.callbacks.BlockReturnCallBack;
import service.QueryableQueue;
import service.jdbctester.JdbcQueryExecutor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ConcurrencyTest {
    public static void main(String[] args) throws Exception {
//        var list = new ArrayList<QueryableQueue>();
//        for (int i = 0; i < 50; i++) {
//            list.add(QueueFactory.getJDBCQueue(false));
//        }
//        var cb = new BlockReturnCallBack<List<String[]>>(false, true, "?");
//        cb.setMax(5000);
//        var start = System.currentTimeMillis();
//        for (int j = 0; j < 100; j++) {
//            for (int i = 0; i < 50; i++) {
//                list.get(i).queryTTCByTimeCity(200001, 200001, "Beian", cb);
//            }
//        }
        var queue = QueueFactory.getJDBCQueue(false);
        var cb = new BlockReturnCallBack<List<String[]>>(false,true,"");
        cb.setMax(100);
        var start = System.currentTimeMillis();
        for(int i=0;i<100;i++){
            queue.queryTTCByTimeCity(199101,199101,"Beian",cb);
        }
        cb.blockForAll();
        System.out.println(System.currentTimeMillis()-start);
        JdbcQueryExecutor.close();
        System.out.println("?");
    }
}
