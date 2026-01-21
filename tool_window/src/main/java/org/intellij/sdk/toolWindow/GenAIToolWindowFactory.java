// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.intellij.sdk.toolWindow;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

final class GenAIToolWindowFactory implements ToolWindowFactory, DumbAware {
  private APImed med;
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    CalendarToolWindowContent toolWindowContent = new CalendarToolWindowContent(toolWindow);
    Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
    toolWindow.getContentManager().addContent(content);
  }

  private static class CalendarToolWindowContent {

    private static final String CALENDAR_ICON_PATH = "/toolWindow/Calendar-icon.png";
    private static final String TIME_ZONE_ICON_PATH = "/toolWindow/Time-zone-icon.png";
    private static final String TIME_ICON_PATH = "/toolWindow/Time-icon.png";

    private final JPanel contentPanel = new JPanel();
    private final JPanel login = new JPanel();
    private final JPanel home = new JPanel();
    private final JPanel questions = new JPanel();
    private final ToolWindow toolWindow;

    private final JTextArea info = new JTextArea();

    private final PostClass postClass = new PostClass();

    public CalendarToolWindowContent(ToolWindow toolWindow) {
      createLoginPanel();
      contentPanel.add(login);
      createHome(toolWindow);
      contentPanel.add(home);
      createQuestionsPanel();
      contentPanel.add(questions);
      this.toolWindow = toolWindow;
    }

    void createQuestionsPanel() {
      questions.setLayout(new BoxLayout(questions, BoxLayout.Y_AXIS));
      questions.setVisible(false);


    }

    void createLoginPanel() {
      //login.setLayout(new GridLayout(4,1,-10,4));
      login.setLayout(new BoxLayout(login, BoxLayout.Y_AXIS));
      JLabel loginstat = new JLabel(" ");
      //JLabel usernameLabel = new JLabel("Username:");
      //JLabel passwordLabel = new JLabel("Password:");
      JTextField username = new JTextField();
      username.setText("Username");
      username.setForeground(Color.GRAY);
      username.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
          if (username.getText().equals("Username")) {
            username.setText("");
            username.setForeground(Color.WHITE);
          }
        }

        @Override
        public void focusLost(FocusEvent e) {
          if (username.getText().isEmpty()) {
            username.setText("Username");
            username.setForeground(Color.GRAY);
          }
        }
      });
      JPasswordField password = new JPasswordField();
      password.setEchoChar((char) 0);
      password.setText("Password");
      password.setForeground(Color.GRAY);
      password.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
          if (String.valueOf(password.getPassword()).equals("Password")) {
            password.setText("");
            password.setForeground(Color.WHITE);
            password.setEchoChar('•');
          }
        }

        @Override
        public void focusLost(FocusEvent e) {
          if (String.valueOf(password.getPassword()).isEmpty()) {
            password.setText("Password");
            password.setForeground(Color.GRAY);
            password.setEchoChar((char) 0);
          }
        }
      });

      Dimension size = new Dimension(200, 35);
      username.setMaximumSize(size);
      password.setMaximumSize(size);
      username.setPreferredSize(size);
      password.setPreferredSize(size);
      username.setMinimumSize(size);
      password.setMinimumSize(size);
      //login.add(usernameLabel);

      login.add(username);
      //login.add(passwordLabel);
      login.add(password);
      login.add(new JLabel(" "));
      JButton loginButton = new JButton("Login");
      System.out.println(loginButton.getHeight());

      loginButton.putClientProperty("JButton.buttonType", "default");
      username.setAlignmentX( Component.LEFT_ALIGNMENT );//0.0
      password.setAlignmentX( Component.LEFT_ALIGNMENT );//0.0
      loginButton.setAlignmentX( Component.LEFT_ALIGNMENT );//0.0
      loginstat.setAlignmentX( Component.LEFT_ALIGNMENT );

      loginButton.setMaximumSize(size);
      loginButton.setPreferredSize(size);
      loginButton.setMinimumSize(size);

      loginstat.setMaximumSize(size);
      loginstat.setPreferredSize(size);
      loginstat.setMinimumSize(size);

      //loginButton.set(new Color(78, 139, 202));
      login.add(loginButton);
      login.add(loginstat);
      loginButton.addActionListener(e -> {
        new Thread(new Runnable() {
          public void run() {
            try {
              boolean res = postClass.login(username.getText(), String.valueOf(password.getPassword()));
              if(res){
                loginstat.setText("✔ Login Successful");
                loginstat.setForeground(JBColor.GREEN);
                EventQueue.invokeAndWait(new Runnable(){
                  public void run() {
                      try {
                          Thread.sleep(1000);
                      } catch (InterruptedException ex) {
                          throw new RuntimeException(ex);
                      }
                      login.setVisible(false);
                      home.setVisible(true);
                  };
                });
              }
              else{
                loginstat.setText("⚠ Login Failed");
                loginstat.setForeground(JBColor.RED);
                System.out.println("⚠ Login failed");
              }
            } catch(Exception e) {
              System.out.println("Error: " + e.getMessage());
            }
          }
        }).start();
      });
    }

    void createHome(ToolWindow toolWindow) {
      home.setVisible(false);
      home.setLayout(new BorderLayout());
      JPanel findpanel = new JPanel();
      findpanel.setPreferredSize(new Dimension(100, 100));
      JButton find = new JButton("Find Project Files");
      find.setMaximumSize(new Dimension(30, 20));
      find.putClientProperty("JButton.buttonType", "default");
      find.addActionListener(e -> {
        new Thread(new Runnable() {
          public void run() {
            try{
              EventQueue.invokeAndWait(new Runnable(){
                public void run() {
            HashMap<File, JCheckBox> fileJCheckBoxHashMap = new HashMap<File, JCheckBox>();
            LinkedHashSet<File> fileSet = new LinkedHashSet<>();

            Container trial = new Container();
            trial.setLayout(new BoxLayout(trial, BoxLayout.Y_AXIS));

            Project project = ProjectManager.getInstance().getOpenProjects()[0];
            String lastmodule = ModuleManager.getInstance(project).getModules()[ModuleManager.getInstance(project).getModules().length - 1].getName();

                for (Module module : ModuleManager.getInstance(project).getModules()) {
                  System.out.println(module);
                  for (VirtualFile root :
                          ModuleRootManager.getInstance(module).getSourceRoots()) {
                    VfsUtilCore.iterateChildrenRecursively(root, null, file -> {
                      String s = file.getPath().replaceFirst(".*?"+lastmodule+"\\s*", "");
                      File temp = new File(file.getPath());
                      if(!s.isEmpty() && !temp.isDirectory() && !fileJCheckBoxHashMap.containsKey(temp)) {
                        System.out.println(lastmodule);
                        System.out.println(s);
                        JPanel panel = new JPanel();
                        panel.setLayout(new BorderLayout());
                        JCheckBox tempCheckBox = new JCheckBox(s);
                        panel.add(tempCheckBox, BorderLayout.WEST);
                        trial.add(panel);
                        fileJCheckBoxHashMap.put(temp, tempCheckBox);
                      }
                      return true;
                    });
                  }
                }
                JBScrollPane listScroller = new JBScrollPane(trial);
                listScroller.setPreferredSize(new Dimension(toolWindow.getComponent().getWidth() -20, 300));
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(listScroller, BorderLayout.CENTER);
                panel.setPreferredSize(new Dimension(toolWindow.getComponent().getWidth() -20, 300));
                home.add(panel, BorderLayout.CENTER);
                JButton questions = new JButton("Generate Questions");
                questions.setMaximumSize(new Dimension(30, 20));
                questions.putClientProperty("JButton.buttonType", "default");
                ArrayList<File> filesforq = new ArrayList<>();
                questions.addActionListener(e -> {
                  for (HashMap.Entry<File, JCheckBox> entry : fileJCheckBoxHashMap.entrySet()) {
                    if(entry.getValue().isSelected()){
                      filesforq.add(entry.getKey());
                    }
                  }
                  if(!filesforq.isEmpty()){
                    System.out.println(filesforq);
                    //home.setVisible(false);
                    //questions.setVisible(true);
                    QuestionSenderFormator questionSenderFormator = new QuestionSenderFormator(postClass);
                    //home.setVisible(false);
                    new Thread(new Runnable() {
                      @Override
                      public void run() {
                        home.setVisible(false);
                        updateQuestionsPanel();
                      }
                    }).start();
                    new Thread(new Runnable() {
                      @Override
                      public void run() {
                          try {
                            ArrayList<Question> questions1 = questionSenderFormator.send(filesforq, "Generate Multiple Choice Questions in json format based on these code files, your response MUST be ONLY in json format", QuestionType.MCQ);

                            updateQuestions(questions1);
                          } catch (Exception e) {
                            e.printStackTrace();
                          }
                      }
                    }).start();
                  }
                });
                home.add(questions, BorderLayout.SOUTH);

              };
            });

          } catch (Exception e) {
            e.printStackTrace();}
          }
        }).start();

        //home.add(list);
      });
      home.add(find, BorderLayout.PAGE_START);





    }

    private void updateQuestionsPanel() {
      JProgressBar progressBar = new JProgressBar(0, 100);
      questions.add(progressBar);
      questions.setVisible(true);
        for(int i = 0; i < 100; i++){
          progressBar.setValue(i);
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
        progressBar.setVisible(false);
    }

    private void updateQuestions(ArrayList<Question> questionsA) {
      Container trial = new Container();
      trial.setLayout(new BoxLayout(trial, BoxLayout.Y_AXIS));
      //final String html = "<html><body style='width: %1spx'>%1s";
      ArrayList<JPanel> panelsA = new ArrayList<>();
      HashMap<String, ArrayList<JCheckBox>> checkboxz = new HashMap<>();
      for (Question question : questionsA) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(" "));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
        //String.format(html, 200, mcq.getQuestion())
        panel.add(new JLabel("<html>" + mcq.getQuestion() + "</html>"));
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

        for (String option : mcq.getOptions()) {
          JCheckBox checkBox = new JCheckBox(option);
          checkBoxes.add(checkBox);
          panel.add(checkBox);
        }

        checkboxz.put(((MultipleChoiceQuestion) question).getQuestion(), checkBoxes);

        for (JCheckBox checkBox : checkBoxes) {
          checkBox.addActionListener(e -> {
            System.out.println("Clicked ");
            if (checkBox.isSelected()) {
              for (JCheckBox checkBox1 : checkBoxes) {
                if (!checkBox1.equals(checkBox)) {
                  checkBox1.setSelected(false);
                }
              }
            }
          });
        }

        panelsA.add(panel);
        trial.add(panel);
      }
      //panel.setPreferredSize(new Dimension(400, 10000));
      JScrollPane questionScroller = new JBScrollPane(trial);
      questionScroller.setPreferredSize(new Dimension(toolWindow.getComponent().getWidth() -20, toolWindow.getComponent().getHeight() - 100));
      questionScroller.setBorder(BorderFactory.createEmptyBorder());
      questions.add(questionScroller, BorderLayout.CENTER);
      JButton questionsSubmit = new JButton("Submit");
      questionsSubmit.addActionListener(e -> {
        boolean submitable = true;
        for (HashMap.Entry<String, ArrayList<JCheckBox>> entry : checkboxz.entrySet()) {
          String ques = entry.getKey();
          ArrayList<JCheckBox> checkBoxes = entry.getValue();
          int unselected = 0;
          for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
              break;
            }else {
              unselected++;
            }
          }
          if (unselected == checkBoxes.size()) {
            submitable = false;
            break;
          }
        }
        System.out.println("submitable: " + submitable);
        if (submitable) {
          int correct = 0;
           for (HashMap.Entry<String, ArrayList<JCheckBox>> entry : checkboxz.entrySet()) {
             String ques = entry.getKey();
             ArrayList<JCheckBox> checkBoxes = entry.getValue();
             String questionss = "";
             String correctOption = "";
             for (Question question : questionsA) {
               MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
               if(ques.equals(mcq.getQuestion())){
                 questionss = mcq.getQuestion();
                 correctOption = mcq.getAnswer();
                 break;
               }
             }
             for (JCheckBox checkBox : checkBoxes) {
               if (checkBox.isSelected() && checkBox.getText().equals(correctOption)) {
                 correct++;
                 checkBox.setText(checkBox.getText() + "✔");
             }
               String cor = "tick";

              /*
             for (JCheckBox checkBox1 : checkBoxes) {
               if (checkBox1.getText().equals(correctOption)) {
                 checkBox1.setText(checkBox.getText() + "✔");
               }else{
                 checkBox1.setText(checkBox.getText() + "✖");
               }
             }*/
             }
           }

           questionScroller.setEnabled(false);


           JLabel label = new JLabel("Score:" + correct + " / " + questionsA.size());
           label.setForeground(JBColor.GREEN);
           questions.add(label, BorderLayout.AFTER_LAST_LINE);

          for (HashMap.Entry<String, ArrayList<JCheckBox>> entry : checkboxz.entrySet()) {
            ArrayList<JCheckBox> checkBoxes = entry.getValue();
            for (JCheckBox checkBox : checkBoxes) {
              checkBox.setEnabled(false);
            }
          }
        }

      });
      questions.add(questionsSubmit, BorderLayout.SOUTH);
    }

    @NotNull
    private JPanel createInfoPanel(int width, int height) {

      JPanel panel = new JPanel();
      panel.add(info);
      info.setEditable(false);
      info.setText("placeholder");
      System.out.println("frame width: " + width);
      //info.setMinimumSize(new Dimension(width, height));
      //info.setMaximumSize(new Dimension(width, height));
      //info.setLineWrap(true);
      //panel.setPreferredSize(new Dimension(width, height));
      //panel.setMinimumSize(new Dimension(width, height));
      //panel.setMaximumSize(new Dimension(width, height));
      info.setLineWrap(true);
      panel.add(new JScrollPane(info));
      return panel;
    }


    @NotNull
    private JPanel createControlsPanel(ToolWindow toolWindow) {
      JPanel controlsPanel = new JPanel();
      JButton hideToolWindowButton = new JButton("GenAI Test");
      hideToolWindowButton.addActionListener(e -> {
          try {
              updateText();
          } catch (InterruptedException ex) {
              throw new RuntimeException(ex);
          } catch (InvocationTargetException ex) {
              throw new RuntimeException(ex);
          }
      });// toolWindow.hide(null));
      controlsPanel.add(hideToolWindowButton);
      return controlsPanel;
    }

    private void updateText() throws InterruptedException, InvocationTargetException {
      new Thread(() -> {
        try {
          ArrayList<Question> res = postClass.post("testing", QuestionType.MCQ);
          EventQueue.invokeAndWait(new Runnable(){
            public void run() {
              info.setText(String.valueOf(res));
            };
          });
        } catch(Exception e) {
          System.out.println("Error: " + e.getMessage());
        }
      }).start();

      //info.setText("blep");
    }



    private void showHome(){

    }


    public JPanel getContentPanel() {
      return contentPanel;
    }

  }

}


 /*
 SOME PROJECT STRUCTURE CODE

      Module[] modules = ModuleManager.getInstance(project).getModules();
      for (Module module : modules) {
        String name = module.getName();
        ModuleRootManager rootManager = ModuleRootManager.getInstance(module);

        VirtualFile[] contentRoots = rootManager.getContentRoots();
        VirtualFile[] sourceRoots  = rootManager.getSourceRoots();

        System.out.println(name);
        System.out.println(Arrays.toString(sourceRoots));
        System.out.println(Arrays.toString(contentRoots));
      }*/