package requiem;

import com.xapp.application.api.Launcher;
import com.xapp.objectmodelling.annotations.TreeMeta;
import com.xapp.objectmodelling.tree.Tree;

/**
 * Created with IntelliJ IDEA.
 * User: davidw
 * Date: 9/20/12
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
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

    public static void main(String[] args)
    {
        Launcher.run(Backlog.class, "backlog.xml");
    }
}
