package dataCollection.bplustree;

import java.io.Serializable;
import java.util.List;

public interface KeyAndValues<K,V> extends Serializable {

    public boolean contains(K k);

    public int size();

    public void add(K key,V value);

    public void delete(V value);

    public List<V> get(K k);
}
