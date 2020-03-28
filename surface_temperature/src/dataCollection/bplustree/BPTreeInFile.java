package dataCollection.bplustree;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.nustaq.serialization.FSTConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
public class BPTreeInFile<K, V> implements Serializable {

    protected BNode root;
    protected int rank;
    protected String fileName = "./fileTrees/";
    protected transient static Kryo kryo = new Kryo();
    protected int count = 0;

    static {
        kryo.setReferences(true);
    }

    public BPTreeInFile() {

    }

    public BPTreeInFile(int rank, String name) {
        fileName += name + "/";
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

    public abstract static class BNode<K,V> implements Serializable {
        protected int[] hashs;
        protected String name;
        protected transient Object[] to;
        protected String[] _to;
        protected int rank;
        protected String baseName;
        protected transient BNode from;
        protected int number;
        protected transient BPTreeInFile<K, V> bPTree;

        protected BNode() {

        }

        protected BNode(BNode from, int size, BPTreeInFile<K, V> bPTree) {
            hashs = new int[size];
            to = new Object[size];
            _to = new String[size];
            this.from = from;
            this.bPTree = bPTree;
            this.rank = size;
            this.baseName = bPTree.fileName;
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

        public abstract LeafNode refreshLeft();

        protected abstract void serialize();
    }

    protected static class PlusNode<K,V> extends BNode<K,V> {
        protected PlusNode() {

        }

        protected PlusNode(BNode from, int size, BPTreeInFile<K, V> bPTree) {
            super(from, size, bPTree);
            this.name = bPTree.count++ + ".n";
        }

        @Override
        public void insert(int key, K ok, V value) {
            int left = 0, right = number - 1;
            if (key > this.hashs[right]) {
                ((BNode) this.to[right]).insert(key, ok, value);
                return;
            }
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (key <= this.hashs[mid] || this.hashs[mid] == 0) {
                    right = mid;
                } else {
                    if (left == mid && this.hashs[mid] >= key) {
                        ((BNode) this.to[left]).insert(key, ok, value);
                        return;
                    }
                    left = mid + 1;
                }
            }
            ((BNode) this.to[right]).insert(key, ok, value);
        }

        @Override
        public List<V> find(int key, K ok) {
            if (to == null)
                synchronized (this) {
                    if (to == null) to = new Object[this.hashs.length];
                }
            int left = 0, right = number - 1;
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (key <= this.hashs[mid] || this.hashs[mid] == 0) {
                    right = mid;
                } else {
                    if (left == mid) {
                        right = left + 1;
                        break;
                    }
                    left = mid;
                }
            }
            try {
                BNode next = to[right] == null ? null : (BNode) to[right];
                var s = System.currentTimeMillis();
                if (next == null) {
                    synchronized (this) {
                        if (to[right] == null) {
                            var bytes = Files.readAllBytes(Paths.get(baseName+_to[right]));
//                            var bytes = new byte[10];//TODO
                            var input = new Input(bytes);
                            synchronized (kryo) {
                                if (_to[right].contains(".l")) {
                                    next = kryo.readObject(input, LeafNode.class);
                                } else {
                                    next = kryo.readObject(input, PlusNode.class);
                                }
                            }
                            to[right] = next;
                            input.close();
                        } else {
                            next = (BNode) to[right];
                        }
                    }
                }
                System.err.println(System.currentTimeMillis()-s);
                return next.find(key, ok);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        BNode insertNode(BNode node1, BNode node2, int key) {
            int oldKey = 0;
            if (this.number > 0)
                oldKey = this.hashs[this.number - 1];
            if (key == 0 || this.number <= 0) {
                this.hashs[0] = node1.hashs[node1.number - 1];
                this.hashs[1] = node2.hashs[node2.number - 1];
                this._to[0] = node1.name;
                this._to[1] = node2.name;
                this.to[0] = node1;
                this.to[1] = node2;
                this.number += 2;
                return this;
            }
            int left = 0;
            while (key != hashs[left] && left < this.number - 1) left++;

            this.hashs[left] = node1.hashs[node1.number - 1];
            this.to[left] = node1;
            this._to[left] = node1.name;

            int[] tempKeys = new int[rank];
            Object[] tempTo = new Object[rank];
            var temp_tp = new String[rank];
            System.arraycopy(this.hashs, 0, tempKeys, 0, left + 1);
            System.arraycopy(this.to, 0, tempTo, 0, left + 1);
            System.arraycopy(this._to, 0, temp_tp, 0, left + 1);
            System.arraycopy(this.hashs, left + 1, tempKeys, left + 2, this.number - left - 1);
            System.arraycopy(this.to, left + 1, tempTo, left + 2, this.number - left - 1);
            System.arraycopy(this._to, left + 1, temp_tp, left + 2, this.number - left - 1);

            tempKeys[left + 1] = node2.hashs[node2.number - 1];
            tempTo[left + 1] = node2;
            temp_tp[left + 1] = node2.name;

            this.number++;

            if (this.number < rank) {
                System.arraycopy(tempKeys, 0, this.hashs, 0, this.number);
                System.arraycopy(tempTo, 0, this.to, 0, this.number);
                System.arraycopy(temp_tp, 0, this._to, 0, this.number);
                return this;//TODO
            }

            int middle = this.number / 2;
            PlusNode tempNode = new PlusNode(from, rank, bPTree);
            tempNode.number = number - middle;
            if (from == null) {
                PlusNode tNode = new PlusNode(null, rank, bPTree);
                tempNode.from = tNode;
                this.from = tNode;
                bPTree.root = tNode;
                oldKey = 0;
            }
            System.arraycopy(tempKeys, middle, tempNode.hashs, 0, tempNode.number);
            System.arraycopy(tempTo, middle, tempNode.to, 0, tempNode.number);
            System.arraycopy(temp_tp, middle, tempNode._to, 0, tempNode.number);
            for (Object b : tempNode.to) {
                if (b == null) break;
                ((BNode) b).from = tempNode;
            }
            this.number = middle;
            this.hashs = new int[rank];
            this.to = new Object[rank];
            this._to = new String[rank];
            System.arraycopy(tempKeys, 0, this.hashs, 0, this.number);
            System.arraycopy(tempTo, 0, this.to, 0, this.number);
            System.arraycopy(temp_tp, 0, this._to, 0, this.number);

            return ((PlusNode) from).insertNode(this, tempNode, oldKey);
        }

        @Override
        public LeafNode refreshLeft() {
            return ((BNode) this.to[0]).refreshLeft();
        }

        protected void serialize() {
            if (this != bPTree.root) {
                var output = new Output(1 << 20);
                try {
                     output.setOutputStream(new BufferedOutputStream(new FileOutputStream(baseName+name)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                kryo.writeObject(output, this);
                output.close();
            }
            for (var b : this.to) {
                if (b == null)
                    break;
                ((BNode) b).serialize();
            }
        }

    }

    protected static class LeafNode<K,V> extends BNode<K,V> {
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

        protected KV[] kvs;
        protected transient LeafNode left;
        protected transient LeafNode right;

        protected LeafNode() {

        }

        protected LeafNode(BNode from, int size, BPTreeInFile<K, V> bPTree) {
            super(from, size, bPTree);
            this.left = null;
            this.right = null;
            kvs = new KV[size];
            this.name = bPTree.count++ + ".l";
        }

        @Override
        public void insert(int key, K ok, V value) {
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

            var tempKeys = new int[hashs.length];
            var tempKVs = new Object[hashs.length];
            System.arraycopy(this.hashs, 0, tempKeys, 0, left);
            System.arraycopy(this.kvs, 0, tempKVs, 0, left);
            System.arraycopy(this.hashs, left, tempKeys, left + 1, this.number - left);
            System.arraycopy(this.kvs, left, tempKVs, left + 1, this.number - left);

            tempKeys[left] = key;
            tempKVs[left] = new KV<>();
            ((KV) tempKVs[left]).hash = key;
            ((KV<K, V>) tempKVs[left]).add(ok, value);

            this.number++;

            if (this.number < hashs.length) {
                System.arraycopy(tempKeys, 0, this.hashs, 0, this.number);
                System.arraycopy(tempKVs, 0, this.kvs, 0, this.number);

                BNode node = this;
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
            var tempNode = new LeafNode(this.from, hashs.length, bPTree);
            tempNode.number = this.number - middle;

            if (this.from == null) {
                BNode plusNode = new PlusNode(null, hashs.length, bPTree);
                this.from = plusNode;
                tempNode.from = plusNode;
                bPTree.root = plusNode;
                oldKey = 0;
            }
            System.arraycopy(tempKeys, middle, tempNode.hashs, 0, tempNode.number);
            System.arraycopy(tempKVs, middle, tempNode.kvs, 0, tempNode.number);

            this.number = middle;
            this.hashs = new int[hashs.length];
            this.kvs = new KV[hashs.length];
            System.arraycopy(tempKeys, 0, this.hashs, 0, middle);
            System.arraycopy(tempKVs, 0, this.kvs, 0, middle);

            tempNode.right = this.right;
            this.right = tempNode;
            tempNode.left = this;

            ((PlusNode) from).insertNode(this, tempNode, oldKey);
        }

        @Override
        public List<V> find(int key, K ok) {
            if (this.number <= 0)
                return new ArrayList<>();
            int left = 0, right = this.number - 1;
            int middle = left + (right - left) / 2;
            while (left <= right) {
                if (kvs[middle].getHash() == key) {
                    return kvs[middle].get(ok);
                } else if (kvs[middle].getHash() > key) {
                    right = middle - 1;
                } else {
                    left = middle + 1;
                }
                middle = left + (right - left) / 2;
            }
            return new ArrayList<>();
        }

        @Override
        public LeafNode refreshLeft() {
            if (this.number <= 0)
                return null;
            return this;
        }

        protected void serialize() {
            var output = new Output(1 << 20);
            try {
                output.setOutputStream(new FileOutputStream(baseName + name));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            kryo.writeObject(output, this);
            output.close();
        }

    }

    public void serialize() {
        try {
            var path = fileName.substring(0, fileName.length() - 1) + ".tree";
            if (!Files.exists(Paths.get(fileName))) {
                Files.createDirectories(Paths.get(fileName));
            }
            var output = new Output(new FileOutputStream(path));
            kryo.writeObject(output, this);
            output.close();
            root.serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LeafNode getFirstLeaf() {
        var bNode = root;
        var i = 1;
        while (!(bNode.getClass().equals(LeafNode.class))) {
            bNode = ((BNode) bNode.to[0]);
            i++;
        }
        System.out.println(i);
        return (LeafNode) bNode;
    }

    public static Kryo getKryo() {
        return kryo;
    }
}

