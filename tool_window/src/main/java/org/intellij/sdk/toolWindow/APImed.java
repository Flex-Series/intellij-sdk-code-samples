package org.intellij.sdk.toolWindow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class APImed {
    public static void main(String[] args) {
        //PostClass postClass = new PostClass();


        String tesing = "{\"answer\":\"{\\n  \\\"questions\\\": [\\n    {\\n      \\\"question\\\": \\\"What is the primary purpose of the Sender class in the provided code?\\\",\\n      \\\"options\\\": [\\n        \\\"To handle user authentication\\\",\\n        \\\"To manage network communication and send messages\\\",\\n        \\\"To store user information\\\",\\n        \\\"To handle database connections\\\"\\n      ],\\n      \\\"correct_answer\\\": \\\"To manage network communication and send messages\\\"\\n    },\\n    {\\n      \\\"question\\\": \\\"Which of the following methods is used to send a regular message with the username?\\\",\\n      \\\"options\\\": [\\n        \\\"sendMessageTimed()\\\",\\n        \\\"sendMessage()\\\",\\n        \\\"updateUsername()\\\",\\n        \\\"updateCurrentStatus()\\\"\\n      ],\\n      \\\"correct_answer\\\": \\\"sendMessage()\\\"\\n    },\\n    {\\n      \\\"question\\\": \\\"What does the 'lock' variable in the Sender class prevent?\\\",\\n      \\\"options\\\": [\\n        \\\"Multiple threads from accessing shared resources simultaneously\\\",\\n        \\\"Socket connections from being closed\\\",\\n        \\\"Usernames from being changed\\\",\\n        \\\"Messages from being sent\\\"\\n      ],\\n      \\\"correct_answer\\\": \\\"Multiple threads from accessing shared resources simultaneously\\\"\\n    },\\n    {\\n      \\\"question\\\": \\\"Which method is used to close the socket connection?\\\",\\n      \\\"options\\\": [\\n        \\\"closeSocket()\\\",\\n        \\\"run()\\\",\\n        \\\"sendMessage()\\\",\\n        \\\"setUsername()\\\"\\n      ],\\n      \\\"correct_answer\\\": \\\"closeSocket()\\\"\\n    },\\n    {\\n      \\\"question\\\": \\\"What is the purpose of the synchronized block in the run() method?\\\",\\n      \\\"options\\\": [\\n        \\\"To ensure thread safety when initializing the PrintWriter\\\",\\n        \\\"To prevent multiple users from logging in\\\",\\n        \\\"To close the socket connection\\\",\\n        \\\"To update the user status\\\"\\n      ],\\n      \\\"correct_answer\\\": \\\"To ensure thread safety when initializing the PrintWriter\\\"\\n    },\\n    {\\n      \\\"question\\\": \\\"What is the delimiter used to separate the message and username in the sendMessage() method?\\\",\\n      \\\"options\\\": [\\n        \\\"++\\\",\\n        \\\"@@\\\",\\n        \\\"\\u2058\\\",\\n        \\\"::\\\"\\n      ],\\n      \\\"correct_answer\\\": \\\"@@\\\"\\n    },\\n    {\\n      \\\"question\\\": \\\"What happens in the run() method when myUsername is an empty string?\\\",\\n      \\\"options\\\": [\\n        \\\"The loop exits immediately\\\",\\n        \\\"The thread sleeps for 100 milliseconds\\\",\\n        \\\"The socket is closed\\\",\\n        \\\"An exception is thrown\\\"\\n      ],\\n      \\\"correct_answer\\\": \\\"The thread sleeps for 100 milliseconds\\\"\\n    },\\n    {\\n      \\\"question\\\": \\\"Which method is used to send a message with a time parameter?\\\",\\n      \\\"options\\\": [\\n        \\\"sendMessage()\\\",\\n        \\\"sendMessageTimed()\\\",\\n        \\\"updateCurrentStatus()\\\",\\n        \\\"viewUsers()\\\"\\n      ],\\n      \\\"correct_answer\\\": \\\"sendMessageTimed()\\\"\\n    }\\n  ]\\n}\"}\n";
        JSONObject obj = new JSONObject(tesing);
        String obj2 = obj.getString("answer");

        JSONObject obj3 = new JSONObject(obj2);

        JSONArray jsonArray = obj3.getJSONArray("questions");

        System.out.println(obj3);
        System.out.println(jsonArray);

        ArrayList<MultipleChoiceQuestion> multipleChoiceQuestions = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject questionObj = jsonArray.getJSONObject(i);

            String question = questionObj.getString("question");
            JSONArray options = questionObj.getJSONArray("options");
            String correctAnswer = questionObj.getString("correct_answer");

            System.out.println((i + 1) + ". " + question);

            for (int j = 0; j < options.length(); j++) {
                System.out.println("   " + (char)('A' + j) + ". " + options.getString(j));
            }

            System.out.println("Correct: " + correctAnswer);
            System.out.println();
        }

        //System.out.println(postClass.post("testing", QuestionType.MCQ));
    }
}
