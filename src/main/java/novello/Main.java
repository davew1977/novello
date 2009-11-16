/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.utils.SwingUtils;
import com.xapp.application.api.Launcher;
import com.xapp.application.api.ApplicationContainer;

import javax.swing.*;
import java.awt.*;

public class Main
{
    public static final ImageIcon ICON = new ImageIcon(Main.class.getResource("/book icon.gif"), "");

    public static void main(String[] args)
    {
        SwingUtils.DEFAULT_FRAME_ICON = ICON;
        SwingUtils.DEFAULT_FONT = Font.decode("Dialog-12");
        ApplicationContainer applicationContainer = Launcher.run(Book.class, new NovelloApp(), args[0]);
        //mainView.getGuiContext().getClassDatabase().getClassModel(Content.class).restrictProperty("Name");
        
    }
}
