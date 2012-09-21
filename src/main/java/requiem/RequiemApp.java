package requiem;

import com.xapp.application.api.SimpleApplication;
import com.xapp.application.api.SpecialTreeGraphics;

/**
 * Encapsulates ...
 */
public class RequiemApp extends SimpleApplication<Backlog> {
    @Override
    public SpecialTreeGraphics createSpecialTreeGraphics() {
        return new RequiemTreeGraphics();
    }
}
