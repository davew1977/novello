/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import com.xapp.objectmodelling.annotations.TreeMeta;
import com.xapp.objectmodelling.tree.TreeNode;

import java.util.List;

public class Book
{
    private Text m_text;


    @TreeMeta(leafTypes = {Text.class, Content.class})
    public Text getText()
    {
        return m_text;
    }

    public void setText(Text text)
    {
        m_text = text;
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
        List<Content> contents = m_text.enumerate(Content.class);
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
