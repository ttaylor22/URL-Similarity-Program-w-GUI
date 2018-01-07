/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author dt817
 */
public class HuffmanEncoder implements Serializable{
    
    private static final int APLHABET_SIZE = 256;
    
    //Reads the user's input;
    public HuffmanEncodedResult compress(final String data) {
        //Count the frequncy of the input
        final int[] freq = buildFrequencyTable(data);
        //Creates the tree
        final Node root = buildTree(freq);
        //Builds the code table
        final Map<Character, String> codeTable = new HashMap(); 
        buildCodeTable(root,codeTable, "");
        //Stores the encodedData with it's following string 
        return new HuffmanEncodedResult(generateEncodedData(data, codeTable), root);
    }
    
    //Build the huffman frequency table with its given frequencies
    private static int[] buildFrequencyTable(final String data) {
        final int[] freq = new int[APLHABET_SIZE];
        for (final char character : data.toCharArray()) {
            freq[character]++;
        }
        return freq;
    }
    
    private static Node buildTree(int[] freq) {
        
        final PriorityQueue<Node> pq = new PriorityQueue();
        
        //Create a leaf node for each symbol and add it to the priority queue
        for (char i = 0; i < APLHABET_SIZE; i++) {
            if (freq[i] > 0) {
                pq.add(new Node(i, freq[i], null, null));
            }
        }
        
        //Adds only if there is one character with a nonzero frequency
        if (pq.size() == 1) {
            pq.add(new Node('\0', 1, null, null));
        }
        //
        
        //Merges the two smallest trees
        while (pq.size() > 1) {
            //while there is more than one node in the queue
            //Remove the two nodes of the highest priority from the queue
            final Node left = pq.remove();
            final Node right = pq.remove();
            //Adds a new internal node with the given left and right nodes as the children and the sum of the two node frequencies
            final Node parent = new Node('\0', left.frequency + right.frequency, left, right);
            //Then adds the new node into queue
            pq.add(parent);
        }
        return pq.remove();
    }
    
    //Generates the encoded input using the code table
    private static String generateEncodedData(final String data, final Map<Character, String> codeTable) {
        final StringBuilder builder = new StringBuilder();
        for (final char character : data.toCharArray()) {
            builder.append(codeTable.get(character));
        }
        //Return encoded data from the given map characters on the string
        return builder.toString();
    }
    
    //Returns the decompressed message
    public String decompress(final HuffmanEncodedResult result) {
        final StringBuilder resultBuilder = new StringBuilder();
        //Get the root of tree from compressed input
        Node current = result.getRoot();
        int i = 0;
        //Decodes using the tree
        while (i < result.getEncodedData().length()) {
            while (!current.isLeaf()) {
                char bit = result.getEncodedData().charAt(i);
                switch (bit) {
                    case '1':
                        current = current.rightChild;
                        break;
                    case '0':
                        current = current.leftChild;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid bit in message! " + bit);
                }
                i++;
            }
            //Connect the characters together
            resultBuilder.append(current.character);
            current = result.getRoot();
        }
        //Return the decompressed message
        return resultBuilder.toString();
    }
    
    private static void buildCodeTable(final Node node, final Map<Character, String> codeTable, final String s) {      
        
       if (!node.isLeaf()) {
            buildCodeTable(node.leftChild, codeTable, s + '0');
            buildCodeTable(node.rightChild, codeTable, s + '1');
        } else {
            codeTable.put(node.character, s);
        }
    }
        
        
    
    
    
    
    
    
    static class Node implements Comparable<Node> , Serializable {
        
        private final char character;
        private final int frequency;
        private final Node leftChild;
        private final Node rightChild;
        
        private Node(final char character, final int frequency, final Node leftChild, final Node rightChild) {
            this.character = character;
            this.frequency = frequency;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }
        
        boolean isLeaf() {
            return this.leftChild == null && this.rightChild == null;
        }
        
        @Override
        public int compareTo(final Node that) {
            final int frequencyComparsion = Integer.compare(this.frequency, that.frequency);
            if (frequencyComparsion != 0) {
                return frequencyComparsion;
            }
            return Integer.compare(this.frequency, that.frequency);
        }
    }
    
    static class HuffmanEncodedResult implements Serializable{

        final Node root;
        final String encodedData;
        
        //Stores the encodedData with it's following node
        HuffmanEncodedResult(final String encodedData, final Node root) {
            this.encodedData = encodedData;
            this.root = root;
        }

        public Node getRoot() {
            return this.root;
        }
        
        public String getEncodedData() {
            return this.encodedData;
        }
    }
    
    //Testing purposes only
    /*
    public static void main(String[] args) {
        final String test = "Huffman Encoder works :D!";
        final HuffmanEncoder encoder = new HuffmanEncoder();
        final HuffmanEncodedResult result = encoder.compress(test);
        System.out.println(encoder.decompress(result));
    }
    */
}
