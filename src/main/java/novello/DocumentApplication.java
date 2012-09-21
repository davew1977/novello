package novello;

import com.xapp.application.api.ApplicationContainer;
import com.xapp.objectmodelling.tree.Tree;
import novello.wordhandling.DictionaryType;

import java.util.List;

/**
 * Encapsulates an application with sophisticated editing
 */
public interface DocumentApplication {
    void setStatusMessage(String message);

    Tree getDocTree();

    void expand(Object node);

    String getCurrentUser();

    void addWordToDict(DictionaryType pDictType, String pWord);

    List<String> getLocalDictionary();

    String getStyleSheet();

    boolean shouldSplit(Text pChunk);

    void doSplit(Text pChunk);

    Object step(Direction pType, TextHolder pParentContent);

    TextHolder stepCircular(Direction pType, TextHolder textHolder);

    void quit();

    ApplicationContainer getAppContainer();

    TextHolder getTextHolder(Text text);
}
