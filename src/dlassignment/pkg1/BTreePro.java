/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 *
 * @author dt817
 */

public class BTreePro {
    int buffSize;
    Node root;
    int nodeCounter = 0;
    int track = 0;
    int t = 4; 
    ArrayList<Node> nodes = new ArrayList();
    
    public BTreePro() throws IOException {
        buffSize = 4 + 8 + (2 * t - 1) * 8 + (2 * t) * 8 + 4;
        Node x = new Node();
        x.id = 0;
        x.leaf = true;
        x.numOfKeys = 0;
        diskwrite(x);
        root = x;

    }

    public class Edge {
        Node src;
        Node dst;
        float weight;
    }

    public class Node {
        boolean leaf;
        long children[];
        int numOfKeys;
        long key[];
        long id;
        
        Node() throws IOException {
            children = new long[8];
            key = new long[8];
            numOfKeys = 0;
            id = ++nodeCounter;

        }
    }
        
    public long search(Node x, long key) throws IOException {
        int i = 0; //Index of
        while (i <= x.numOfKeys - 1 && key > x.key[i]) {
            i = i + 1;
        }
        if (i <= x.numOfKeys - 1 && key == x.key[i]) {
            return x.key[i]; 
        } else if (x.leaf) {
            return 0;
        } else {
            Node c = diskread(x.children[i]);
            return search(c, key);
        }
    }

    void splitChild(Node x, int i) throws IOException {
        Node z = new Node();
        Node y = diskread(x.children[i]);

        z.leaf = y.leaf;

        z.numOfKeys = t - 1; //3

        for (int j = 0; j < t - 1; j++) { //0->2=index children[index] shift right 
            z.key[j] = y.key[j + t];
        }

        if (!y.leaf) {
            for (int j = 0; j < t; j++) { //0->4 = index children[index] shift right
                z.children[j] = y.children[j + t];
            }
        }

        y.numOfKeys = t - 1; //3
        x.numOfKeys = x.numOfKeys + 1;

        for (int j = x.numOfKeys - 1; j >= i; j--) { //5->3=index children[index] shift left
            x.children[j + 1] = x.children[j];
        }
        x.children[i + 1] = z.id;

        for (int j = x.numOfKeys - 1; j >= i; j--) { 
            x.key[j + 1] = x.key[j];
        }
        x.key[i] = y.key[t - 1];

        diskwrite(y);
        diskwrite(z);
        diskwrite(x);
    }

    void insert(long key) throws IOException {
        Node r = root;
        if (r.numOfKeys == 2 * t - 1) {
            Node s = new Node();
            root = s;
            s.leaf = false;
            s.numOfKeys = 0;
            s.children[0] = r.id;
            splitChild(s, 0);
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    void insertNonFull(Node x, long key) throws IOException {
        int i = x.numOfKeys - 1;
        if (x.leaf) {
            while (i >= 0 && key < x.key[i]) {
                x.key[i + 1] = x.key[i];
                i = i - 1;
            }
            x.key[i + 1] = key;
            x.numOfKeys = x.numOfKeys + 1;
            diskwrite(x);
        } else {
            while (i >= 0 && key < x.key[i]) {
                i = i - 1;
            }
            i = i + 1;
            Node c = diskread(x.children[i]);
            if (c.numOfKeys == 2 * t - 1) {
                splitChild(x, i);
                if (key > x.key[i]) {
                    i = i + 1;
                }
            }
            c = diskread(x.children[i]);
            insertNonFull(c, key);

        }
    }

    void diskwrite(Node a) throws IOException {
        RandomAccessFile writeFile = new RandomAccessFile("C:\\Users\\dt817\\Desktop\\DL#2\\Cache.dat", "rw");
        FileChannel f = writeFile.getChannel();
        writeFile.seek(a.id * buffSize);
        ByteBuffer b = ByteBuffer.allocate(buffSize);
        b.putLong(a.id);
        b.putInt(a.numOfKeys);
        if (a.leaf) {
            b.putInt(1);
        } else {
            b.putInt(0);
        }
        for (int i = 0; i < a.numOfKeys; i++) {
            b.putLong(a.key[i]);
        }
        if (!a.leaf) {
            for (int i = 0; i <= a.numOfKeys; i++) {
                b.putLong(a.children[i]);
            }
        }
        b.flip();
        f.write(b);
        f.close();
        writeFile.close();
    }

    Node diskread(long pointer) throws IOException {
        Node out = new Node();
        RandomAccessFile readFile = new RandomAccessFile("C:\\Users\\dt817\\Desktop\\DL#2\\Cache.dat", "rw");
        FileChannel f = readFile.getChannel();
        readFile.seek(pointer * buffSize);
        ByteBuffer b = ByteBuffer.allocate(buffSize);
        f.read(b);
        b.rewind();
        out.id = b.getLong();
        out.numOfKeys = b.getInt();
        if (b.getInt() == 1) {
            out.leaf = true;
        } else {
            out.leaf = false;
        }
        for (int i = 0; i < out.numOfKeys; i++) {
            out.key[i] = b.getLong();
        }
        if (!out.leaf) {
            for (int i = 0; i <= out.numOfKeys; i++) {
                out.children[i] = b.getLong();
            }
        }
        f.close();
        readFile.close();
        return out;
    }
    }


