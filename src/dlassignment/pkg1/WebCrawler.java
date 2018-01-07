/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

/**
 *
 * @author dt817
 */
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

    private ArrayList<String> links = new ArrayList<>();
    private int count = 0;
    private int max = 0;
 

    public WebCrawler() {
    }

    /**
     * This performs all the work. It makes an HTTP request, checks the
     * response, and then gathers up all the links on the page. Perform a
     * searchForWord after the successful crawl
     *
     * @param url - The URL to visit
     * @return 
     */
    public void crawl(String url) {
 if (count != 1000 && max != 100) {
                
        if (!links.contains((url)) && !url.contains("%") && !url.contains("@")) {
          
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(500);
                if (connection.getResponseCode() == 200) // 200 is the HTTP OK status code
                // indicating that everything is great.
                {
                    connection.disconnect();

                    // System.out.println("\n**Visiting** Received web page at " + url);
                    try {
                        
                            Document doc = Jsoup.connect(url).get();
                            //
                            System.out.println("limit:" + ++max);
                            //
                            links.add(url);
                            System.out.println(++count + " :URL");
                            Elements linksOnPage = doc.select("a[href]");
                            System.out.println("Found (" + linksOnPage.size() + ") links");
                            for (Element linkOnPage : linksOnPage) {                              
                                    crawl(linkOnPage.attr("abs:href"));
                            }
                        

                    } catch (UnsupportedMimeTypeException d) {

                    }
                }
            } catch (IOException ioe) {
                // We were not successful in our HTTP request
            }
        }
    }
       
    }

    public ArrayList<String> getLinks() {
        return links;
    }
    
   public void setMax(int max){
        this.max = max;
    }

}
