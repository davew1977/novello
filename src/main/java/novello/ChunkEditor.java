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
import com.xapp.application.utils.SwingUtils;

import javax.swing.*;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import novello.wordhandling.Dictionary;
import novello.undo.*;

public class ChunkEditor extends AbstractPropertyWidget<String>
{
    private JScrollPane m_scrollPane;
    private TextEditor m_textEditor;
    private static final Color DARK_BLUE = new Color(0, 0, 180);
    private static final Color DARKGREEN = new Color(0, 128, 0);
    private Dictionary m_dict;
    private UndoManager m_undoManager = new UndoManager();


    Pattern html = Pattern.compile("<[\\w\\W&&[^>]]*>");
    Pattern speech = Pattern.compile("[\"“].*?[\"”]");
    Pattern comment = Pattern.compile("<!--.*?-->");
    Pattern wholeLine = Pattern.compile(".*");
    private UndoAction m_undoAction = new UndoAction();
    private RedoAction m_redoAction = new RedoAction();

    public void setDict(Dictionary dict)
    {
        m_dict = dict;
    }

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
            m_scrollPane.setPreferredSize(new Dimension(300, 400));
        }
        return m_scrollPane;
    }

    public TextEditor getTextEditor()
    {
        if (m_textEditor == null)
        {
            m_textEditor = new TextEditor()
            {
                public void handleNewText(int offs, String newText, Line linePreEdit, List<Line> lineOrLinesPostEdit)
                {
                    if (newText != null)
                    {
                        m_undoManager.textAdded(offs, newText);
                    }
                    for (Line line : lineOrLinesPostEdit)
                    {
                        setBold(line.m_startIndex, line.length(), false);
                        setForegroundColor(line.m_startIndex, line.length(), Color.BLACK);
                        Matcher matcher = speech.matcher(line.m_text);
                        while (matcher.find())
                        {
                            int start = line.m_startIndex + matcher.start();
                            int length = matcher.group().length();
                            setBold(start, length);
                            setForegroundColor(start, length, DARKGREEN);
                        }

                        matcher = html.matcher(line.m_text);
                        while (matcher.find())
                        {
                            int start = line.m_startIndex + matcher.start();
                            int length = matcher.group().length();
                            setForegroundColor(start, length, DARK_BLUE);
                            setBold(start, length);
                        }

                        matcher = comment.matcher(line.m_text);
                        while (matcher.find())
                        {
                            int start = line.m_startIndex + matcher.start();
                            int length = matcher.group().length();
                            setForegroundColor(start, length, Color.GRAY);
                            setBold(start, length, false);
                            setItalic(start, length);
                        }
                    }
                }

                public void handleTextRemoved(int offs, int len, Line lineAffected, String removedText)
                {
                    m_undoManager.textRemoved(offs, removedText);
                    handleNewText(-1, null, null, Arrays.asList(lineAffected));
                }
            };
            m_textEditor.setFont(Font.decode("Courier-PLAIN-12"));

            m_textEditor.setWordwrap(true);
            m_textEditor.addLiveTemplate("a", "<a href=\"$END$\"></a>");
            m_textEditor.addLiveTemplate("b", "<b>$END$</b>");
            m_textEditor.addLiveTemplate("i", "<i>$END$</i>");
            m_textEditor.addLiveTemplate("img", "<img src=\"$END$\"/>");
            m_textEditor.addLiveTemplate("table", "<table>\n$END$\n</table>");
            m_textEditor.addLiveTemplate("tr", "<tr>$END$</tr>");
            m_textEditor.addLiveTemplate("td", "<td>$END$</td>");
            m_textEditor.addLiveTemplate("p", "<pre>$END$</pre>");
            m_textEditor.addLiveTemplate("c", "<!--$END$-->");
            m_textEditor.addLiveTemplate("s", "“$END$”");

            m_textEditor.addAction("control W", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    TextEditor.Line line = m_textEditor.getCurrentLine();
                    findMatch(line);
                }
            });

            m_textEditor.addAction("control shift SPACE", new WordCompleteAction());
            m_textEditor.addAction("control Z", m_undoAction);
            m_textEditor.addAction("control shift Z", m_redoAction);
        }
        return m_textEditor;
    }

    private void findMatch(TextEditor.Line line)
    {
        Pattern[] patterns = new Pattern[]{comment, html, speech, wholeLine};
        for (Pattern pattern : patterns)
        {
            Matcher matcher = pattern.matcher(line.m_text);
            while (matcher.find())
            {
                int i = line.m_caretIndexInLine - matcher.start();
                if (i >= 0 && i < matcher.group().length())
                {
                    int start = line.m_startIndex + matcher.start();
                    int end = line.m_startIndex + matcher.end();
                    m_textEditor.setSelectionStart(start);
                    m_textEditor.setSelectionEnd(end);
                }
            }
        }
    }

    public String getValue()
    {
        return m_textEditor.getText();
    }

    public void setValue(String value, Object target)
    {
        m_undoManager.disable();
        m_undoManager.init();
        getTextEditor().setText(value);
        m_undoManager.enable();
    }

    private class WordCompleteAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            JPopupMenu popup = m_textEditor.newPopUp();
            String wordToCaret = m_textEditor.getCurrentLine().wordToCaret();
            List<String> words = m_dict.findWords(wordToCaret);
            for (String word : words)
            {
                m_textEditor.addInsertAction(word, word, wordToCaret.length());
            }
            m_textEditor.showPopUp();
            popup.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent e)
                {
                    System.out.println("hello");
                    char aChar = e.getKeyChar();
                    if (Character.isLetter(aChar))
                    {
                        new WordCompleteAction().actionPerformed(null);
                    }
                }
            });
        }
    }

    public static void main(String[] args)
    {
        TextChunk chunk = new TextChunk();
        chunk.setText("this is a text chunk\nthis is another line");
        ChunkEditor chunkEditor = new ChunkEditor();
        chunkEditor.setValue(chunk.getText(), chunk);
        SwingUtils.showInFrame(chunkEditor.getComponent());
    }

    private class UndoAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {

            m_undoManager.disable();
            m_undoManager.flush();
            if (m_undoManager.canUndo())
            {
                Update update = m_undoManager.pullUndo();
                update.undo(m_textEditor);
            }
            m_undoManager.enable();
        }
    }

    private class RedoAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            m_undoManager.disable();
            m_undoManager.flush();
            if (m_undoManager.canRedo())
            {
                Update update = m_undoManager.pullRedo();
                update.redo(m_textEditor);
            }
            m_undoManager.enable();
        }
    }
}
