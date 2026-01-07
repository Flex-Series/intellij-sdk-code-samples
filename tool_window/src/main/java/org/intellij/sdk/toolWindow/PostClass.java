package org.intellij.sdk.toolWindow;

// Source - https://stackoverflow.com/a
// Posted by Pitto, modified by community. See post 'Timeline' for change history
// Retrieved 2026-01-05, License - CC BY-SA 4.0

import org.json.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostClass {
    public String post(String request) {
        HttpURLConnection conn = null;
        DataOutputStream os = null;

        try{
            URL url = new URL("http://127.0.0.1:5000/question/"); //important to add the trailing slash after add
            String[] inputData = {"{\"q\": \"" + request + "\"}"};
            for(String input: inputData){
                byte[] postData = input.getBytes(StandardCharsets.UTF_8);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty( "charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(input.length()));
                os = new DataOutputStream(conn.getOutputStream());
                os.write(postData);
                os.flush();

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                String response = "";
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    response += output;
                    //System.out.println(output);
                }
                conn.disconnect();

                //System.out.println(response);
                JSONObject obj = new JSONObject(response);
                String answer = obj.getString("answer");
                System.out.println(answer);
                return answer;

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally
        {
            if(conn != null)
            {
                conn.disconnect();
            }
        }
        return null;
    }

}

