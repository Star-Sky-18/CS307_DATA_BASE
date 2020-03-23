package dataCollection.bplustree;

import java.io.Serializable;

public class ValueNode<K, V> implements Serializable {
    ValueNode<K, V> next;
    V value;

    ValueNode() {

    }

    ValueNode(V value) {
        this.value = value;
    }
}

