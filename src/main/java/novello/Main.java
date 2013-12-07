/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import net.sf.xapp.application.api.ApplicationContainer;
import net.sf.xapp.application.api.Launcher;
import net.sf.xapp.application.utils.SwingUtils;
import net.sf.xapp.utils.svn.SVNFacade;

import java.awt.*;

public class Main
{
    static
    {
        SwingUtils.DEFAULT_FRAME_ICON = NovelloTreeGraphics.BOOK_ICON;
        SwingUtils.DEFAULT_FONT = Font.decode("Tahoma-11");
    }
    public static void main(String[] args)
    {
        launch(null, args[0]);
    }

    public static void launch(SVNFacade svnFacade, String filename)
    {
        ApplicationContainer applicationContainer = Launcher.run(Book.class, new NovelloApp(svnFacade), filename);
        //mainView.getGuiContext().getClassDatabase().getClassModel(Content.class).restrictProperty("Name");
    }
}
