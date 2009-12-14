/*
 *
 * Date: 2009-dec-02
 * Author: davidw
 *
 */
package novello;

import novello.startup.StartupScreen;
import novello.startup.StartupCallback;
import novello.startup.BookFile;
import novello.startup.BookFileSVN;
import com.xapp.utils.svn.SVNFacade;
import com.xapp.utils.svn.SVNKitFacade;
import com.xapp.application.utils.SwingUtils;

import javax.swing.*;
import java.io.File;
import java.awt.*;

public class NovelloLauncher
{
    public static SVNFacade SVN_FACADE;
    public static File HOME_DIR = new File(getHomeDir() + "/novello");

    static
    {
        SwingUtils.DEFAULT_FRAME_ICON = NovelloTreeGraphics.BOOK_ICON;
        SwingUtils.DEFAULT_FONT = Font.decode("Tahoma-11");
    }

    private static String getHomeDir()
    {
        boolean override = Boolean.getBoolean("home.dir.override");

        if(override)
        {
            Object[] items = new Object[]{"C:\\dev\\novello_test\\david","C:\\dev\\novello_test\\bob"};
            JComboBox jcombo = new JComboBox(items);
            JOptionPane.showMessageDialog(null,jcombo);

            return (String) jcombo.getSelectedItem();
        }
        return System.getProperty("user.home", ".");
    }

    public static void main(String[] args)
    {
        HOME_DIR.mkdir();
        System.out.println(HOME_DIR.getAbsolutePath());
        final LauncherData launcherData = LauncherData.load();
        final StartupScreen startupScreen = new StartupScreen(launcherData);
        StartupCallback callback = new StartupCallback()
        {
            public void startNovello(BookFile bookFile)
            {
                String filename = null;
                if (bookFile instanceof BookFileSVN)
                {
                    final BookFileSVN bookFileSVN = (BookFileSVN) bookFile;
                    String username = bookFileSVN.getSvnUsername();
                    String password = bookFileSVN.getSvnPassword();
                    SVN_FACADE = new SVNKitFacade(username, password);
                    JProgressBar prog = new JProgressBar(0,100);
                    prog.setIndeterminate(true);
                    final JFrame jFrame = SwingUtils.showInFrame(prog);
                    jFrame.setLocationRelativeTo(startupScreen.getDialog());
                    new Thread(new Runnable()
                    {
                        public void run()
                        {
                            String svnloc = bookFileSVN.getLocation();
                            //trim off filename
                            String svnfolder = svnloc.substring(0, svnloc.lastIndexOf("/"));
                            String folder = bookFileSVN.getCheckoutFolder();
                            //make a folder to check out to
                            String leaffolderName = svnfolder.substring(svnfolder.lastIndexOf("/"));
                            folder += leaffolderName;
                            SVN_FACADE.checkout(svnfolder, folder);
                            String filename = folder + "/" + svnloc.substring(svnloc.lastIndexOf("/"));
                            startupScreen.getDialog().setVisible(false);
                            jFrame.setVisible(false);
                            Main.launch(SVN_FACADE, filename);
                        }
                    }).start();

                }
                else
                {
                    System.out.println("here");
                    filename = bookFile.getLocation();
                    startupScreen.getDialog().setVisible(false);
                    Main.launch(null, filename);
                }


                launcherData.addRecentlyOpened(bookFile);
                LauncherData.save(launcherData);
            }
        };
        startupScreen.setStartupCallback(callback);
        startupScreen.getDialog().setVisible(true);
    }

}
