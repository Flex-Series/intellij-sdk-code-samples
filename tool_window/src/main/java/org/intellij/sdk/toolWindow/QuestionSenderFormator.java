package org.intellij.sdk.toolWindow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class QuestionSenderFormator {
    private final PostClass postClass;
    public QuestionSenderFormator(PostClass postClass) {
        this.postClass = postClass;
    }

    public ArrayList<Question> send(ArrayList<File> files, String prompt, QuestionType questionType) throws IOException {
        String format = "{\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"question\": \"ExampleQuestion\",\n" +
                "      \"options\": [\n" +
                "        \"Answer1\",\n" +
                "        \"Answer2\",\n" +
                "        \"Answer3\",\n" +
                "        \"Answer5\"\n" +
                "      ],\n" +
                "      \"correct_answer\": \"Answer2\"\n" +
                "    }  \n" +
                "  ]\n" +
                "}";
        String FinalPrompt = prompt + ", the response must be in the format of" + format;

        for (File file : files) {
            String content = null;
            try {
                content = Files.readString(file.toPath());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            FinalPrompt+="### New File\n"+content;

            System.out.println(content);
        }
        return postClass.post(FinalPrompt, QuestionType.MCQ);

    }
}
