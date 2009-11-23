/*
 *
 * Date: 2009-nov-20
 * Author: davidw
 *
 */
package novello.wordhandling;

import java.util.*;

public class DictionaryNode
{
    private DictionaryNode m_parent;
    public Map<Character, DictionaryNode> m_children;
    public String m_word;
    public int m_depth;

    public DictionaryNode(DictionaryNode parent)
    {
        m_parent = parent;
        m_depth = parent != null ? parent.depth() + 1 : 0;
    }

    public int depth()
    {
        return m_depth;
    }

    public DictionaryNode childDict(DictionaryNode parent, char c)
    {
        if (m_children == null)
        {
            m_children = new TreeMap<Character, DictionaryNode>();
        }
        DictionaryNode dict = m_children.get(c);
        if (dict == null)
        {
            dict = new DictionaryNode(parent);
            m_children.put(c, dict);
        }
        return dict;
    }

    public List<DictionaryNode> children()
    {
        return new ArrayList<DictionaryNode>(m_children.values());
    }

    public LinkedList<String> words()
    {
        LinkedList<String> words = new LinkedList<String>();
        if(m_word!=null)
        {
            words.add(m_word);
        }
        if (m_children!=null)
        {
            for (DictionaryNode dictionaryNode : m_children.values())
            {
                words.addAll(dictionaryNode.words());
            }
        }
        return words;
    }


    public void findWords(String s, List<String> matches, int max)
    {
        if (depth() < s.length())
        {
            char c = s.charAt(depth());
            DictionaryNode childNode = m_children.get(c);
            if (childNode!=null)
            {
                childNode.findWords(s, matches, max);
            }
        }
        else
        {
            if (m_children!=null)
            {
                for (DictionaryNode dictionaryNode : m_children.values())
                {
                    LinkedList<String> words = dictionaryNode.words();
                    while(!words.isEmpty())
                    {
                        matches.add(words.removeFirst());
                        if(matches.size()==max)
                        {
                            return;
                        }
                    }
                }
            }
        }
    }
}
