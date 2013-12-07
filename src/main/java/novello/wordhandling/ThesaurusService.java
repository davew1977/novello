/*
 *
 * Date: 2009-dec-15
 * Author: davidw
 *
 */
package novello.wordhandling;

import novello.utils.CachedLookup;
import novello.utils.Lookup;

import java.util.Collection;

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