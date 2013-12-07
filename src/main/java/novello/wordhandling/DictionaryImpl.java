/*
 *
 * Date: 2009-nov-20
 * Author: davidw
 *
 */
package novello.wordhandling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DictionaryImpl implements Dictionary
{
    private DictionaryNode m_dict;
    private HashSet<String> m_words;

    public DictionaryImpl()
    {
        m_dict = new DictionaryNode(null);
        m_words = new HashSet<String>();
        m_words.add("a");
        m_words.add("i");
    }


    public void addWords(String... words)
    {
        for (String word : words)
        {
            addWord(word);
        }
    }
    public void addWord(String word)
    {
        word = word.replace('\u2019', '\'');
        m_words.add(word.toLowerCase());
        DictionaryNode dict = m_dict;
        for (int i = 0; i < word.length(); i++)
        {
            char c = word.charAt(i);
            dict = dict.childDict(dict, c);
            if(i==word.length()-1) //last char
            {
                dict.m_word = word;
            }
        }
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        appendDic(sb, m_dict);
        return sb.toString();
    }

    public void appendDic(StringBuilder sb, DictionaryNode d)
    {
        String indent = "";
        for(int i=0;i<d.depth();i++)
        {
            indent+="  ";
        }
        if(d.m_word!=null)
        {
            sb.append(indent).append(d.m_word).append("\n");
        }
        if (d.m_children!=null)
        {
            for (Map.Entry<Character,DictionaryNode> child : d.m_children.entrySet())
            {
                sb.append(indent).append(child.getKey()).append(":\n");
                appendDic(sb, child.getValue());
            }
        }
    }

    public boolean wordOk(String word)
    {
        word = word.toLowerCase();
        word = word.replace('\u2019', '\'');
        return m_words.contains(word.toLowerCase());
    }

    public List<String> findWords(String s)
    {
        return findWords(s, 10);
    }

    public List<String> findWords(String s, int max)
    {
        List<String> matches = new ArrayList<String>();
        m_dict.findWords(s, matches, max);
        return matches;
    }

    public void addWords(List<String> words)
    {
        for (String word : words)
        {
            addWord(word);
        }
    }

    @Override
    public void reset() {

    }
}
