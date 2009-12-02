/*
 *
 * Date: 2009-dec-02
 * Author: davidw
 *
 */
package novello.undo;

import novello.TextChunk;

public class UndoManager implements EditorListener
{
    private EditorListener m_state;
    private BufferedUndoRedoHandler m_bufferedUndoRedoHandler;
    private NullEditorListener m_nullEditorListener;

    public UndoManager()
    {
        m_nullEditorListener = new NullEditorListener();
        m_bufferedUndoRedoHandler = new BufferedUndoRedoHandler(new DefaultUndoRedoHandler());
        m_bufferedUndoRedoHandler.init();
        m_state = m_nullEditorListener;
    }

    public void enable()
    {
        m_state = m_bufferedUndoRedoHandler;
    }

    public void disable()
    {
        m_state = m_nullEditorListener;
    }

    public void textAdded(int offs, String newText)
    {
        m_state.textAdded(offs, newText);
    }

    public void textRemoved(int offs, String removedText)
    {
        m_state.textRemoved(offs, removedText);
    }

    public void setSource(TextChunk textChunk)
    {
        m_bufferedUndoRedoHandler.setSource(textChunk);
    }
}
