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
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.*;
import java.awt.event.ActionEvent;

import novello.wordhandling.Dictionary;
import novello.wordhandling.ThesaurusService;
import novello.wordhandling.DictFileHandler;
import novello.wordhandling.DictionaryType;
import novello.undo.*;
import novello.TextChunk;
import novello.NovelloApp;

public class ChunkEditor extends AbstractPropertyWidget<String>
{
    private JScrollPane m_scrollPane;
    private TextEditor m_textEditor;
    private static final Color DARK_BLUE = new Color(0, 0, 180);
    private static final Color DARKGREEN = new Color(0, 128, 0);
    public Dictionary m_dict;
    private ThesaurusService m_thesaurus = new ThesaurusService();
    private UndoManager m_undoManager = new UndoManager();

    Pattern html = Pattern.compile("<[\\w\\W&&[^>]]*>");
    Pattern speech = Pattern.compile("[\"\u201c].*?[\"\u201d]");
    Pattern comment = Pattern.compile("<!--.*?-->");
    Pattern wholeLine = Pattern.compile(".*");
    public Pattern WORD = Pattern.compile("[\\w'\u2019]*");
    private UndoAction m_undoAction = new UndoAction();
    private RedoAction m_redoAction = new RedoAction();
    private NovelloApp m_novelloApp;

    public void setDict(Dictionary dict)
    {
        m_dict = dict;
    }

    public void setNovelloApp(NovelloApp novelloApp)
    {
        m_novelloApp = novelloApp;
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
                        setItalic(line.m_startIndex, line.length(), false);
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
                        matcher = WORD.matcher(line.m_text);
                        while (matcher.find())
                        {
                            if (!m_dict.wordOk(matcher.group()))
                            {
                                int start = line.m_startIndex + matcher.start();
                                int length = matcher.group().length();
                                setForegroundColor(start, length, Color.RED);
                            }
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
            m_textEditor.addLiveTemplate("a", "<a href=\"$0\">$1</a>$2");
            m_textEditor.addLiveTemplate("b", "<b>$0</b>$1");
            m_textEditor.addLiveTemplate("i", "<i>$0</i>$1");
            m_textEditor.addLiveTemplate("img", "<img src=\"$0\"/>$1");
            m_textEditor.addLiveTemplate("table", "<table>\n$0\n</table>");
            m_textEditor.addLiveTemplate("tr", "<tr>$0</tr>");
            m_textEditor.addLiveTemplate("td", "<td>$0</td>");
            m_textEditor.addLiveTemplate("p", "<pre>$0</pre>");
            m_textEditor.addLiveTemplate("c", "<!--$0-->");
            m_textEditor.addLiveTemplate("s", "\u201c$0\u201d$1");

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
        Pattern[] patterns = new Pattern[]{WORD, comment, html, speech, wholeLine};
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
                    return;
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

    public void addPopupActions()
    {
        final TextEditor.Line line = m_textEditor.getCurrentLine();
        final String word = line.wordAtCaret(WORD);
        if (!m_dict.wordOk(word))
        {
            m_textEditor.addPopUpAction(new AddWordAction(DictionaryType.local, word, line));
            //m_textEditor.addPopUpAction(new AddWordAction(DictionaryType.global, word, line));
        }
    }

    private class WordCompleteAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            final TextEditor.Line line = m_textEditor.getCurrentLine();
            final String wordToCaret = line.wordToCaret();
            List<String> words = m_dict.findWords(wordToCaret, 25);
            Point p = m_textEditor.getPointAtIndex(m_textEditor.getCaretPosition() - wordToCaret.length());
            if (!words.isEmpty())
            {
                new ComboChooser<String>(p.x - 2, p.y - 2, m_textEditor, words, wordToCaret, new MyComboChooserClient()
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
            Collection<String> options = m_thesaurus.suggest(word);
            if (!options.isEmpty())
            {

                new ComboChooser<String>(p.x - 2, p.y - 2, m_textEditor, options, wordToCaret, new MyComboChooserClient()
                {
                    public void itemChosen(String item)
                    {
                        m_textEditor.replaceWordAtCaret(line, item);
                    }
                });
            }
        }
    }

    private class AddWordAction extends AbstractAction
    {
        private final String m_word;
        private final TextEditor.Line m_line;
        private DictionaryType m_dictType;

        public AddWordAction(DictionaryType dictionaryType, String word, TextEditor.Line line)
        {
            super("add word to " + dictionaryType + " dictionary");
            m_word = word;
            m_line = line;
            m_dictType = dictionaryType;
        }

        public void actionPerformed(ActionEvent e)
        {
            m_dict.addWord(m_word);
            int caret = getTextEditor().getCaretPosition();
            getTextEditor().setText(m_textEditor.getText());
            getTextEditor().setCaretPosition(caret);
            m_textEditor.handleNewText(-1, null, null, Arrays.asList(m_line));
            m_novelloApp.addWordToDict(m_dictType, m_word);
        }
    }

    private class MyComboChooserClient extends SimpleComboChooserClient<String>
    {

        public void selectionChanged(String item)
        {
            if (m_novelloApp != null)
            {
                m_novelloApp.getAppContainer().setStatusMessage(item);
            }
        }

        public void comboRemoved()
        {
            if (m_novelloApp != null)
            {
                m_novelloApp.getAppContainer().setStatusMessage("");
            }
        }
    }
}
