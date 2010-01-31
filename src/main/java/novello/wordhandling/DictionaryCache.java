/*
 *
 * Date: 2010-jan-26
 * Author: davidw
 *
 */
package novello.wordhandling;

import java.util.Set;
import java.util.HashSet;

/**
 * Wrapper to optimise word lookup
 */
public class DictionaryCache implements Dictionary
{
    private Set<String> m_cache;
    private Dictionary m_dict;

    public DictionaryCache(Dictionary dict)
    {
        m_dict = dict;
        m_cache = new HashSet<String>();
    }

    public boolean wordOk(String word)
    {
        if(m_cache.contains(word))
        {
            return true;
        }
        boolean ok = m_dict.wordOk(word);
        if(ok)
        {
            m_cache.add(word);
        }
        return ok;
    }

    public void reset()
    {
        m_cache.clear();
    }
}