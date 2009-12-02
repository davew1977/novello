/*
 *
 * Date: 2009-nov-30
 * Author: davidw
 *
 */
package novello.undo;

import novello.TextChunk;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

public class BufferedUndoRedoHandler implements EditorListener
{
    private TextChunk m_source;
    Timer m_timer;
    private UndoRedoHandler m_delegate;
    List<Update> m_updates;

    public BufferedUndoRedoHandler(UndoRedoHandler undoRedoHandler)
    {
        m_delegate = undoRedoHandler;
        m_updates = new ArrayList<Update>();
    }

    public void init()
    {
        m_timer = new Timer(5000, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                flush();
            }
        });
        m_timer.setRepeats(true);
        m_timer.start();

    }

    protected void flush()
    {
        //merge down the updates
        if (!m_updates.isEmpty())
        {
            List<Update> merged = new ArrayList<Update>();
            Update newUpdate = m_updates.get(0);
            merged.add(newUpdate);
            for (int i = 1; i < m_updates.size(); i++)
            {
                Update update = m_updates.get(i);
                if (update.followsOnFrom(newUpdate))
                {
                    newUpdate.merge(update);
                }
                else
                {
                    newUpdate = update.clone();
                    merged.add(newUpdate);
                }
            }
            m_updates.clear();
            m_delegate.handleUpdates(merged);
        }
    }

    public void setSource(TextChunk textChunk)
    {
        m_source = textChunk;
    }

    public void textAdded(int offs, String newText)
    {
        m_updates.add(new AddUpdate(offs,newText));
    }

    public void textRemoved(int offs, String removedText)
    {
        m_updates.add(new RemoveUpdate(offs, removedText));
    }
}
