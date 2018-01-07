package dlassignment.pkg1;

import dlassignment.pkg1.HuffmanEncoder.HuffmanEncodedResult;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author dt817
 */
public class HashtablePro implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_CAPACITY = 16;
    private int capacity;
    private Object[] keys;
    private Object[] values;
    private int size;
    
    //Special Usage
    private Object values1;
    private Object keys1;

    
    
    public HashtablePro() {
        capacity = DEFAULT_CAPACITY;
        keys = new Object[capacity];
        values = new Object[capacity];
         }

    public HashtablePro(int cap) {
        capacity = cap;
        keys = new Object[capacity];
        values = new Object[capacity];
    }

   
    public Object[] getKeys() {
        return keys;
    }

    public Object[] getValues() {
        return values;
    }
    
     public Object getKeys1(){
        return keys1;
    }
    public Object getValues1(){
        return values1;
    }

    public Float loadFactor() {
        return (float) size / capacity;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private int hash(Object key) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    private void resize(int cap) {
        HashtablePro temp = new HashtablePro(cap);
        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null) {
                temp.put(keys[i], values[i]);
            }
        }
        keys = temp.keys;
        values = temp.values;
        capacity = temp.capacity;
    }

    public void put(Object key, Object value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            remove(key);
            return;
        }
        if (loadFactor() >= 0.5) {
            resize(capacity * 2);
        }
        int i;
        for (i = hash(key); keys[i] != null; i = (i + 1) % capacity) {
            if (keys[i].equals(key)) {
                values[i] = value;
                return;
            }
        }
        keys[i] = key;
        values[i] = value;
        //Special Usage
        keys1 = key;
        values1 = value;
        size++;
    }

    public Object get(Object key) {

        if (key == null) {
            return null;
        }
        if (key instanceof String || key instanceof Long) {
            int i;
            for (i = hash(key); keys[i] != null; i = (i + 1) % capacity) {
                if (keys[i].equals(key)) {
                    return values[i];
                }
            }
        } else if (key instanceof Integer) {
                if (keys[(Integer)key] != null) {
                    return values[(Integer)key];
                }
        }
        return 0;
    }

    public void remove(Object key) {
        if (key == null || get(key) == null) {
            return;
        }
        int i = hash(key);
        while (!key.equals(keys[i])) {
            i = (i + 1) % capacity;
        }
        keys[i] = null;
        values[i] = null;

        while (keys[i] != null) {
            Object rehashKey = keys[i];
            Object rehashValue = values[i];
            keys[i] = null;
            values[i] = null;
            size--;
            put(rehashKey, rehashValue);
            i = (i + 1) % capacity;
        }
        size--;

        if (loadFactor() <= 0.25 && capacity > 0) {
            resize(capacity / 2);
        }
    }

    void writeObject(ObjectOutputStream s) throws IOException {
        final HuffmanEncoder encoder = new HuffmanEncoder();
        s.writeInt(size);
        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null) {
                final HuffmanEncodedResult result = encoder.compress(keys[i].toString());
                s.writeObject(result);
                System.out.println(encoder.decompress(result));
                s.writeObject(values[i]);
            }
        }
        s.close();
    }

    void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        final HuffmanEncoder encoder = new HuffmanEncoder();
        int length = s.readInt();
        for (int i = 0; i < length; i++) {
            Object key = encoder.decompress((HuffmanEncoder.HuffmanEncodedResult) s.readObject());
            Object val = s.readObject();
            put(key, val);
            //DEBUG
           /// System.out.println(i + ". " + key + "and" + val);
        }
        s.close();
    }
}
