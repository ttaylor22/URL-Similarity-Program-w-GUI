/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.jsoup.*;

/**
 *
 * @author dt817
 */
public class DLAssignment1 implements Serializable {

    Document document;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    Object fs;
    double val;
    BTreePro urlPointers;
    int counter = 0;
    HashMap<String, ArrayList<EdgeSerialization>> tracker = new HashMap();
    List<Integer> numbers = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
    public HashMap<String, Node> nodes = new HashMap();
    ArrayList<String> dup = new ArrayList();
    
    public HashtablePro urlNamesUpdate = new HashtablePro();
    HashtablePro allTermsCompared;
    FileWriter writer;
    String[] keys;

    public DLAssignment1() throws IOException, ClassNotFoundException {
        this.urlPointers = new BTreePro();
        Collections.shuffle(numbers);
    }

    /**
     * @param urlName
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws java.text.ParseException
     */
    public void addAndReadURL(String urlName) throws IOException, FileNotFoundException, ParseException {
        //Fetches and parses a html file
        document = Jsoup.connect(urlName).get();
        StringBuilder s = new StringBuilder();
        //Finds the element that match "p" with this element as the starting context
        Elements plinks = document.select("p");
        //Loop through every "p" element 
        for (Element link : plinks) {
            //Check if attribute value in this attribute key is not empty
            if (!link.text().isEmpty()) {
                //Add the attribute value contents into the arraylist
                s.append(link.text());
            }
        }
        //Finds the element that match "meta[name]" with this element as the starting context
        Elements metalinks = document.select("meta[name]");
        //Loop through every "p" element 
        for (Element link : metalinks) {
            //Check if attribute value in this attribute key is not empty
            if (!link.text().isEmpty()) {
                //Add the attribute value contents into the arraylist
                s.append(link.text());
            }
        }
        //Initalize the hashtable
        HashtablePro allTerms = new HashtablePro();
        //Fix text structure by removing all special cases and spacing out individual words
        String[] tokenizedTerms = s.toString().replace("[\\W&&[^\\s]]", "").split("\\W+");
        for (String term : tokenizedTerms) {
            if(!"".equals(term)){
            //Check if the word appears in the hashtable
            int m = (int) allTerms.get(term.toLowerCase());
            //If there is no value set m = 1 or if there is a value increment the value by 1
            m = (m == 0) ? 1 : ++m;
            //Place word in the hashtable with its' given value
            allTerms.put(term.toLowerCase(), m);
        }
        }

        //Place the url name and html data into hashtable 
        //8 URL pointers can be stored in a B-Tree
        String input = urlName.toLowerCase();
        long result = (input.hashCode() & 0x7FFFFFFF);
        nodes.put(urlName, new Node(urlName, numbers.get(counter)));
        urlPointers.insert(result);
        urlNamesUpdate.put(result, urlName);
        oos = new ObjectOutputStream(new FileOutputStream("C:\\Users\\dt817\\Desktop\\DL#2\\File" + urlPointers.search(urlPointers.root, result) + ".txt"));
        allTerms.writeObject(oos);
        ++counter;
    }

    public void calculateAllEdges() throws IOException {
        for (Node parent : nodes.values()) {
            connectedNodes(parent);
        }
    }

//    public void insertClusterPoints(String [] urls){
//        f = nodes.keySet();
//        for (String u: urls) {
//            if(f.contains(u)){
//                f.remove(u);
//            }
//        }
//    }
    public void connectedNodes(Node root) throws IOException {
        int maxlinks = 0;
        ArrayList<EdgeSerialization> dest = new ArrayList();
        Document doc = Jsoup.connect(root.key).get();
        Elements linksOnPage = doc.select("a[href]");
        System.out.println("Found (" + linksOnPage.size() + ") links");
        for (Element linkOnPage : linksOnPage) {
            if (nodes.keySet().contains(linkOnPage.attr("abs:href"))) {
                if (!root.key.equals(linkOnPage.attr("abs:href"))) {
                    System.out.println("Parent: " + root.key + " Child: " + linkOnPage.attr("abs:href"));          
                    dest.add(new EdgeSerialization(root.key, linkOnPage.attr("abs:href"), (1 - checkCSForFiles4(root.key, nodes.get(linkOnPage.attr("abs:href")).key))));
//                    ++maxlinks;
//                    if(maxlinks == 2) break;
                }
            }
        }
        tracker.put(root.key, dest);
        System.out.println("Node completed.");
    }

    public void useAfterCN() {
        for (String e : tracker.keySet()) {
            ArrayList<EdgeSerialization> srcEdges = tracker.get(e);
            for (int i = 0; i < srcEdges.size(); i++) {
                nodes.get(e).edges.add(new Edge(nodes.get(srcEdges.get(i).src), nodes.get(srcEdges.get(i).dest), srcEdges.get(i).weight));              
            }
        }
    }
    
    public void readNodes() throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream("C:\\Users\\dt817\\Desktop\\DL#2\\Nodes.ser"))) {
            nodes = (HashMap<String, Node>) ois2.readObject();
        }
    }

    public void writeNodes() throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream("C:\\Users\\dt817\\Desktop\\DL#2\\Nodes.ser"))) {
            oos2.writeObject(nodes);
        }
    }


    public void readNodesEdgesFile2() throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream("C:\\Users\\dt817\\Desktop\\DL#2\\NodesEdgesFile2.ser"))) {
            tracker = (HashMap<String, ArrayList<EdgeSerialization>>) ois2.readObject();
        }
    }

    public void writeNodesEdgesFile2() throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream("C:\\Users\\dt817\\Desktop\\DL#2\\NodesEdgesFile2.ser"))) {
            oos2.writeObject(tracker);
        }
    }


    //Cosine Similarity method 
    private double getCosineSim(final HashtablePro leftV, final HashtablePro rightV) {
        double magLeftV = 0.0, magRightV = 0.0, similarity;
        long dot = 0;
        for (int i = 0; i < leftV.size(); i++) {
            //Checks if the value does not equal 0
            if ((int) leftV.get(i) != 0) {
                //Magnitude all values to the power of 2 
                magLeftV += Math.pow((int) leftV.get(i), 2);
                for (int j = 0; j < rightV.size(); j++) {
                    //Checks if the values are not 0 and indexes are the same
                    if (((int) rightV.get(j) != 0 && (int) leftV.get(i) != 0) && i == j) {
                        //Multiple the two values
                        dot += (int) leftV.get(i) * (int) rightV.get(j); //Use this for clustering
                    }
                }
            }
        }
        for (int i = 0; i < rightV.size(); i++) {
            //Checks if the value does not equal 0
            if ((int) rightV.get(i) != 0) {
                //Magnitude all values to the power of 2 
                magRightV += Math.pow((int) rightV.get(i), 2);
            }
        }
        //Checks if the magnitude of either hashtable is less than or equal to 0.0
        if (magLeftV <= 0.0 || magRightV <= 0.0) {
            similarity = 0.0;
        } else {
            similarity = dot / (Math.sqrt(magLeftV) * Math.sqrt(magRightV));
        }
        //  cos2DVector[cosNum++] = new double[] {Math.sqrt(magLeftV),Math.sqrt(magRightV)};
        return similarity;
    }

    //Compares each url to the given inputted url for similarity
    public void compare(String urlname) throws IOException, ClassNotFoundException {
        //Fetches and parses a html file
        document = Jsoup.connect(urlname).get();
        StringBuilder s = new StringBuilder();
        //Finds the element that match "p" with this element as the starting context
        Elements plinks = document.select("p");
        //Loop through every "p" element 
        for (Element link : plinks) {
            //Check if attribute value in this attribute key is not empty
            if (!link.text().isEmpty()) {
                //Add the attribute value contents into the arraylist
                s.append(link.text());
            }
        }
        //Finds the element that match "meta[name]" with this element as the starting context
        Elements metalinks = document.select("meta[name]");
        //Loop through every "p" element 
        for (Element link : metalinks) {
            //Check if attribute value in this attribute key is not empty
            if (!link.text().isEmpty()) {
                //Add the attribute value contents into the arraylist
                s.append(link.text());
            }
        }
        allTermsCompared = new HashtablePro();
        //Fix text structure by removing all special cases and spacing out individual words
        String[] tokenizedTerms = s.toString().replace("[\\W&&[^\\s]]", "").split("\\W+");
        for (String term : tokenizedTerms) {
              if(!"".equals(term)){
            //Check if the word appears in the hashtable 
            Integer m = (Integer) allTermsCompared.get(term);
            //If there is no value set m = 1 or if there is a value increment the value by 1
            m = (m == null) ? 1 : ++m;
            //Place word in the hashtable with its' given value
            allTermsCompared.put(term.toLowerCase(), m);
        }
        }
        checkCSForFiles();
    }

    //Compares each url to the given inputted url for similarity
    public HashMap<String, Double> compare4(String urlname) throws IOException, ClassNotFoundException {
        //Fetches and parses a html file
        document = Jsoup.connect(urlname).get();
        StringBuilder s = new StringBuilder();
        //Finds the element that match "p" with this element as the starting context
        Elements plinks = document.select("p");
        //Loop through every "p" element 
        for (Element link : plinks) {
            //Check if attribute value in this attribute key is not empty
            if (!link.text().isEmpty()) {
                //Add the attribute value contents into the arraylist
                s.append(link.text());
            }
        }
        //Finds the element that match "meta[name]" with this element as the starting context
        Elements metalinks = document.select("meta[name]");
        //Loop through every "p" element 
        for (Element link : metalinks) {
            //Check if attribute value in this attribute key is not empty
            if (!link.text().isEmpty()) {
                //Add the attribute value contents into the arraylist
                s.append(link.text());
            }
        }
        allTermsCompared = new HashtablePro();
        //Fix text structure by removing all special cases and spacing out individual words
        String[] tokenizedTerms = s.toString().replace("[\\W&&[^\\s]]", "").split("\\W+");
        for (String term : tokenizedTerms) {
            //Check if the word appears in the hashtable 
            Integer m = (Integer) allTermsCompared.get(term);
            //If there is no value set m = 1 or if there is a value increment the value by 1
            m = (m == null) ? 1 : ++m;
            //Place word in the hashtable with its' given value
            allTermsCompared.put(term.toLowerCase(), m);
        }
        return checkCSForFiles4(urlname);
    }

    public void checkCSForFiles() throws FileNotFoundException, IOException {
        double filecount = 0;
        int i = 2;
        for (Object value : urlNamesUpdate.getKeys()) {
            if (value != null && i != 999) {
                ois = new ObjectInputStream(new FileInputStream("C:\\Users\\dt817\\Desktop\\DL#2\\File" + urlPointers.search(urlPointers.root, Long.parseLong(value.toString())) + ".txt"));
                HashtablePro allTerms = new HashtablePro();
                try {
                    allTerms.readObject(ois);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DLAssignment1.class.getName()).log(Level.SEVERE, null, ex);
                }

                double d = getCosineSim(allTerms, allTermsCompared);
                if (d >= filecount) {
                    filecount = d;

                    fs = urlNamesUpdate.get(value);
                    val = d;
                }
                ++i;
            }
        }
    }

    //Compares each url to the given inputted url for similarity
    public HashMap<String, Double> compare3(String urlname) throws IOException, ClassNotFoundException {
        //Fetches and parses a html file
        document = Jsoup.connect(urlname).get();
        StringBuilder s = new StringBuilder();
        //Finds the element that match "p" with this element as the starting context
        Elements plinks = document.select("p");
        //Loop through every "p" element 
        for (Element link : plinks) {
            //Check if attribute value in this attribute key is not empty
            if (!link.text().isEmpty()) {
                //Add the attribute value contents into the arraylist
                s.append(link.text());
            }
        }
        //Finds the element that match "meta[name]" with this element as the starting context
        Elements metalinks = document.select("meta[name]");
        //Loop through every "p" element 
        for (Element link : metalinks) {
            //Check if attribute value in this attribute key is not empty
            if (!link.text().isEmpty()) {
                //Add the attribute value contents into the arraylist
                s.append(link.text());
            }
        }
        allTermsCompared = new HashtablePro();
        //Fix text structure by removing all special cases and spacing out individual words
        String[] tokenizedTerms = s.toString().replace("[\\W&&[^\\s]]", "").split("\\W+");
        for (String term : tokenizedTerms) {
            //Check if the word appears in the hashtable 
            Integer m = (Integer) allTermsCompared.get(term);
            //If there is no value set m = 1 or if there is a value increment the value by 1
            m = (m == null) ? 1 : ++m;
            //Place word in the hashtable with its' given value
            allTermsCompared.put(term.toLowerCase(), m);
        }
        return checkCSForFiles3(urlname);
    }

// 

    

    public double checkCSForFiles4(String urlname, String urlname2) throws FileNotFoundException, IOException {
        double cosinesum;
        int i = 1;
        HashtablePro allTerms1 = new HashtablePro();
        HashtablePro allTerms2 = new HashtablePro();
        for (Object value : urlNamesUpdate.getKeys()) {
            String filenm = (String) urlNamesUpdate.get(value);
            if (value != null && i != 999 && filenm.equals(urlname)) {
                ois = new ObjectInputStream(new FileInputStream("C:\\Users\\dt817\\Desktop\\DL#2\\File" + Long.parseLong(value.toString()) + ".txt"));
                try {
                    allTerms1.readObject(ois);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DLAssignment1.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
        for (Object value : urlNamesUpdate.getKeys()) {
            String filenm = (String) urlNamesUpdate.get(value);
            if (value != null && i != 999 && filenm.equals(urlname2)) {
                ois = new ObjectInputStream(new FileInputStream("C:\\Users\\dt817\\Desktop\\DL#2\\File" + Long.parseLong(value.toString()) + ".txt"));
                try {
                    allTerms2.readObject(ois);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DLAssignment1.class.getName()).log(Level.SEVERE, null, ex);
                }
                cosinesum = getCosineSim(allTerms1, allTerms2);
                return cosinesum;
            }
        }
        return 0;
    }

    public HashMap<String, Double> checkCSForFiles3(String urlname) throws FileNotFoundException, IOException {
        double cosineSumOfAll = 0;
        int i = 1;
        HashMap<String, Double> s = new HashMap();
        for (Object value : urlNamesUpdate.getKeys()) {
            if (value != null && i != 999) {
                ois = new ObjectInputStream(new FileInputStream("C:\\Users\\dt817\\Desktop\\DL#2\\File" + urlPointers.search(urlPointers.root, Long.parseLong(value.toString())) + ".txt"));
                HashtablePro allTerms = new HashtablePro();
                try {
                    allTerms.readObject(ois);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DLAssignment1.class.getName()).log(Level.SEVERE, null, ex);
                }
                cosineSumOfAll = +getCosineSim(allTerms, allTermsCompared);
                ++i;
            }
        }
        s.put(urlname, cosineSumOfAll);
        return s;
    }

    //URL cosine value for each comparsion.
    public HashMap<String, Double> checkCSForFiles4(String urlname) throws FileNotFoundException, IOException {
        int i = 1;
        HashMap<String, Double> s = new HashMap();
        for (Object value : urlNamesUpdate.getKeys()) {
            if (value != null && i != 999) {
                ois = new ObjectInputStream(new FileInputStream("C:\\Users\\dt817\\Desktop\\DL#2\\File" + urlPointers.search(urlPointers.root, Long.parseLong(value.toString())) + ".txt"));
                HashtablePro allTerms = new HashtablePro();
                try {
                    allTerms.readObject(ois);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DLAssignment1.class.getName()).log(Level.SEVERE, null, ex);
                }
                s.put(urlNamesUpdate.get(value).toString(), getCosineSim(allTerms, allTermsCompared));
            }
        }
        return s;
    }

    public ArrayList getListSim() throws FileNotFoundException, IOException, ClassNotFoundException {
        ArrayList n = new ArrayList<>();
        int i = 0;
        for (Object value : urlNamesUpdate.getKeys()) {
            if (i == 5) {
                break;
            } else if (value != null) {
                ois = new ObjectInputStream(new FileInputStream("C:\\Users\\dt817\\Desktop\\DL#2\\File" + urlPointers.search(urlPointers.root, Long.parseLong(value.toString())) + ".txt"));
                HashtablePro allTerms = new HashtablePro();
                try {
                    allTerms.readObject(ois);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DLAssignment1.class.getName()).log(Level.SEVERE, null, ex);
                }
                n.add(getCosineSim(allTerms, allTermsCompared));
                ++i;
            }
        }
        return n;
    }

    public Object getClosestSource() {
        return fs;
    }

    public double getClosestSourceValue() {
        return val;
    }

//    public int getPosition() {
//        return position;
//    }
    public ArrayList getAllInPosition() throws IOException, ClassNotFoundException {
        ArrayList n = new ArrayList<>();
        int i = 0;
        for (Object value : urlNamesUpdate.getKeys()) {
            if (i == 5) {
                break;
            } else if (value != null) {
                ois = new ObjectInputStream(new FileInputStream("C:\\Users\\dt817\\Desktop\\DL#2\\File" + urlPointers.search(urlPointers.root, Long.parseLong(value.toString())) + ".txt"));
                n.add(urlNamesUpdate.get(value));
                ++i;
            }
        }
        return n;
    }

    public void readCachefile() throws IOException {
        urlPointers.diskread(1);
    }

    public void createFileNamesfile() throws FileNotFoundException, IOException {
        oos = new ObjectOutputStream(new FileOutputStream("C:\\Users\\dt817\\Desktop\\DL#2\\FileNameTracker.txt"));
        urlNamesUpdate.writeObject(oos);
    }

    public void readFileNamesfile() throws IOException, ClassNotFoundException {
        ois = new ObjectInputStream(new FileInputStream("C:\\Users\\dt817\\Desktop\\DL#2\\FileNameTracker.txt"));
        urlNamesUpdate.readObject(ois);
    }

    public void cacheUpdate() throws FileNotFoundException, ParseException, IOException, ClassNotFoundException {
        File dir = new File("C:\\Users\\dt817\\Desktop\\DL#2\\");
        File[] filesInDir = dir.listFiles();
        if (filesInDir != null) {
            for (File fileInDir : filesInDir) {
                Path dirPath = Paths.get(fileInDir.toString());
                BasicFileAttributes attributes = Files.readAttributes(dirPath, BasicFileAttributes.class);
                //Obtain modified date from the file
                long lastModifiedFile = attributes.lastModifiedTime().toMillis();
                //Obtain file path to current file
                File file = new File("C:\\Users\\dt817\\Desktop\\DL#2\\" + fileInDir.getName());
                if (!"URLS.txt".equals(fileInDir.getName()) && !"FileNameTracker.txt".equals(fileInDir.getName()) && !"Cache.dat".equals(fileInDir.getName())) {
                    String removeTxtText = "\\s*\\b.txt\\b\\s*";
                    String firstC = fileInDir.getName().substring(4, fileInDir.getName().length());
                    String secondC = firstC.replaceAll(removeTxtText, "");
                    //Obtain file value
                    long trimmed = Long.parseLong(secondC);
                    //Obtain and connect to the url's last modified date
                    String name = (String) urlNamesUpdate.get(trimmed);
                    URLConnection connection = new URL(name).openConnection();
                    long lastModified = connection.getLastModified();
                    try {
                        System.out.println("Checking: " + name);
                        if (lastModified > lastModifiedFile) {
                            System.out.println("Updating URL: " + name);
                            file.delete();
                            //Fetches and parses a html file
                            document = Jsoup.connect(name).get();
                            StringBuilder s = new StringBuilder();
                            //Finds the element that match "p" with this element as the starting context
                            Elements plinks = document.select("p");
                            //Loop through every "p" element 
                            for (Element link : plinks) {
                                //Check if attribute value in this attribute key is not empty
                                if (!link.text().isEmpty()) {
                                    //Add the attribute value contents into the arraylist
                                    s.append(link.text());
                                }
                            }
                            //Finds the element that match "meta[name]" with this element as the starting context
                            Elements metalinks = document.select("meta[name]");
                            //Loop through every "p" element 
                            for (Element link : metalinks) {
                                //Check if attribute value in this attribute key is not empty
                                if (!link.text().isEmpty()) {
                                    //Add the attribute value contents into the arraylist
                                    s.append(link.text());
                                }
                            }
                            //Initalize the hashtable
                            HashtablePro allTerms = new HashtablePro();
                            //Fix text structure by removing all special cases and spacing out individual words
                            String[] tokenizedTerms = s.toString().replace("[\\W&&[^\\s]]", "").split("\\W+");
                            for (String term : tokenizedTerms) {
                                //Check if the word appears in the hashtable
                                int m = (int) allTerms.get(term.toLowerCase());
                                //If there is no value set m = 1 or if there is a value increment the value by 1
                                m = (m == 0) ? 1 : ++m;
                                //Place word in the hashtable with its' given value
                                allTerms.put(term.toLowerCase(), m);
                            }
                            oos = new ObjectOutputStream(new FileOutputStream("C:\\Users\\dt817\\Desktop\\DL#2\\File" + trimmed + ".txt"));
                            allTerms.writeObject(oos);
                        }
                    } catch (NullPointerException f) {
                    }
                }
            }
            System.out.println("All URLs Up-To-Date.");
        }
    }

    //K-mediods function
    public String ClosestCatergory(String url1, String url2, String url3, String url4, String url5, int iterations) throws IOException, ClassNotFoundException {
        double[] values = new double[5];
        keys = new String[5];
        File dir = new File("C:\\Users\\dt817\\Desktop\\DL#2\\");
        String removeTxtText = "\\s*\\b.txt\\b\\s*";
        File[] filesInDir = dir.listFiles();
        int random = (int) (Math.random() * 999 + 0);
        String f = null;

        HashMap<String, Double> CSSumOfFiles1 = compare3(url1);
        HashMap<String, Double> CSSumOfFiles2 = compare3(url2);
        HashMap<String, Double> CSSumOfFiles3 = compare3(url3);
        HashMap<String, Double> CSSumOfFiles4 = compare3(url4);
        HashMap<String, Double> CSSumOfFiles5 = compare3(url5);

        for (Map.Entry<String, Double> s : CSSumOfFiles1.entrySet()) {
            keys[0] = s.getKey();
            values[0] = s.getValue();
            break;
        }
        for (Map.Entry<String, Double> s : CSSumOfFiles2.entrySet()) {
            keys[1] = s.getKey();
            values[1] = s.getValue();
            break;
        }
        for (Map.Entry<String, Double> s : CSSumOfFiles3.entrySet()) {
            keys[2] = s.getKey();
            values[2] = s.getValue();
            break;
        }
        for (Map.Entry<String, Double> s : CSSumOfFiles4.entrySet()) {
            keys[3] = s.getKey();
            values[3] = s.getValue();
            break;
        }
        for (Map.Entry<String, Double> s : CSSumOfFiles5.entrySet()) {
            keys[4] = s.getKey();
            values[4] = s.getValue();
            break;
        }
        s:
        if (true) {
            if (values[0] == getMinValue(values) && iterations != 0) {
                String firstC = filesInDir[random].getName().substring(4, filesInDir[random].getName().length());
                String secondC = firstC.replaceAll(removeTxtText, "");
//                    Obtain file value
                long trimmed = Long.parseLong(secondC);
                url1 = (String) urlNamesUpdate.get(trimmed);
                iterations--;
                f = ClosestCatergory(url1, keys[1], keys[2], keys[3], keys[4], iterations);
                break s;
            } else if (values[1] == getMinValue(values) && iterations != 0) {
                String firstC = filesInDir[random].getName().substring(4, filesInDir[random].getName().length());
                String secondC = firstC.replaceAll(removeTxtText, "");
                //Obtain file value
                long trimmed = Long.parseLong(secondC);
                url2 = (String) urlNamesUpdate.get(trimmed);
                iterations--;
                f = ClosestCatergory(keys[0], url2, keys[2], keys[3], keys[4], iterations);
                break s;
            } else if (values[2] == getMinValue(values) && iterations != 0) {
                String firstC = filesInDir[random].getName().substring(4, filesInDir[random].getName().length());
                String secondC = firstC.replaceAll(removeTxtText, "");
                //Obtain file value
                long trimmed = Long.parseLong(secondC);
                url3 = (String) urlNamesUpdate.get(trimmed);
                iterations--;
                f = ClosestCatergory(keys[0], keys[1], url3, keys[3], keys[4], iterations);
                break s;
            } else if (values[3] == getMinValue(values) && iterations != 0) {
                String firstC = filesInDir[random].getName().substring(4, filesInDir[random].getName().length());
                String secondC = firstC.replaceAll(removeTxtText, "");
                //Obtain file value
                long trimmed = Long.parseLong(secondC);
                url4 = (String) urlNamesUpdate.get(trimmed);
                iterations--;
                f = ClosestCatergory(keys[0], keys[1], keys[2], url4, keys[4], iterations);
                break s;
            } else if (values[4] == getMinValue(values) && iterations != 0) {
                String firstC = filesInDir[random].getName().substring(4, filesInDir[random].getName().length());
                String secondC = firstC.replaceAll(removeTxtText, "");
                //Obtain file value
                long trimmed = Long.parseLong(secondC);
                url5 = (String) urlNamesUpdate.get(trimmed);
                iterations--;
                f = ClosestCatergory(keys[0], keys[1], keys[2], keys[3], url5, iterations);
                break s;
            } else {
                if (values[0] == getMaxValue(values)) {
                    return keys[0];
                } else if (values[1] == getMaxValue(values)) {
                    return keys[1];
                } else if (values[2] == getMaxValue(values)) {
                    return keys[2];
                } else if (values[3] == getMaxValue(values)) {
                    return keys[3];
                } else {
                    return keys[4];
                }
            }
        }
        return f;

    }

    public String[] getKeys() {
        return keys;
    }

    // getting the maximum value
    public static double getMaxValue(double[] array) {
        double maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
            }
        }
        return maxValue;
    }

// getting the miniumum value
    public static double getMinValue(double[] array) {
        double minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }
}
