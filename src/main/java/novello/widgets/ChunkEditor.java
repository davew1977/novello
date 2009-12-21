/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2009-nov-08
 * Author: davidw
 *
 */
package novello.widgets;

import com.xapp.application.editor.widgets.TextEditor;
import com.xapp.application.editor.widgets.AbstractPropertyWidget;
import com.xapp.application.utils.SwingUtils;

import javax.swing.*;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.*;
import java.awt.event.ActionEvent;

import novello.wordhandling.Dictionary;
import novello.wordhandling.ThesaurusService;
import novello.wordhandling.DictFileHandler;
import novello.undo.*;
import novello.TextChunk;

public class ChunkEditor extends AbstractPropertyWidget<String>
{
    private JScrollPane m_scrollPane;
    private TextEditor m_textEditor;
    private static final Color DARK_BLUE = new Color(0, 0, 180);
    private static final Color DARKGREEN = new Color(0, 128, 0);
    private Dictionary m_dict;
    private ThesaurusService m_thesaurus = new ThesaurusService();
    private UndoManager m_undoManager = new UndoManager();

    Pattern html = Pattern.compile("<[\\w\\W&&[^>]]*>");
    Pattern speech = Pattern.compile("[\"\u201c].*?[\"\u201d]");
    Pattern comment = Pattern.compile("<!--.*?-->");
    Pattern wholeLine = Pattern.compile(".*");
    private UndoAction m_undoAction = new UndoAction();
    private RedoAction m_redoAction = new RedoAction();

    public void setDict(Dictionary dict)
    {
        m_dict = dict;
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
            m_textEditor.addLiveTemplate("s", "\u201c$END$\u201d");

            m_textEditor.addAction("control W", new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    TextEditor.Line line = m_textEditor.getCurrentLine();
                    findMatch(line);
                }
            });

            m_textEditor.addAction("control shift SPACE", new WordCompleteAction());
            m_textEditor.addAction("control T", new ThesaurusAction());
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
            final TextEditor.Line line = m_textEditor.getCurrentLine();
            final String wordToCaret = line.wordToCaret();
            List<String> words = m_dict.findWords(wordToCaret);
            Point p = m_textEditor.getPointAtIndex(m_textEditor.getCaretPosition() - wordToCaret.length());
            if (!words.isEmpty())
            {
                new ComboChooser<String>(p.x-2, p.y-2,m_textEditor, words, wordToCaret, new ComboChooserClient<String>()
                {
                    public void itemChosen(String item)
                    {
                        m_textEditor.replaceWordAtCaret(line, item);
                    }

                    public List<String> filterValues(String updatedText)
                    {
                        return m_dict.findWords(updatedText);
                    }
                });
            }
        }
    }

    public static void main(String[] args)
    {
        Dictionary dictionary = DictFileHandler.loadDictionary("en_uk");
        TextChunk chunk = new TextChunk();
        chunk.setText("this is a text chunk\nthis is another line");
        ChunkEditor chunkEditor = new ChunkEditor();
        chunkEditor.setDict(dictionary);
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

    private class ThesaurusAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            final TextEditor.Line line = m_textEditor.getCurrentLine();
            String word = line.wordAtCaret();
            final String wordToCaret = line.wordToCaret();
            Point p = m_textEditor.getPointAtIndex(m_textEditor.getCaretPosition() - wordToCaret.length());
            List<String> options = m_thesaurus.suggest(word);
            if(!options.isEmpty())
            {

                new ComboChooser<String>(p.x-2, p.y-2,m_textEditor, options, wordToCaret, new ComboChooserClient<String>()
                {
                    public void itemChosen(String item)
                    {
                        m_textEditor.replaceWordAtCaret(line, item);
                    }

                    public List<String> filterValues(String updatedText)
                    {
                        return null;
                    }
                });
            }
        }
    }
}