package novello;

import junit.framework.TestCase;
import net.sf.xapp.marshalling.Unmarshaller;
import net.sf.xapp.objectmodelling.api.ClassDatabase;
import net.sf.xapp.objectmodelling.core.ClassModelManager;

/**
 * Book Tester.
 *
 * @author <Authors name>
 * @since <pre>12/23/2009</pre>
 * @version 1.0
 */
public class BookTest extends TestCase 
{
    public BookTest(String name) 
    {
        super(name);
    }

    public void testStep()
    {
        ClassDatabase<Book> cdb = new ClassModelManager<Book>(Book.class);
        Book book = Unmarshaller.load(cdb, Book.class, "classpath:///novello/test-book.xml");

        Content content = cdb.getRootObjMeta().find(Content.class, "workspace/section1/1").getInstance();
        assertNotNull(content);

        /*Content next = book.step(Direction.forward, content);
        assertEquals("workspace/section1/2", next.pathKey());
        next = book.step(Direction.forward, next);
        assertEquals("workspace/section2/1", next.pathKey());
        Content previous = book.step(Direction.back, next);
        assertEquals("workspace/section1/2", previous.pathKey());
        //check it will step back to the beginning
        next = book.step(Direction.forward, next);
        assertEquals("workspace/section1/1", next.pathKey());
        next = book.step(Direction.back, next);
        assertEquals("workspace/section2/1", next.pathKey());*/
    }
}
