package requiem;

import com.xapp.application.annotations.EditorWidget;
import com.xapp.application.annotations.Mandatory;
import com.xapp.marshalling.annotations.FormattedText;
import com.xapp.objectmodelling.annotations.ContainsReferences;
import com.xapp.objectmodelling.annotations.Reference;
import com.xapp.objectmodelling.annotations.ValidImplementations;
import com.xapp.objectmodelling.tree.Tree;
import novello.widgets.ChunkEditor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: davidw
 * Date: 9/20/12
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */
@ValidImplementations({Task.class, WaitFor.class, Folder.class})
public class WorkItem extends Tree
{
    private String content;
    private int size;
    private Date dueDate;
    private List<WorkItem> dependencies = new ArrayList<WorkItem>();
    private WorkItemState status;

    @Reference
    public WorkItem getDependency()
    {
        return dependencies.isEmpty() ? null : dependencies.get(0);
    }

    public void setDependency(WorkItem dependency)
    {
        if(dependencies.isEmpty())
        {
            dependencies.add(dependency);
        }
        else
        {
            dependencies.set(0, dependency);

        }
    }

    @FormattedText
    @EditorWidget(ChunkEditor.class)
    @Mandatory
    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public WorkItemState getStatus()
    {
        return status;
    }

    public void setStatus(WorkItemState status)
    {
        this.status = status;
    }

    @ContainsReferences
    public List<WorkItem> getDependencies()
    {
        return dependencies;
    }

    public void setDependencies(List<WorkItem> dependencies)
    {
        this.dependencies = dependencies;
    }
}
