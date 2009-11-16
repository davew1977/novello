/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2009-nov-08
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.editor.widgets.TextEditor;
import com.xapp.application.editor.widgets.AbstractPropertyWidget;
import com.xapp.application.api.WidgetContext;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChunkEditor extends AbstractPropertyWidget<String>
{
    private JScrollPane m_scrollPane;
    private TextEditor m_textEditor;
    private static final Color DARK_BLUE = new Color(0,0,180);

    public String validate()
    {
        String[] lines = m_textEditor.getText().split("\n");
        for (int i = 0; i < lines.length; i++)
        {
            //TODO
        }
        return null;
    }

    @Override
    public void init(WidgetContext<String> context)
    {
        super.init(context);
    }

    public void setEditable(boolean editable)
    {
        m_textEditor.setEditable(editable);
    }

    public JScrollPane getComponent()
    {
        if (m_scrollPane == null)
        {
            m_scrollPane = new JScrollPane(getTextEditor(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            m_scrollPane.setPreferredSize(new Dimension(300,400));
        }
        return m_scrollPane;
    }

    public TextEditor getTextEditor()
    {
        if (m_textEditor == null)
        {
            m_textEditor = new TextEditor()
            {
                Pattern pattern = Pattern.compile("<[\\w\\W&&[^>]]*>");
                public void handleNewText(int offs, String newText, Line linePreEdit, List<Line> lineOrLinesPostEdit)
                {
                    for (Line line : lineOrLinesPostEdit)
                    {
                        Matcher matcher = pattern.matcher(line.m_text);
                        int lastEnd = line.m_startIndex;
                        int lastEndInLine = 0;
                        SimpleAttributeSet as = new SimpleAttributeSet();
                        while(matcher.find())
                        {
                            //set start chunk black
                            int end = matcher.start() - lastEndInLine;
                            setBold(lastEnd, end, false);
                            setForegroundColor(lastEnd, end, Color.BLACK);
                            //set tag blue
                            int length = matcher.end() - matcher.start();
                            int start = line.m_startIndex + matcher.start();
                            setForegroundColor(start, length, DARK_BLUE);
                            setBold(start, length);
                            lastEnd = line.m_startIndex + matcher.end();
                            lastEndInLine = matcher.end();
                        }
                        //set the trailing bit black
                        as = new SimpleAttributeSet();
                        StyleConstants.setForeground(as, Color.BLACK);
                        m_textEditor.getDoc().setCharacterAttributes(lastEnd, line.length() - lastEndInLine, as, true);


                    }
                }

                public void handleTextRemoved(int offs, Line lineAffected)
                {
                    handleNewText(-1, null, null, Arrays.asList(lineAffected));
                }
            };
            m_textEditor.setWordwrap(true);
            m_textEditor.addLiveTemplate("a", "<a href=\"$END$\"></a>");
            m_textEditor.addLiveTemplate("b", "<b>$END$</b>");
            m_textEditor.addLiveTemplate("i", "<i>$END$</i>");
            m_textEditor.addLiveTemplate("img", "<img src=\"$END$\"/>");
            m_textEditor.addLiveTemplate("table", "<table>\n$END$\n</table>");
            m_textEditor.addLiveTemplate("tr", "<tr>$END$</tr>");
            m_textEditor.addLiveTemplate("td", "<td>$END$</td>");
            m_textEditor.addLiveTemplate("p", "<pre>$END$</pre>");

        }
        return m_textEditor;
    }

    public String getValue()
    {
        return m_textEditor.getText();
    }

    public void setValue(String value, Object target)
    {
        getTextEditor().setText(value);
    }
}
