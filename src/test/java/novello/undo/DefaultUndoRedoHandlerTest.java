package novello.undo;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

/**
 * DefaultUndoRedoHandler Tester.
 *
 * @author <Authors name>
 * @since <pre>12/02/2009</pre>
 * @version 1.0
 */
public class DefaultUndoRedoHandlerTest extends TestCase 
{
    public DefaultUndoRedoHandlerTest(String name) 
    {
        super(name);
    }

    public void testDefault()
    {
        DefaultUndoRedoHandler u = new DefaultUndoRedoHandler();
        u.updates(createUpdateList("+23,ksksk"));
        assertTrue(u.canUndo());
        assertFalse(u.canRedo());
        
        u = new DefaultUndoRedoHandler();
        assertFalse(u.canUndo());
        assertFalse(u.canRedo());
        u.updates(createUpdateList("+23,hello","+27,boo","-17,AAA","+3,ads"));
        assertTrue(u.canUndo());
        assertFalse(u.canRedo());

        Update update = u.pullUndo();
        assertEquals("ads",update.m_text);
        assertTrue(u.canUndo());
        assertTrue(u.canRedo());
        update = u.pullRedo();
        assertEquals("ads",update.m_text);
        assertTrue(u.canUndo());
        assertFalse(u.canRedo());

        u.pullUndo();
        u.pullUndo();
        update = u.pullUndo();
        assertEquals("boo",update.m_text);

        u.updates(createUpdateList("+1,T","+1,T","+1,T"));
        assertFalse(u.canRedo());
        assertTrue(u.canUndo());
    }

    static List<Update> createUpdateList(String... updates)
    {
        ArrayList<Update> updatesList = new ArrayList<Update>();
        for (String update : updates)
        {
            String[] args = update.substring(1).split(",", 2);
            int offs = Integer.parseInt(args[0]);
            String text = args[1];
            Update u = update.charAt(0)=='+' ? new AddUpdate(offs, text) : new RemoveUpdate(offs, text);
            updatesList.add(u);
        }
        return updatesList;
    }
}
