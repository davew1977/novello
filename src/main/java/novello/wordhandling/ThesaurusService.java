/*
 *
 * Date: 2009-dec-15
 * Author: davidw
 *
 */
package novello.wordhandling;

import com.xapp.utils.FileUtils;

import java.util.*;

import novello.utils.Lookup;
import novello.utils.CachedLookup;

public class ThesaurusService implements Lookup<String, Collection<String>>
{
    private Lookup<String, Collection<String>> m_lookup;

    public ThesaurusService()
    {
        m_lookup = new CachedLookup<String, Collection<String>>(new ThesaurusLookup());
    }

    public Collection<String> lookup(String word)
    {
        return m_lookup.lookup(word);
    }

}