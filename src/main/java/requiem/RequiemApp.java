package requiem;

import com.xapp.application.api.SimpleApplication;
import com.xapp.application.api.SpecialTreeGraphics;
import com.xapp.utils.svn.SVNFacade;
import novello.Direction;
import novello.DocumentApp;
import novello.Section;
import novello.Text;
import novello.TextHolder;
import novello.wordhandling.DictionaryType;

import java.util.List;

/**
 * Encapsulates ...
 */
public class RequiemApp extends DocumentApp<Backlog> {

    public RequiemApp(SVNFacade svnFacade) {
        super(svnFacade);
    }

    @Override
    public SpecialTreeGraphics createSpecialTreeGraphics() {
        return new RequiemTreeGraphics();
    }

    @Override
    public Section getDocTree() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void doSplit(Text pChunk) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object step(Direction pType, TextHolder pParentContent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TextHolder stepCircular(Direction pType, TextHolder textHolder) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TextHolder getTextHolder(Text text) {
        return (TextHolder) text;
    }
}
