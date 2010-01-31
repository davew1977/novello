/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import com.xapp.objectmodelling.annotations.TreeMeta;
import com.xapp.application.annotations.EditorWidget;
import com.xapp.application.editor.widgets.FreeTextPropertyWidget;

import java.util.List;
import java.util.ArrayList;

public class Book
{
    private Section m_section = new Section("workspace");
    private List<String> m_localDictionary = new ArrayList<String>();
    private String m_styleSheet = "p {\n" +
                "color:#222222;  " +
                "line-height: 200%;\n" +
                "font-family:Tahoma, sans-serif;" +
                "}";

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

    @EditorWidget(FreeTextPropertyWidget.class)
    public String getStyleSheet()
    {
        return m_styleSheet;
    }

    public void setStyleSheet(String styleSheet)
    {
        m_styleSheet = styleSheet;
    }

    @Override
    public String toString()
    {
        return "Book";
    }

    /**
     * steps and loops. If end is reached, it will return the first and vica versa
     * @param type
     * @param thisContent
     * @return
     */
    public Content stepCircular(Direction type, Content thisContent)
    {
        Content content = step(type, thisContent);
        if(content==null)
        {
            List<Content> contents = m_section.enumerate(Content.class);
            return type == Direction.forward ? contents.get(0) : contents.get(contents.size()-1);
        }
        return content;
    }
    public Content step(Direction type, Content thisContent)
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
        return type.equals(Direction.forward) ? next : previous;
    }


}
