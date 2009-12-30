/*
 *
 * Date: 2009-dec-29
 * Author: davidw
 *
 */
package novello.utils;

public interface Lookup<K,V>
{
    V lookup(K key);
}
