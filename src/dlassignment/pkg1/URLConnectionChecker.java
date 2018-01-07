/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author dt817
 */
public class URLConnectionChecker {
    //Checks if the url exist
  public static String exists(String URLName) throws MalformedURLException, IOException {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection conn = (HttpURLConnection) new URL("http://" + URLName).openConnection();
            HttpURLConnection conn1 = (HttpURLConnection) new URL("https://" + URLName).openConnection();
            conn1.setConnectTimeout(500);
            conn.setConnectTimeout(500);
            conn.setRequestMethod("HEAD");
            conn1.setRequestMethod("HEAD");

            if (conn1.getResponseCode() == HttpURLConnection.HTTP_OK) {
                conn1.disconnect();
                conn.disconnect();
                return "https://" + URLName;
            } else if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                conn1.disconnect();
                conn.disconnect();
                return "http://" + URLName;
            } else {
                return "";
            }
        } catch (Exception s) {
            return "";
        }
    }
}
