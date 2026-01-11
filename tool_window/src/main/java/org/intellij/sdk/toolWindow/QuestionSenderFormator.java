package org.intellij.sdk.toolWindow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class QuestionSenderFormator {
    private PostClass postClass;
    public QuestionSenderFormator(PostClass postClass) {
        this.postClass = postClass;
    }

    public void send(ArrayList<File> files, String prompt) {

        String FinalPrompt = prompt;

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
        postClass.post(FinalPrompt);

    }
}
