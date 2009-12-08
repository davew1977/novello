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
import com.xapp.utils.SVNFacade;
import com.xapp.utils.SvnConfig;
import com.xapp.utils.SVNKitFacade;
import com.xapp.application.api.Launcher;

import java.io.File;

public class NovelloLauncher
{
    public static SVNFacade SVN_FACADE;
    public static File HOME_DIR = new File(System.getProperty("user.home", ".") + "/novello");

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
                startupScreen.getDialog().setVisible(false);
                String filename = null;
                if (bookFile instanceof BookFileSVN)
                {
                    BookFileSVN bookFileSVN = (BookFileSVN) bookFile;
                    String username = bookFileSVN.getSvnUsername();
                    String password = bookFileSVN.getSvnPassword();
                    SVN_FACADE = new SVNKitFacade(username, password);
                    String svnloc = bookFileSVN.getLocation();
                    //trim off filename
                    String svnfolder = svnloc.substring(0,svnloc.lastIndexOf("/"));
                    String folder = bookFileSVN.getCheckoutFolder();
                    //make a folder to check out to
                    String leaffolderName = svnfolder.substring(svnfolder.lastIndexOf("/"));
                    System.out.println(leaffolderName);
                    /*SVN_FACADE.checkout(svnfolder , folder);

                    filename = folder + "/" + svnloc.substring(svnloc.lastIndexOf("/"));*/

                }
                else
                {
                    System.out.println("here");
                    filename = bookFile.getLocation();
                }
                Main.main(new String[]{filename});


                launcherData.addRecentlyOpened(bookFile);
                LauncherData.save(launcherData);
            }
        };
        startupScreen.setStartupCallback(callback);
        startupScreen.getDialog().setVisible(true);
    }

}
