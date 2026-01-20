package org.intellij.sdk.toolWindow;

import javax.swing.*;
import java.util.ArrayList;

public class MultipleChoiceQuestion implements Question {
    private final String question;
    private final ArrayList<String> options;
    private final String answer;
    private ArrayList<JCheckBox> checkBoxes;

    public MultipleChoiceQuestion(String question, ArrayList<String> options, String answer) {
        this.question = question;
        this.options = options;
        this.answer = answer;

    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public ArrayList<JCheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public void setCheckBoxes(ArrayList<JCheckBox> checkBoxes) {
        this.checkBoxes = checkBoxes;
    }

    public String getAnswer() {
        return answer;
    }
}
