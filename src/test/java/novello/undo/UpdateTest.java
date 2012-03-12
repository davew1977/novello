package novello.undo;

import junit.framework.TestCase;

/**
 * Update Tester.
 *
 * @author <Authors name>
 * @since <pre>12/01/2009</pre>
 * @version 1.0
 */
public class UpdateTest extends TestCase 
{
    public UpdateTest(String name) 
    {
        super(name);
    }

    public void testFollowsOnFrom()
    {
        //2 adds that do follow on
        Update u1 = new AddUpdate(6,"hello");
        Update u2 = new AddUpdate(11,"hello");
        assertTrue(u2.followsOnFrom(u1));
        //2 adds that don't follow on
        u1 = new AddUpdate(6,"hello");
        u2 = new AddUpdate(10,"hello");
        assertFalse(u2.followsOnFrom(u1));
        //2 adds that don't follow on
        u1 = new AddUpdate(6,"hello");
        u2 = new AddUpdate(12,"hello");
        assertFalse(u2.followsOnFrom(u1));
        //2 adds that don't follow on
        u1 = new AddUpdate(6,"hello");
        u2 = new RemoveUpdate(6,"thisi");
        assertFalse(u2.followsOnFrom(u1));
        //1 add, 1 remove they never follow on
        u1 = new AddUpdate(6,"hello");
        u2 = new RemoveUpdate(6,"jdkjfd");
        assertFalse(u2.followsOnFrom(u1));
        //2 removes that do follow on
        u1 = new RemoveUpdate(12,"kk");
        u2 = new RemoveUpdate(10,"kk");
        assertTrue(u2.followsOnFrom(u1));
        //2 removes that don't follow on
        u1 = new RemoveUpdate(12,"dkd");
        u2 = new RemoveUpdate(10,"sjsjs");
        assertFalse(u2.followsOnFrom(u1));

        //2 removes that do follow on
        u1 = new RemoveUpdate(12,"w");
        u2 = new RemoveUpdate(11,"w");
        Update u3 = new RemoveUpdate(10,"w");
        assertTrue(u2.followsOnFrom(u1));
        assertTrue(u3.followsOnFrom(u2));
    }

    public void testMerge()
    {
        Update u1 = new AddUpdate(6,"hello");
        Update u2 = new AddUpdate(11,"hello");
        u1.merge(u2);
        assertEquals("hellohello",u1.m_text);
        assertEquals(6,u1.m_offs);
        
        u1 = new RemoveUpdate(12,"jj");
        u2 = new RemoveUpdate(10,"dd");
        u1.merge(u2);
        assertEquals(4,u1.length());
        assertEquals(10,u1.m_offs);
        assertEquals("ddjj",u1.m_text);

        u1 = new RemoveUpdate(26,"s");
        u2 = new RemoveUpdate(25,"w");
        u1.merge(u2);
        assertEquals(2,u1.length());
        assertEquals(25,u1.m_offs);
    }
}
