package org.intellij.sdk.toolWindow;

// Source - https://stackoverflow.com/a
// Posted by Pitto, modified by community. See post 'Timeline' for change history
// Retrieved 2026-01-05, License - CC BY-SA 4.0

import com.google.gson.Gson;
import org.json.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostClass {
    private User user;

    public ArrayList<Question> post(String request, QuestionType type) {
        HttpURLConnection conn = null;
        DataOutputStream os = null;
        try{
            URL url = new URL("http://127.0.0.1:5000/question/");
            Gson gson = new Gson();
            Map<String, String> payload = new HashMap<>();
            payload.put("q", request);
            payload.put("userid", String.valueOf(user.getId()));
            //payload.put("type", String.valueOf(type));
            String inputData = gson.toJson(payload);
            System.out.println(inputData);
            byte[] postData = inputData.getBytes(StandardCharsets.UTF_8);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(inputData.length()));
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
            }
            conn.disconnect();
            JSONObject obj = new JSONObject(response);


            /*
            System.out.println(obj);

            String obj2 = obj.getString("answer");

            JSONObject obj3 = new JSONObject(obj2);

            JSONArray jsonArray = obj3.getJSONArray("questions");

            System.out.println(obj3);
            System.out.println(jsonArray);

            ArrayList<MultipleChoiceQuestion> multipleChoiceQuestions = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                System.out.println(
                        (i + 1) + ". " +
                                jsonArray.getJSONObject(i).getString("question")
                );
                JSONObject oasd = new JSONObject(jsonArray.getJSONObject(i).getJSONObject("question").getString("options"));
                System.out.println(oasd);

                //multipleChoiceQuestions.add(new MultipleChoiceQuestion(jsonArray.getJSONObject(i).getString("question"), ));
            }

            String answer = obj.getString("answer");
            //String question = answer.getString("question");
            //System.out.println(answer);

             */

            String obj2 = obj.getString("answer");

            JSONObject obj3 = new JSONObject(obj2);

            JSONArray jsonArray = obj3.getJSONArray("questions");

            System.out.println(obj3);
            System.out.println(jsonArray);

            ArrayList<Question> multipleChoiceQuestions = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject questionObj = jsonArray.getJSONObject(i);

                String question = questionObj.getString("question");
                JSONArray options = questionObj.getJSONArray("options");
                String correctAnswer = questionObj.getString("correct_answer");

                ArrayList<String> optionsList = new ArrayList<>();

                //System.out.println((i + 1) + ". " + question);

                for (int j = 0; j < options.length(); j++) {
                    optionsList.add(options.getString(j));
                    //System.out.println("   " + (char)('A' + j) + ". " + options.getString(j));
                }

                //System.out.println("Correct: " + correctAnswer);
                //System.out.println();

                multipleChoiceQuestions.add(new MultipleChoiceQuestion(question, optionsList, correctAnswer));
            }

            return multipleChoiceQuestions;
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


    public boolean login(String username, String password) {
        HttpURLConnection conn = null;
        DataOutputStream os = null;

        try{
            URL url = new URL("http://127.0.0.1:5000/login/"); //important to add the trailing slash after add
            String[] inputData = {"{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"};
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
                String answer = obj.getString("valid");
                if(answer.equals("true")){
                    user = new User(obj.getString("username"),obj.getInt("id"));
                }
                System.out.println(answer);
                return answer.equals("true");

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
        return false;
    }

}

