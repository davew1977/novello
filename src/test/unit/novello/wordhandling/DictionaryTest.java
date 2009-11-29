package novello.wordhandling;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * Dictionary Tester.
 *
 * @author <Authors name>
 * @since <pre>11/20/2009</pre>
 * @version 1.0
 */
public class DictionaryTest extends TestCase 
{
    public DictionaryTest(String name) 
    {
        super(name);
    }

    public void testAdd()
    {
        Dictionary dict = new Dictionary();
        String words = "these,are,some,common,there,words,i,wish,it,was,easy,philistine,this,the,to,tim,that,that's";
        dict.addWords(words.split(","));


        assertEquals("[that, that's, the, there, these, this, tim, to]", dict.findWords("t").toString());
        assertEquals("[that, that's, the, there, these, this]", dict.findWords("th").toString());

        dict.addWords("thespian","thorough","through","thought","think");

        assertEquals(10, dict.findWords("th").size());
        assertEquals(5, dict.findWords("th", 5).size());

    }
}
