/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import com.xapp.objectmodelling.annotations.TreeMeta;

import java.util.List;
import java.util.ArrayList;

public class Book
{
    private Section m_section = new Section("workspace");
    private List<String> m_localDictionary = new ArrayList<String>();

    @TreeMeta(leafTypes = {Section.class, Content.class})
    public Section getSection()
    {
        return m_section;
    }

    public List<String> getLocalDictionary()
    {
        return m_localDictionary;
    }

    public void setLocalDictionary(List<String> localDictionary)
    {
        m_localDictionary = localDictionary;
    }

    public void setSection(Section section)
    {
        m_section = section;
    }

    @Override
    public String toString()
    {
        return "Book";
    }

    public Content step(MainEditor.StepType type, Content thisContent)
    {
        Content previous = null;
        Content next = null;
        boolean found = false;
        List<Content> contents = m_section.enumerate(Content.class);
        for (Content content : contents)
        {
            if (content.equals(thisContent))
            {
                found = true;
            }
            else
            {
                if (found)
                {
                    next = content;
                    break;
                }
                else
                {
                    previous = content;
                }
            }
        }
        return type.equals(MainEditor.StepType.next) ? next : previous;
    }


}
