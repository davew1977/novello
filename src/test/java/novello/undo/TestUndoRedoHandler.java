/*
 *
 * Date: 2009-dec-01
 * Author: davidw
 *
 */
package novello.undo;

import java.util.List;

public class TestUndoRedoHandler implements UpdateListener, UndoRedoHandler
{
    public List<Update> m_updates;

    public void updates(List<Update> updates)
    {
        m_updates = updates;
    }

    public boolean canUndo()
    {
        return false;
    }

    public boolean canRedo()
    {
        return false;
    }

    public Update pullUndo()
    {
        return null;
    }

    public Update pullRedo()
    {
        return null;
    }
}
