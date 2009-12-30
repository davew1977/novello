/*
 *
 * Date: 2009-dec-29
 * Author: davidw
 *
 */
package novello.utils;

import java.util.Map;
import java.util.HashMap;

public class CachedLookup<K,V> implements Lookup<K,V>
{
    private Map<K,V> m_cache;
    private Lookup<K,V> m_delegate;

    public CachedLookup(Lookup<K, V> delegate)
    {
        m_delegate = delegate;
        m_cache = new HashMap<K,V>();
    }

    public V lookup(K key)
    {
        V val = m_cache.get(key);
        if(val==null)
        {
            val = m_delegate.lookup(key);
            m_cache.put(key,val);
        }
        return val;
    }
}
