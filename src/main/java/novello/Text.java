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

public class Text extends Tree
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
        List<Text> list = children();
        for (Text text : list)
        {
            if(!text.isExcluded())count+=text.wordcount();
        }
        return count;
    }

    public List<Text> children()
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
