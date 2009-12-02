/*
 *
 * Date: 2009-dec-01
 * Author: davidw
 *
 */
package novello.undo;

import java.util.List;

public class TestUndoRedoHandler implements UndoRedoHandler
{
    public List<Update> m_updates;

    public void handleUpdates(List<Update> updates)
    {
        m_updates = updates;
    }
}
