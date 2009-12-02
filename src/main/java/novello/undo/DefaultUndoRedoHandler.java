/*
 *
 * Date: 2009-nov-30
 * Author: davidw
 *
 */
package novello.undo;

import java.util.List;
import java.util.ArrayList;

public class DefaultUndoRedoHandler implements UndoRedoHandler
{
    private int m_pointer = 0;
    private List<Update> m_updates;

    public DefaultUndoRedoHandler()
    {
        m_updates = new ArrayList<Update>();
    }

    public void handleUpdates(List<Update> merged)
    {
        if(!canRedo()) //no undos done
        {
            m_updates.addAll(merged);
            m_pointer = m_updates.size()-1;
        }
        else
        {
            m_updates = m_updates.subList(0, m_pointer);
        }
    }

    public boolean canUndo()
    {
        return m_pointer>0;
    }

    public boolean canRedo()
    {
        return m_pointer<m_updates.size()-1;
    }

    public Update pullUndo()
    {
        return m_updates.get(m_pointer--);
    }

    public Update pullRedo()
    {
        return m_updates.get(m_pointer++);
    }
}
