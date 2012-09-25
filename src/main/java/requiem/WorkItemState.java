package requiem;

/**
 * Encapsulates ...
 */
public enum WorkItemState
{
    DONE,TODO,STALLED;

    public WorkItemState next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
