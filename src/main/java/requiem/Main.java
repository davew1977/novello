package requiem;

import com.xapp.application.api.Launcher;
import com.xapp.utils.svn.SVNFacade;

/**
 * Encapsulates ...
 */
public class Main {
    public static void main(String[] args) {
        launch(null, "backlog.xml");
    }

    public static void launch(SVNFacade svnFacade, String filename)
    {
        Launcher.run(Backlog.class, new RequiemApp(svnFacade), filename);

    }
}
