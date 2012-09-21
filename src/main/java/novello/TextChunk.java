/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2008-maj-08
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.annotations.Mandatory;
import com.xapp.application.annotations.EditorWidget;
import com.xapp.marshalling.annotations.FormattedText;
import novello.widgets.ChunkEditor;

public class TextChunk implements Text
{
    private String m_text = "";

    @FormattedText
    @EditorWidget(value= ChunkEditor.class)
    @Mandatory
    public String getText()
    {
        return m_text;
    }

    public void setText(String text)
    {
        m_text = text;
    }

    @Override
    public String toString()
    {
        String s = m_text.split("\n")[0];
        return s.substring(0,Math.min(20,s.length()));
    }

    public static void main(String[] args)
    {
    }

    @Override
    public String text() {
        return getText();
    }
}
