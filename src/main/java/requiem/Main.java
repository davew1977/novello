package requiem;

import com.xapp.application.api.Launcher;
import com.xapp.utils.svn.SVNFacade;
import com.xapp.utils.svn.SVNKitFacade;
import com.xapp.utils.svn.SvnConfig;

/**
 * Encapsulates ...
 */
public class Main {
    public static void main(String[] args) {
        launch(new SVNKitFacade(new SvnConfig()), "backlog.xml");
    }

    public static void launch(SVNFacade svnFacade, String filename)
    {
        Launcher.run(Backlog.class, new RequiemApp(svnFacade), filename);

    }
}
