package requiem;

import com.xapp.application.api.Launcher;
import com.xapp.objectmodelling.annotations.TreeMeta;
import com.xapp.objectmodelling.tree.Tree;

/**
 */
public class Backlog
{
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
}
