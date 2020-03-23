package tests;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import dataCollection.bplustree.BPTree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

public class BTreeTest {
    public static void main(String[] args) throws Exception {
        readTest();
    }

    static void createTest() throws FileNotFoundException {
        var bTree = new BPTree<Integer, Integer>(100, "TestTree");
        int[] ints = new int[8000000];
        var r = new Random();
        for (int i = 0; i < ints.length; i++) {
            ints[i] = r.nextInt();
        }
        var insert = System.currentTimeMillis();
        for (int i = 1; i < ints.length; i++) {
            bTree.insert(ints[i], ints[i], ints[i]);
        }
        var query = System.currentTimeMillis();
        System.out.println("insert: " + (query - insert));
        for (int i = 1; i < ints.length; i++) {
            if (bTree.find(ints[i], ints[i]) != null)
                if (bTree.find(ints[i], ints[i]).get(0) != ints[i])
                    System.out.println("WRong!!!");
        }
        var serialize = System.currentTimeMillis();
        System.out.println("query: " + (serialize - query));
        bTree.serialize();
        System.out.println(System.currentTimeMillis() - serialize);
        var output = new Output(new FileOutputStream("ints"), 50000000);
        BPTree.getKryo().writeObject(output, ints);
        output.close();
    }

    static void readTest() throws FileNotFoundException {

        var start = System.currentTimeMillis();
        var input = new Input(new FileInputStream("trees/TestTree.tree"), 10106642);
        var bTree = (BPTree<Integer, Integer>) BPTree.getKryo().readObject(input, BPTree.class);
        input.close();
        var sInts = System.currentTimeMillis();
        System.out.println("antiserialize: " + (sInts - start));
        input = new Input(new FileInputStream("ints"), 50000000);
        var ints = BPTree.getKryo().readObject(input, int[].class);
        var query = System.currentTimeMillis();
        System.out.println("anti ints: " + (query - sInts));
        for (int i = 1; i < 2; i++) {
            if (bTree.find(ints[i], ints[i]).get(0) != ints[i])
                System.out.println("WRong!!!");
        }
        System.out.println("query: " + (System.currentTimeMillis() - query));
        query = System.currentTimeMillis();
        for (int i = 1; i < ints.length; i++) {
            if (bTree.find(ints[i], ints[i]).get(0) != ints[i])
                System.out.println("WRong!!!");
        }
        System.out.println("query: " + (System.currentTimeMillis() - query));
    }
}
