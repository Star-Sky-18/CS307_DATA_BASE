package tests;

import client.callbacks.BlockReturnCallBack;
import client.callbacks.PrintCallBack;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import service.TTCFilter;
import service.TestableQueue;

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

    @BeforeClass
    public static void init() {

    }

    /**
     * 22      * @AfterClass:这个注解表示这个方法会在所有方法执行完毕之后执行，通常用来释放资源
     * 23
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
//        Thread.sleep(20000);
    }

    @Test
    public void test01_single_no_index_query() throws Exception {
        QueueFactory.init();
        var rmi = QueueFactory.getRMIQueue();
        var jdbc = QueueFactory.getJDBCQueue(false);
        var filter = new TTCFilter("bei", -20, 0, -100, 100, 182001, 190001);
        var start = System.currentTimeMillis();
        for(int i=0;i<50;i++) {
            var callBack0 = new BlockReturnCallBack<List<String[]>>(true, true, "1.rmi");
            assert rmi != null;
            rmi.queryTTCByFilterWithoutIndex(filter, callBack0);
            callBack0.block();
        }
        var start2 = System.currentTimeMillis();
        System.err.println(start2-start);
        for(int i=0;i<500;i++) {
            var callBack1 = new BlockReturnCallBack<List<String[]>>(true, true, "1.jdbc");
            jdbc.queryTTCByFilterWithoutIndex(filter, callBack1);
            callBack1.block();
        }
        System.err.println(System.currentTimeMillis()-start2);
//        asserts(callBack0, callBack1);
    }

    @Test
    public void test02_single_index_direct_query() throws Exception {
        QueueFactory.init();
        var rmi = QueueFactory.getRMIQueue();
        var jdbc = QueueFactory.getJDBCQueue(false);
        var start = System.currentTimeMillis();
        for(int i=0;i<1000;i++) {
            var callBack0 = new BlockReturnCallBack<List<String[]>>(true, true, "2.rmi");
            assert rmi != null;
            rmi.queryTTCByTimeCity(199001, 200012, "Beian", callBack0);
            callBack0.block();
        }
        var start2 = System.currentTimeMillis();
        System.err.println(start2-start);
        for(int i=0;i<1000;i++) {
            var callBack1 = new BlockReturnCallBack<List<String[]>>(true, true, "2.jdbc");
            jdbc.queryTTCByTimeCity(199001, 200012, "Beian", callBack1);
            callBack1.block();
        }
        System.err.println(System.currentTimeMillis()-start2);
//        asserts(callBack0, callBack1);
    }

    @Test
    public void test03_single_index_union_query() throws Exception{
        QueueFactory.init();
        var rmi = QueueFactory.getRMIQueue();
        var jdbc = QueueFactory.getJDBCQueue(false);
        var start = System.currentTimeMillis();
        for(int i=0;i<1000;i++) {
            var callBack0 = new BlockReturnCallBack<List<String[]>>(true, true, "3.rmi");
            assert rmi != null;
            rmi.queryTCCByVagueCity(199001, 200012, "Bei", callBack0);
            callBack0.block();
        }
        var start2 = System.currentTimeMillis();
        System.err.println(start2-start);
        for(int i=0;i<1000;i++) {
            var callBack1 = new BlockReturnCallBack<List<String[]>>(true, true, "3.jdbc");
            jdbc.queryTCCByVagueCity(199001, 200012, "Bei", callBack1);
            callBack1.block();
        }
        System.err.println(System.currentTimeMillis()-start2);
//        asserts(callBack0, callBack1);
    }

    @Test
    public void test04_single_small_query() throws Exception{
        QueueFactory.init();
        var rmi = QueueFactory.getRMIQueue();
        var jdbc = QueueFactory.getJDBCQueue(false);
        var start = System.currentTimeMillis();
        for(int i=0;i<10000;i++) {
            var callBack0 = new BlockReturnCallBack<String[]>(true, true, "4.rmi");
            assert rmi != null;
            rmi.queryCCLLByCity("Beian", callBack0);
            callBack0.block();
        }
        var start2 = System.currentTimeMillis();
        System.err.println(start2-start);
        for(int i=0;i<10000;i++) {
            var callBack1 = new BlockReturnCallBack<String[]>(true, true, "4.jdbc");
            jdbc.queryCCLLByCity("Beian", callBack1);
            callBack1.block();
        }
        System.err.println(System.currentTimeMillis()-start2);
    }

    @Test
    public void test05_32_parallel_index_query() throws Exception{
        QueueFactory.init();
        var callback0 = new BlockReturnCallBack<List<String[]>>(true,true,"5.rmi");
        callback0.setMax(32);
        for(int i=0;i<32;i++) {
            Objects.requireNonNull(QueueFactory.getRMIQueue())
                    .queryTTCByTimeCity(180001,190001,"Würzburg",callback0);
        }
        callback0.blockForAll();
        var jdbcs = new ArrayList<TestableQueue>(32);
        for (int i=0;i<32;i++){
            jdbcs.add(QueueFactory.getJDBCQueue(true));
        }
        var callback1 = new BlockReturnCallBack<List<String[]>>(true,true,"5.jdbc");
        callback1.setMax(32);
        for(int i=0;i<32;i++){
            jdbcs.get(i).queryTTCByTimeCity(180001,190001,"Würzburg",callback1);
        }
        callback1.blockForAll();
    }

    @Test
    public void test06_32_parallel_no_index_query() throws Exception{
        var filter = new TTCFilter("bei", -10, 0, -100, 100, 182001, 190001);
        var callback0 = new BlockReturnCallBack<List<String[]>>(true,true,"6.rmi");
        callback0.setMax(32);
        for(int i=0;i<32;i++) {
            Objects.requireNonNull(QueueFactory.getRMIQueue())
                    .queryTTCByFilterWithoutIndex(filter,callback0);
        }
        callback0.blockForAll();
        var jdbcs = new ArrayList<TestableQueue>(32);
        for (int i=0;i<32;i++){
            jdbcs.add(QueueFactory.getJDBCQueue(true));
        }
        var callback1 = new BlockReturnCallBack<List<String[]>>(true,true,"6.jdbc");
        callback1.setMax(32);
        for(int i=0;i<32;i++){
            jdbcs.get(i).queryTTCByFilterWithoutIndex(filter,callback1);
        }
        callback1.blockForAll();
    }

//    @Test
//    public void test07_

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
}
