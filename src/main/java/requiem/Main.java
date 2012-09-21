package requiem;

import com.xapp.application.api.Launcher;

/**
 * Encapsulates ...
 */
public class Main {
    public static void main(String[] args) {
        Launcher.run(Backlog.class, new RequiemApp(), "backlog.xml");
    }
}
