/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import net.sf.xapp.annotations.application.EditorWidget;
import net.sf.xapp.application.editor.widgets.FreeTextPropertyWidget;
import net.sf.xapp.tree.Tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * "Sections" mark the structure of the document. Any text entered here is metadata
 */
public class Section extends Tree
{
    private String m_text;
    private boolean m_excluded;
    private String m_customLiveTemplates;

    public Section(String s)
    {
        setName(s);
    }

    public Section()
    {
    }

    @EditorWidget(FreeTextPropertyWidget.class)
    public String getText()
    {
        return m_text;
    }

    public void setText(String text)
    {
        m_text = text;
    }

    @EditorWidget(FreeTextPropertyWidget.class)
    public String getCustomLiveTemplates()
    {
        return m_customLiveTemplates;
    }

    public void setCustomLiveTemplates(String customLiveTemplates)
    {
        m_customLiveTemplates = customLiveTemplates;
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

    public Section parent()
    {
        return (Section) super.parent();
    }

    public Map<String,String> resolveCustomLiveTemplates()
    {
        Map<String,String> liveTemplates = new HashMap<String, String>();
        if(!_isRoot())
        {
            liveTemplates.putAll(parent().resolveCustomLiveTemplates());
        }
        if(getCustomLiveTemplates()!=null)
        {
            String[] lines = getCustomLiveTemplates().split("\n");
            for (String line : lines)
            {
                String[] args = line.split(":", 2);
                liveTemplates.put(args[0], args[1]);
            }
        }
        return liveTemplates;
    }
}
