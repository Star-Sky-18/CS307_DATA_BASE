package tests;

import client.callbacks.BlockReturnCallBack;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import service.TTCFilter;
import service.QueryableQueue;
import service.jdbctester.JdbcQueryExecutor;
import service.jdbctester.JdbcQueryQueue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FinalTest {

    //    public static void main(String[] args) throws Exception {
    //        var finalTest = new FinalTest();
    //        finalTest.test01_single_no_index_query();
    //    }
    static PrintStream pw;
    static ByteArrayOutputStream bos;

    @BeforeClass
    public static void init() {
//        FileService.getService();
        bos = new ByteArrayOutputStream();
        pw = new PrintStream(bos);
        System.setOut(pw);
    }

    /**
     * 22      * @AfterClass:这个注解表示这个方法会在所有方法执行完毕之后执行，通常用来释放资源
     * 23
     */
    @AfterClass
    public static void after() throws Exception {
        pw = new PrintStream("finalTest_final_5.txt");
        pw.write(bos.toByteArray());
        pw.close();
        JdbcQueryExecutor.close();
        System.err.println("finish");
    }

    @Test
    public void test01_single_no_index_query() throws Exception {
        System.out.println("test01:");
        var filter = new TTCFilter("pall", -10, 20, -100, 100, 172001, 180001);
        for (int j = 1; j <= 5; j++) {
            System.out.println(j);
            QueueFactory.init();
            var rmi = QueueFactory.getRMIQueue();
            var start = System.currentTimeMillis();
            for (int i = 0; i < j * 3; i++) {
                var callBack0 = new BlockReturnCallBack<List<String[]>>(true, true, "1.rmi");
                assert rmi != null;
                rmi.queryTTCByFilterWithoutIndex(filter, callBack0);
                callBack0.block();
            }
            System.out.println("\n-----------------------------");
            System.out.println(System.currentTimeMillis() - start);
            System.err.println(System.currentTimeMillis() - start);
            var jdbc = QueueFactory.getJDBCQueue(false);
            var start2 = System.currentTimeMillis();
            for (int i = 0; i < j * 3; i++) {
                var callBack1 = new BlockReturnCallBack<List<String[]>>(true, true, "1.jdbc");
                jdbc.queryTTCByFilterWithoutIndex(filter, callBack1);
                callBack1.block();
            }
            System.out.println("\n-----------------------------");
            System.err.println(System.currentTimeMillis() - start2);
            System.out.println(System.currentTimeMillis() - start2);
        }
//        asserts(callBack0, callBack1);
    }

    @Test
    public void test02_single_index_direct_query() throws Exception {
        System.out.println("\ntest02:");
        for (int j = 1; j <= 1; j++) {
            System.out.println(j);
            QueueFactory.init();
            var rmi = QueueFactory.getRMIQueue();
            var start = System.currentTimeMillis();
            for (int i = 0; i < j * 10; i++) {
                var callBack0 = new BlockReturnCallBack<List<String[]>>(true, true, "2.rmi");
                rmi.queryTTCByTimeCity(198001, 198001, "Kashi", callBack0);
                callBack0.block();
            }
            System.out.println("\n-----------------------------");
            System.out.println(System.currentTimeMillis() - start);
            System.err.println(System.currentTimeMillis() - start);
            var jdbc = QueueFactory.getJDBCQueue(false);
            var start2 = System.currentTimeMillis();
            for (int i = 0; i < j * 10; i++) {
                var callBack1 = new BlockReturnCallBack<List<String[]>>(true, true, "2.jdbc");
                jdbc.queryTTCByTimeCity(198001, 198001, "Kashi", callBack1);
                callBack1.block();
            }
            System.out.println("\n-----------------------------");
            System.out.println(System.currentTimeMillis() - start2);
            System.err.println(System.currentTimeMillis() - start2);
        }
//        asserts(callBack0, callBack1);
    }

    @Test
    public void test03_single_index_union_query() throws Exception {
        System.out.println("\ntest03:");
        for (int j = 1; j <= 10; j++) {
            System.out.println(j);
            QueueFactory.init();
            var rmi = QueueFactory.getRMIQueue();
            var start = System.currentTimeMillis();
            for (int i = 0; i < 30 * j; i++) {
                var callBack0 = new BlockReturnCallBack<List<String[]>>(true, true, "3.rmi");
                assert rmi != null;
                rmi.queryTCCByVagueCity(191001, 192012, "ss", callBack0);
                callBack0.block();
            }
            System.out.println("\n-----------------------------");
            System.out.println(System.currentTimeMillis() - start);
            System.err.println(System.currentTimeMillis() - start);
            var jdbc = QueueFactory.getJDBCQueue(false);
            var start2 = System.currentTimeMillis();
            for (int i = 0; i < 30 * j; i++) {
                var callBack1 = new BlockReturnCallBack<List<String[]>>(true, true, "3.jdbc");
                jdbc.queryTCCByVagueCity(191001, 192012, "ss", callBack1);
                callBack1.block();
            }
            System.out.println("\n-----------------------------");
            System.err.println(System.currentTimeMillis() - start2);
            System.out.println(System.currentTimeMillis() - start2);
        }
//        asserts(callBack0, callBack1);
    }

    @Test
    public void test04_single_small_query() throws Exception {
        System.out.println("\ntest04:");
        for (int j = 1; j <= 10; j++) {
            System.out.println(j);
            var rmi = QueueFactory.getRMIQueue();
            QueueFactory.init();
            var start = System.currentTimeMillis();
            for (int i = 0; i < 100 * j; i++) {
                var callBack0 = new BlockReturnCallBack<String[]>(true, true, "4.rmi");
                assert rmi != null;
                rmi.queryCCLLByCity("Taian", callBack0);
                callBack0.block();
            }
            System.out.println("\n-----------------------------");
            System.out.println(System.currentTimeMillis() - start);
            System.err.println(System.currentTimeMillis() - start);
            var jdbc = QueueFactory.getJDBCQueue(false);
            var start2 = System.currentTimeMillis();
            for (int i = 0; i < 100 * j; i++) {
                var callBack1 = new BlockReturnCallBack<String[]>(true, true, "4.jdbc");
                jdbc.queryCCLLByCity("Taian", callBack1);
                callBack1.block();
            }
            System.out.println("\n-----------------------------");
            System.err.println(System.currentTimeMillis() - start2);
            System.out.println(System.currentTimeMillis() - start2);
        }
//        asserts(callBack0,callBack1,true);
    }

    @Test
    public void test05_parallel_index_query() throws Exception {
        System.out.println("\ntest05:");
        var flag = true;
        for (int j = 1; j <= 10; j++) {
            System.out.println(j);
            QueueFactory.init();
            var callback0 = new BlockReturnCallBack<List<String[]>>(true, true, "5.rmi");
            callback0.setMax(5 * j);
            for (int i = 0; i < 5 * j; i++) {
                Objects.requireNonNull(QueueFactory.getRMIQueue())
                        .queryTTCByTimeCity(200001, 200001, "Xuanhua", callback0);
            }
            callback0.blockForAll();
            System.out.println("\n-----------------------------");
            var jdbcs = new ArrayList<QueryableQueue>(5 * j);
            for (int i = 0; i < 5 * j; i++) {
                jdbcs.add(QueueFactory.getJDBCQueue(true));
            }
            var callback1 = new BlockReturnCallBack<List<String[]>>(true, true, "5.jdbc");
            callback1.setMax(5 * j);
            for (int i = 0; i < 5 * j; i++) {
                jdbcs.get(i).queryTTCByTimeCity(200001, 200001, "Xuanhua", callback1);
            }
            callback1.blockForAll();
            System.out.println("\n-----------------------------");
            if (flag) {
                j--;
                flag = false;
            }
        }
    }

    @Test
    public void test06_parallel_no_index_query() throws Exception {
        System.out.println("\ntest06:");
        var filter = new TTCFilter("men", -5, 30, -10, 20, 192001, 200001);
        for (int j = 1; j <= 10; j++) {
            System.out.println(j);
            var callback0 = new BlockReturnCallBack<List<String[]>>(true, true, "6.rmi");
            callback0.setMax(5 * j);
            for (int i = 0; i < 5 * j; i++) {
                Objects.requireNonNull(QueueFactory.getRMIQueue())
                        .queryTTCByFilterWithoutIndex(filter, callback0);
            }
            callback0.blockForAll();
            System.out.println("\n-----------------------------");
            var jdbcs = new ArrayList<QueryableQueue>(5 * j);
            for (int i = 0; i < 5 * j; i++) {
                jdbcs.add(QueueFactory.getJDBCQueue(true));
            }
            var callback1 = new BlockReturnCallBack<List<String[]>>(true, true, "6.jdbc");
            callback1.setMax(5 * j);
            for (int i = 0; i < 5 * j; i++) {
                jdbcs.get(i).queryTTCByFilterWithoutIndex(filter, callback1);
            }
            callback1.blockForAll();
            System.out.println("\n-----------------------------");
        }
    }

    @Test
    public void test07_high_concurrency_index_query() throws Exception {
        var flag = true;
        for (int j = 1; j <= 10; j++) {
            System.out.println(j);
            System.err.println(j);
            QueueFactory.init();
            var queue = QueueFactory.getRMIQueue();
            var callback0 = new BlockReturnCallBack<List<String[]>>(true, true, "5.rmi");
            callback0.setMax(50 * 10 * j);
            for (int i = 0; i < 50; i++) {
                for (int k = 0; k < 10 * j; k++)
                    queue.queryTTCByTimeCity(200001, 200001, "Xuanhua", callback0);
            }
            callback0.blockForAll();
            System.out.println("\n-----------------------------");
            var jdbcs = new ArrayList<QueryableQueue>(5 * j);
            for (int i = 0; i < 50; i++) {
                jdbcs.add(QueueFactory.getJDBCQueue(false));
            }
            var callback1 = new BlockReturnCallBack<List<String[]>>(true, true, "5.jdbc");
            callback1.setMax(50 * 10 * j);
            for (int i = 0; i < 50; i++) {
                for (int k = 0; k < 10 * j; k++)
                    jdbcs.get(i).queryTTCByTimeCity(200001, 200001, "Xuanhua", callback1);
            }
            callback1.blockForAll();

            for (int i = 0; i < 50; i++) {
                ((JdbcQueryQueue) jdbcs.get(i)).close();
            }
            System.out.println("\n-----------------------------");
            if (flag) {
                j--;
                flag = false;
            }
        }
    }


    @Test
    public void test08_special_index_query() throws Exception {
        System.out.println("\ntest08:");
        for (int j = 1; j <= 1; j++) {
            System.out.println(j);
            QueueFactory.init();
            var callback0 = new BlockReturnCallBack<List<String[]>>(true, true, "5.rmi");
            callback0.setMax(j*5);
            for (int i = 0; i < 5*j; i++) {
                Objects.requireNonNull(QueueFactory.getRMIQueue())
                        .queryTTCByTimeCity(190001, 200001, "Xuanhua", callback0);
            }
            callback0.blockForAll();
            System.out.println("\n-----------------------------");
            var jdbcs = new ArrayList<QueryableQueue>(5 * j);
            for (int i = 0; i < 5*j; i++) {
                jdbcs.add(QueueFactory.getJDBCQueue(true));
            }
            var callback1 = new BlockReturnCallBack<List<String[]>>(true, true, "5.jdbc");
            callback1.setMax(5*j);
            for (int i = 0; i < 5*j; i++) {
                jdbcs.get(i).queryTTCByTimeCity(190001, 200001, "Xuanhua", callback1);
            }
            callback1.blockForAll();
            System.out.println("\n-----------------------------");
        }
    }

    private void asserts(BlockReturnCallBack<List<String[]>> callBack0, BlockReturnCallBack<List<String[]>> callBack1) throws InterruptedException {
        var rL = callBack0.block().stream().map(s -> {
            s[0] = s[0].substring(0, 4) + "-" + s[0].substring(4) + "-01";
            return s;
        }).map(s -> String.join("", s)).sorted().collect(Collectors.joining());
        var jL = callBack1.block().stream().map(s -> {
            if (s[1].length() > 0)
                s[1] = String.format("%.3f", Double.parseDouble(s[1]));
            if (s[2].length() > 0)
                s[2] = String.format("%.3f", Double.parseDouble(s[2]));
            return s;
        }).map(s -> String.join("", s)).sorted().collect(Collectors.joining());
        assertEquals(rL, jL);
    }

    private void asserts(BlockReturnCallBack<String[]> callBack0, BlockReturnCallBack<String[]> callBack1, boolean b1) throws InterruptedException {
        assertEquals(String.join(",", callBack0.block()).replaceAll("\\s+", ""), String.join(",", callBack1.block()).replaceAll("\\s+", ""));
    }
}
