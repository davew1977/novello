package requiem;

import net.sf.xapp.annotations.objectmodelling.Transient;

/**
 * a collection of work items
 */
public class Folder extends WorkItem
{
    @Transient
    public WorkItemState getStatus() {
        for (WorkItem tWorkItem : children()) {
           if(tWorkItem.getStatus() != WorkItemState.DONE) {
               return WorkItemState.TODO;
           }
        }
        return WorkItemState.DONE;
    }
}
