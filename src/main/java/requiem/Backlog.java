package requiem;

import com.xapp.application.api.Launcher;
import com.xapp.objectmodelling.annotations.TreeMeta;
import com.xapp.objectmodelling.tree.Tree;
import novello.Document;

import java.util.List;

/**
 */
public class Backlog implements Document
{
    List<String> localDictionary;
    private Tree work = new Tree();

    public Backlog()
    {
        work = new WorkItem();
        work.setName("Work");
    }

    @TreeMeta(leafTypes = {Task.class, WaitFor.class, Folder.class})
    public Tree getWork()
    {
        return work;
    }

    public void setWork(Tree work)
    {
        this.work = work;
    }

    @Override
    public String getStyleSheet() {
        return null;
    }

    public List<String> getLocalDictionary() {
        return localDictionary;
    }

    public void setLocalDictionary(List<String> pLocalDictionary) {
        localDictionary = pLocalDictionary;
    }
}
