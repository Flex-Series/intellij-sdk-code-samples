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

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

final class CalendarToolWindowFactory implements ToolWindowFactory, DumbAware {
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


    private final JTextArea info = new JTextArea();

    private final PostClass postClass = new PostClass();

    public CalendarToolWindowContent(ToolWindow toolWindow) {
      createLoginPanel();
      contentPanel.add(login);
      createHome(toolWindow);
      contentPanel.add(home);
      createQuestionsPanel();
      contentPanel.add(questions);
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
                    questionSenderFormator.send(filesforq, "Generate Multiple Choice Questions in json format based on these code files, your response MUST be ONLY in json format");
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
          String res = postClass.post("testing");
          EventQueue.invokeAndWait(new Runnable(){
            public void run() {
              info.setText(res);
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