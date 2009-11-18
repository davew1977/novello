/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.annotations.EditorWidget;
import com.xapp.application.editor.widgets.FreeTextPropertyWidget;
import com.xapp.marshalling.annotations.FormattedText;
import com.xapp.objectmodelling.tree.Tree;

import java.util.List;

/**
 * "Sections" mark the structure of the document. Any text entered here is metadata
 */
public class Section extends Tree
{
    private String m_text;
    private boolean m_excluded;


    @FormattedText
    @EditorWidget(FreeTextPropertyWidget.class)
    public String getText()
    {
        return m_text;
    }

    public void setText(String text)
    {
        m_text = text;
    }

    public int wordcount()
    {
        int count = 0;
        List<Section> list = children();
        for (Section section : list)
        {
            if(!section.isExcluded())count+= section.wordcount();
        }
        return count;
    }

    public List<Section> children()
    {
        return (List) getChildren();
    }

    public void setExcluded(boolean excluded)
    {
        m_excluded = excluded;
    }

    public boolean isExcluded()
    {
        return m_excluded;
    }
}
