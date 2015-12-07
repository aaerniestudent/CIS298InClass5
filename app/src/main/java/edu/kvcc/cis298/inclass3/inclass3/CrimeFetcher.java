package edu.kvcc.cis298.inclass3.inclass3;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aaernie7528 on 12/7/2015.
 */
public class CrimeFetcher {

    private static final String TAG = "CRIMEFETCHER";

    //method to get the raw bytes from the source. conversion from bytes
    //to something more meaningful will happen in a different method.
    //the method has one parameter that is the URL that we want to connect to.
    private byte[] getUrlBytes(String urlSpec) throws IOException {
        //create a new URL object from the url string that was passed in.
        URL url = new URL(urlSpec);
        //create a new http connection to the specified URL.
        //if we were to load data from a secure site, it would be HttpsURLConnection.
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            //create an output stream to hold that data read from the URL source
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //create an input stream from the HTTP connection
            InputStream in = connection.getInputStream();

            //check to see what the response code from the http request is
            //200, which is the same as http_ok. Every web request will
            //return some sort of response code. you can google them.
            //Typically 200s = goods, 300s = cache, 400s = error, 500 = server error.
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            //create an int to hold how many bytes were read in.
            int bytesRead = 0;
            //create a byte array to act as a buffer that will read in
            //up to 1024 bytes at a time
            byte[] buffer = new byte[1024];

            //while we can read bytes from the input stream
            while ((bytesRead = in.read(buffer)) > 0) {
                //write out the bytes to the output stream
                out.write(buffer, 0, bytesRead);
            }
            //when everything has been read and written, close output
            out.close();
            //convert the output to a byte array
            return out.toByteArray();
        } finally {
            //make sure the connection to the web is closed.
            connection.disconnect();
        }
    }

    //method to get the string result from the http web address
    //The url bytes representing the data get returned from the
    //getURLBytes method, and are then transformed into a string
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public void fetchCrimes() {
        try {
            String url = Uri.parse("http://barnesbrothers.homeserver.com/crimeapi").buildUpon().build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Recieved JSON: " + jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items; " + ioe);
        }
    }

}
