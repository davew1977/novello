package requiem;

/**
 * something we don't have power over that a task is waiting for
 */
public class WaitFor extends WorkItem
{
    public WaitFor() {
        setStatus(WorkItemState.STALLED);
    }
}
