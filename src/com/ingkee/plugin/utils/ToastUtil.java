package com.ingkee.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.*;

/**
 * Created by YangLang on 2017/11/25.
 */
public class ToastUtil {

    public static void make(Project project, JComponent jComponent, MessageType type, String text) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(jComponent), Balloon.Position.above);
    }


    public static void make(Project project, MessageType type, String text) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        make(project, statusBar.getComponent(), type, text);
    }
}
