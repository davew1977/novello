package novello;

import net.sf.xapp.application.api.ApplicationContainer;
import net.sf.xapp.tree.Tree;
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

    TextHolder step(Direction pType, TextHolder pParentContent);

    void quit();

    ApplicationContainer getAppContainer();

    TextHolder getTextHolder(Text text);
}
