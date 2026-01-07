// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.intellij.sdk.toolWindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.batik.util.Platform;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Objects;

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
    private final JLabel currentDate = new JLabel();
    private final JLabel timeZone = new JLabel();
    private final JLabel currentTime = new JLabel();
    private final JTextArea info = new JTextArea();

    public CalendarToolWindowContent(ToolWindow toolWindow) {
      contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      contentPanel.add(createControlsPanel(toolWindow));
      contentPanel.add(createInfoPanel(toolWindow.getComponent().getParent().getWidth(), toolWindow.getComponent().getHeight()));

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
      PostClass postClass = new PostClass();

      new Thread(new Runnable() {
        public void run() {
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
        }
      }).start();

      //info.setText("blep");
    }


    public JPanel getContentPanel() {
      return contentPanel;
    }

  }

}
