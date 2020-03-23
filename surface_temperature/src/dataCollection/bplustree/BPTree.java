package dataCollection.bplustree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

@SuppressWarnings("unchecked")
public class BPTree<K, V> implements Serializable {
    protected BNode<K, V> root;
    protected int rank;
    //    protected transient static FSTConfiguration fstConfiguration = FSTConfiguration.createDefaultConfiguration();
    protected String fileNameBase = "./trees/";
    protected int orderForLeaf;
    protected transient static Kryo kryo = new Kryo();

    static {
        kryo.setReferences(true);
    }

    public BPTree() {

    }

    public BPTree(int rank, String name) {
        fileNameBase += name + "/";
        this.rank = rank + 1;
        root = new LeafNode(null, rank + 1, this);
    }

    public boolean insert(int hash, K key, V value) {
        synchronized (this) {
            root.insert(hash, key, value);
            return false;
        }
    }

    public List<V> find(int hash, K key) {
        return this.root.find(hash, key);
    }

    public abstract static class BNode<K, V> implements Serializable {
        protected int[] hashs;
        protected Object[] to;
        protected BNode<K, V> from;
        protected int number;
        protected int rank;
        protected BPTree<K, V> bPTree;

        protected BNode() {

        }

        protected BNode(BNode<K, V> from, int size, BPTree<K, V> bPTree) {
            hashs = new int[size];
            to = new Object[size];
            this.from = from;
            this.rank = size;
            this.bPTree = bPTree;
        }


        public int[] getHashs() {
            return hashs;
        }

        public BNode getFrom() {
            return from;
        }

        public void setHashs(int[] hashs) {
            this.hashs = hashs;
        }

        public void setFrom(BNode from) {
            this.from = from;
        }

        public boolean isLeaf() {
            return to.length == 0 || to[0] == null;
        }

        public boolean isRoot() {
            return from == null;
        }

        public abstract void insert(int key, K ok, V value);

        public abstract List<V> find(int key, K ok);

        public abstract LeafNode<K, V> refreshLeft();
    }

    protected static class PlusNode<K, V> extends BNode<K, V> {
        protected PlusNode() {

        }

        protected PlusNode(BNode<K, V> from, int size, BPTree<K, V> bPTree) {
            super(from, size, bPTree);
        }

        @Override
        public void insert(int key, K ok, V value) {
            int left = 0, right = number - 1;
            if (key > this.hashs[right]) {
                ((BNode<K, V>) this.to[right]).insert(key, ok, value);
                return;
            }
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (key <= this.hashs[mid] || this.hashs[mid] == 0) {
                    right = mid;
                } else {
                    if (left == mid && this.hashs[mid] >= key) {
                        ((BNode<K, V>) this.to[left]).insert(key, ok, value);
                        return;
                    }
                    left = mid + 1;
                }
            }
            ((BNode<K, V>) this.to[right]).insert(key, ok, value);
        }

        @Override
        public List<V> find(int key, K ok) {
            int left = 0, right = number - 1;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (key <= this.hashs[mid] || this.hashs[mid] == 0) {
                    right = mid;
                } else {
                    if (left == mid)
                        return ((BNode<K, V>) this.to[left + 1]).find(key, ok);
                    left = mid;
                }
            }
            return ((BNode<K, V>) this.to[right]).find(key, ok);
        }

        BNode<K, V> insertNode(BNode<K, V> node1, BNode<K, V> node2, int key) {
            int oldKey = 0;
            if (this.number > 0)
                oldKey = this.hashs[this.number - 1];
            if (key == 0 || this.number <= 0) {
                this.hashs[0] = node1.hashs[node1.number - 1];
                this.hashs[1] = node2.hashs[node2.number - 1];
                this.to[0] = node1;
                this.to[1] = node2;
                this.number += 2;
                return this;
            }
            int left = 0;
            while (key != hashs[left] && left < this.number - 1) left++;

            this.hashs[left] = node1.hashs[node1.number - 1];
            this.to[left] = node1;

            int[] tempKeys = new int[rank];
            Object[] tempTo = new Object[rank];
            System.arraycopy(this.hashs, 0, tempKeys, 0, left + 1);
            System.arraycopy(this.to, 0, tempTo, 0, left + 1);
            System.arraycopy(this.hashs, left + 1, tempKeys, left + 2, this.number - left - 1);
            System.arraycopy(this.to, left + 1, tempTo, left + 2, this.number - left - 1);

            tempKeys[left + 1] = node2.hashs[node2.number - 1];
            tempTo[left + 1] = node2;

            this.number++;

            if (this.number < rank) {
                System.arraycopy(tempKeys, 0, this.hashs, 0, this.number);
                System.arraycopy(tempTo, 0, this.to, 0, this.number);
                return this;//TODO
            }

            int middle = this.number / 2;
            PlusNode<K, V> tempNode = new PlusNode<>(from, rank, bPTree);
            tempNode.number = number - middle;
            if (from == null) {
                PlusNode<K, V> tNode = new PlusNode<>(null, rank, bPTree);
                tempNode.from = tNode;
                this.from = tNode;
                bPTree.root = tNode;
                oldKey = 0;
            }
            System.arraycopy(tempKeys, middle, tempNode.hashs, 0, tempNode.number);
            System.arraycopy(tempTo, middle, tempNode.to, 0, tempNode.number);
            for (Object b : tempNode.to) {
                if (b == null) break;
                ((BNode<K, V>) b).from = tempNode;
            }
            this.number = middle;
            this.hashs = new int[rank];
            this.to = new Object[rank];
            System.arraycopy(tempKeys, 0, this.hashs, 0, this.number);
            System.arraycopy(tempTo, 0, this.to, 0, this.number);

            return ((PlusNode<K, V>) from).insertNode(this, tempNode, oldKey);
        }

        @Override
        public LeafNode<K, V> refreshLeft() {
            return ((BNode<K, V>) this.to[0]).refreshLeft();
        }
    }

    protected static class LeafNode<K, V> extends BNode<K, V> {
        private static class KV<K, V> implements KeyAndValues<K, V> {
            ValueNode<K, V> first;
            ValueNode<K, V> last;
            transient int size;
            int hash;

            @Override
            public List<V> get(K key) {
                var list = new ArrayList<V>();
                var now = first;
                while (now != null) {
                    list.add(now.value);
                    now = now.next;
                }
                return list;
            }

            @Override
            public void add(K key, V value) {
                if (get(key).contains(value)) return;
                if (last == null) {
                    first = last = new ValueNode<>(value);
                } else {
                    last.next = new ValueNode<>(value);
                    last = last.next;
                }
                size++;
            }

            @Override
            public void delete(V value) {
                ValueNode<K, V> node = first;
                if (node.value.equals(value)) {
                    first = node.next;
                    if (first == null) {
                        last = null;
                    }
                    size--;
                    return;
                }
                while (node.next != null) {
                    if (node.next.value.equals(value)) {
                        if (node.next.next == null) {
                            last = node;
                        } else {
                            node.next = node.next.next;
                        }
                        size--;
                    }
                }
            }

            @Override
            public boolean contains(K k) {
                return this.get(k).size() != 0;
            }

            @Override
            public int size() {
                if (first == null) return 0;
                else if (size == 0) {
                    var i = 1;
                    var node = first;
                    while (node.next != null) {
                        i++;
                        node = node.next;
                    }
                    size = i;
                }
                return size;
            }

            @Override
            public String toString() {
                return first.value.toString();
            }

            public int getHash() {
                if (first == null) {
                    return 0;
                } else {
                    return hash;
                }
            }
        }

        protected String valueFileName;
        protected transient Object[] kvs;
        protected LeafNode<K, V> left;
        protected LeafNode<K, V> right;

        protected LeafNode() {

        }

        protected LeafNode(BNode<K, V> from, int size, BPTree<K, V> bPTree) {
            super(from, size, bPTree);
            this.left = null;
            this.right = null;
            kvs = new Object[size];
            valueFileName = bPTree.fileNameBase + bPTree.orderForLeaf++ + ".kvs";
        }

        @Override
        public void insert(int key, K ok, V value) {
            if (kvs == null) {
                try {
                    var input = new Input(1 << 20);
                    input.setInputStream(new FileInputStream(valueFileName));
                    this.kvs = kryo.readObject(input, Object[].class);
                    input.close();
//                    var in = fstConfiguration.getObjectInput(new FileInputStream(valueFileName));
//                    kvs = (Object[]) in.readObject(Object[].class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            int oldKey = 0;
            if (this.number > 0)
                oldKey = this.hashs[this.number - 1];
            int left = 0;
            while (left < this.number) {
                if (kvs[left] == null || key < ((KV<K, V>) kvs[left]).getHash())
                    break;
                if (key == ((KV<K, V>) kvs[left]).getHash()) {
                    ((KV<K, V>) kvs[left]).add(ok, value);
                    return;
                }
                left++;
            }

            int[] tempKeys = new int[rank];
            Object[] tempKVs = new Object[rank];
            System.arraycopy(this.hashs, 0, tempKeys, 0, left);
            System.arraycopy(this.kvs, 0, tempKVs, 0, left);
            System.arraycopy(this.hashs, left, tempKeys, left + 1, this.number - left);
            System.arraycopy(this.kvs, left, tempKVs, left + 1, this.number - left);

            tempKeys[left] = key;
            tempKVs[left] = new KV<K, V>();
            ((KV) tempKVs[left]).hash = key;
            ((KV<K, V>) tempKVs[left]).add(ok, value);

            this.number++;

            if (this.number < rank) {
                System.arraycopy(tempKeys, 0, this.hashs, 0, this.number);
                System.arraycopy(tempKVs, 0, this.kvs, 0, this.number);

                BNode<K, V> node = this;
                while (node.from != null) {
                    int tempKey = node.hashs[node.number - 1];
                    if (tempKey > node.from.hashs[node.from.number - 1]) {
                        node.from.hashs[node.from.number - 1] = tempKey;
                        node = node.from;
                    } else {
                        break;
                    }
                }
                return;
            }

            int middle = this.number / 2;
            var tempNode = new LeafNode<>(this.from, rank, bPTree);
            tempNode.number = this.number - middle;

            if (this.from == null) {
                var plusNode = new PlusNode<>(null, rank, bPTree);
                this.from = plusNode;
                tempNode.from = plusNode;
                bPTree.root = plusNode;
                oldKey = 0;
            }
            System.arraycopy(tempKeys, middle, tempNode.hashs, 0, tempNode.number);
            System.arraycopy(tempKVs, middle, tempNode.kvs, 0, tempNode.number);

            this.number = middle;
            this.hashs = new int[rank];
            this.kvs = new Object[rank];
            System.arraycopy(tempKeys, 0, this.hashs, 0, middle);
            System.arraycopy(tempKVs, 0, this.kvs, 0, middle);

            tempNode.right = this.right;
            this.right = tempNode;
            tempNode.left = this;

            ((PlusNode<K, V>) from).insertNode(this, tempNode, oldKey);
        }

        @Override
        public List<V> find(int key, K ok) {
            if (kvs == null)
                synchronized (this) {
                    if (kvs == null) {
                        try {
                            var bytes = Files.readAllBytes(Paths.get(valueFileName));
                            if (bytes.length == 0) return null;
                            var input = new Input(new ByteArrayInputStream(bytes), bytes.length * 2);
                            this.kvs = kryo.readObject(input, Object[].class);
                            input.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            if (this.number <= 0)
                return new ArrayList<>();
            int left = 0, right = this.number - 1;
            int middle = left + (right - left) / 2;
            while (left <= right) {
                if (((KV<K, V>) kvs[middle]).getHash() == key) {
                    return ((KV<K, V>) kvs[middle]).get(ok);
                } else if (((KV<K, V>) kvs[middle]).getHash() > key) {
                    right = middle - 1;
                } else {
                    left = middle + 1;
                }
                middle = left + (right - left) / 2;
            }
            return new ArrayList<>();
        }

        @Override
        public LeafNode<K, V> refreshLeft() {
            if (this.number <= 0)
                return null;
            return this;
        }

        protected void serialize() {
            try {
                var output = new Output(1 << 20);
                output.setOutputStream(new FileOutputStream(valueFileName));
                kryo.writeObject(output, this.kvs);
//                var in = fstConfiguration.getObjectOutput(new FileOutputStream(valueFileName));
//                in.writeObject(this.kvs);
//                in.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void serialize() {
        try {
            var path = fileNameBase.substring(0, fileNameBase.length() - 1) + ".tree";
            if (!Files.exists(Paths.get(fileNameBase))) {
                Files.createDirectories(Paths.get(fileNameBase));
            }
            var output = new Output(new FileOutputStream(path));
            kryo.writeObject(output, this);
            output.close();
            var bNode = root;
            while (!(bNode.getClass().equals(LeafNode.class))) {
                bNode = ((BNode<K, V>) bNode.to[0]);
            }
            while (bNode != null) {
                ((LeafNode<K, V>) bNode).serialize();
                bNode = ((LeafNode<K, V>) bNode).right;
            }
//            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LeafNode<K, V> getFirstLeaf() {
        var bNode = root;
        var i = 1;
        while (!(bNode.getClass().equals(LeafNode.class))) {
            bNode = ((BNode<K, V>) bNode.to[0]);
            i++;
        }
        System.out.println(i);
        return (LeafNode<K, V>) bNode;
    }

    public static Kryo getKryo() {
        return kryo;
    }
}
