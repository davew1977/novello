package novello.undo;

import junit.framework.TestCase;

/**
 * BufferedUndoRedoHandler Tester.
 *
 * @author <Authors name>
 * @since <pre>12/01/2009</pre>
 * @version 1.0
 */
public class BufferedUndoRedoHandlerTest extends TestCase 
{
    public BufferedUndoRedoHandlerTest(String name) 
    {
        super(name);
    }

    public void testFlush()
    {
        TestUndoRedoHandler undoHandler = new TestUndoRedoHandler();
        BufferedEditorListener buf = new BufferedEditorListener(undoHandler);
        buf.textAdded(5,"hello");
        buf.textAdded(10," are you there?");
        buf.textRemoved(12,"www");
        buf.flush();

        assertEquals(2, undoHandler.m_updates.size());
        assertEquals("hello are you there?", undoHandler.m_updates.get(0).m_text);

        undoHandler.m_updates.clear();
        buf.textRemoved(26,"w");
        buf.textRemoved(25,"w");
        buf.textRemoved(24,"w");
        buf.flush();
        assertEquals(1, undoHandler.m_updates.size());
    }
}
