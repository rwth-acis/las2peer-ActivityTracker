package de.rwth.dbis.acis.activitytracker.service.helpers;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentFifoHashmap<K, V> extends ConcurrentHashMap<K, V> {

    private int maxSize;
    private LinkedList<K> queue;

    public ConcurrentFifoHashmap(int size) {
        super(size);
        maxSize = size;
        queue = new LinkedList<>();
    }

    @Override
    public V put(K key, V value) {
        if (this.size() < maxSize) {
            queue.addFirst(key);
        } else if (this.containsKey(key)) {
            K last = queue.removeLast();
            remove(last);
        }
        return super.put(key, value);
    }
}
