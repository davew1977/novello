/*
 *
 * Date: 2009-dec-02
 * Author: davidw
 *
 */
package novello.undo;

import com.xapp.application.editor.widgets.TextEditor;

public class AddUpdate extends Update
{
    public AddUpdate(int offs, String text)
    {
        super(offs, text);
    }

    public boolean followsOnFrom(Update newUpdate)
    {
        return newUpdate instanceof AddUpdate && m_offs == newUpdate.m_offs + newUpdate.length();
    }

    public String toString()
    {
        return "+" + super.toString();
    }

    public void undo(TextEditor textEditor)
    {
        removeFrom(textEditor);
    }

    public void redo(TextEditor textEditor)
    {
        insertInto(textEditor);
    }
}
