package requiem;

import net.sf.xapp.application.api.Launcher;
import net.sf.xapp.utils.svn.SVNFacade;
import net.sf.xapp.utils.svn.SVNKitFacade;
import net.sf.xapp.utils.svn.SvnConfig;

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
