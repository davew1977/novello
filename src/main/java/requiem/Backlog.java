package requiem;

import net.sf.xapp.annotations.objectmodelling.TreeMeta;
import net.sf.xapp.objectmodelling.core.Tree;
import novello.Direction;
import novello.Document;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class Backlog implements Document
{
    List<String> localDictionary = new ArrayList<String>();
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
    public String getStyleSheet()
    {
        return null;
    }

    public List<String> getLocalDictionary()
    {
        return localDictionary;
    }

    public void setLocalDictionary(List<String> pLocalDictionary)
    {
        localDictionary = pLocalDictionary;
    }
           //TODO promote this to XAPP
    public WorkItem step(Direction type, WorkItem thisContent)
    {
        WorkItem previous = null;
        WorkItem next = null;
        boolean found = false;
        List<WorkItem> contents = work.enumerate(WorkItem.class);
        for (WorkItem content : contents)
        {
            if (content.equals(thisContent))
            {
                found = true;
            }
            else
            {
                if (found)
                {
                    next = content;
                    break;
                }
                else
                {
                    previous = content;
                }
            }
        }
        return type.equals(Direction.forward) ? next : previous;
    }

}
