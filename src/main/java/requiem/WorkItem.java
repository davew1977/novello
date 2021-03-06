package requiem;

import net.sf.xapp.annotations.application.EditorWidget;
import net.sf.xapp.annotations.application.Mandatory;
import net.sf.xapp.annotations.marshalling.FormattedText;
import net.sf.xapp.annotations.objectmodelling.Reference;
import net.sf.xapp.annotations.objectmodelling.ValidImplementations;
import net.sf.xapp.tree.Tree;
import novello.Text;
import novello.TextHolder;
import novello.widgets.ChunkEditor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: davidw
 * Date: 9/20/12
 */
@ValidImplementations({Task.class, WaitFor.class, Folder.class})
public class WorkItem extends Tree implements Text, TextHolder
{
    private String content;
    private int size;
    private Date dueDate;
    private List<WorkItem> dependencies = new ArrayList<WorkItem>();
    private WorkItemState status;

    public WorkItem() {
        setStatus(WorkItemState.TODO);
    }

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

    /*@ContainsReferences
    public List<WorkItem> getDependencies()
    {
        return dependencies;
    }

    public void setDependencies(List<WorkItem> dependencies)
    {
        this.dependencies = dependencies;
    }*/

    public List<WorkItem> children() {
        return (List) getChildren();
    }

    @Override
    public String text() {
        return getContent();
    }

    @Override
    public Text content() {
        return this;
    }

    @Override
    public void setText(String pValue) {
        setContent(pValue);
    }
}
