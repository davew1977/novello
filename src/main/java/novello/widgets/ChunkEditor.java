/*
 * Copyright 2007 bwin games AB
 *
 * Date: 2009-nov-08
 * Author: davidw
 *
 */
package novello.widgets;

import net.sf.xapp.application.editor.text.TextEditor;
import net.sf.xapp.application.editor.text.Word;
import net.sf.xapp.application.editor.widgets.AbstractPropertyWidget;
import net.sf.xapp.application.utils.SwingUtils;
import net.sf.xapp.application.utils.html.HTML;
import net.sf.xapp.application.utils.html.HTMLImpl;
import net.sf.xapp.utils.StringUtils;
import novello.*;
import novello.undo.UndoManager;
import novello.undo.Update;
import novello.wikipedia.ResultItem;
import novello.wikipedia.WikipediaResponse;
import novello.wikipedia.WikipediaService;
import novello.wordhandling.DictFileHandler;
import novello.wordhandling.Dictionary;
import novello.wordhandling.DictionaryType;
import novello.wordhandling.ThesaurusService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkEditor extends AbstractPropertyWidget<String>
{
    private JScrollPane m_scrollPane;
    protected TextEditor m_textEditor;
    public static final Color DARK_BLUE = new Color(0, 0, 180);
    private static final Color DARKGREEN = new Color(0, 128, 0);
    public Dictionary m_dict;
    private ThesaurusService m_thesaurus = new ThesaurusService();
    private WikipediaService m_wikipediaService = new WikipediaService();

    Pattern html = Pattern.compile("<[\\w\\W&&[^>]]*>");
    Pattern speech = Pattern.compile("[\"\u201c].*?[\"\u201d]");
    Pattern comment = Pattern.compile("<!--.*?-->");
    Pattern wholeLine = Pattern.compile(".*");
    public Pattern WORD_FOR_SPELLCHECK = Pattern.compile("[A-Za-z'\u2019]*");
    public Pattern WORD = Pattern.compile("[\\w'\u2019]*");
    private DocumentApplication mDocumentApplication;
    private MainEditor m_mainEditor;

    public ChunkEditor() {
        m_dict = DictFileHandler.getDictionary();
    }

    public void setMainEditor(MainEditor mainEditor)
    {
        m_mainEditor = mainEditor;
    }

    public void setNovelloApp(DocumentApplication pDocumentApplication)
    {
        this.mDocumentApplication = pDocumentApplication;
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
                        matcher = WORD_FOR_SPELLCHECK.matcher(line.m_text);
                        while (matcher.find())
                        {
                            if (!m_dict.wordOk(matcher.group()))
                            {
                                int start = line.m_startIndex + matcher.start();
                                int length = matcher.group().length();
                                setForegroundColor(start, length, Color.RED);
                            }
                        }
                        //following will not be spell checked
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
                    ChunkEditor.this.handleNewText(offs, newText, linePreEdit, lineOrLinesPostEdit);
                }

                public void handleTextRemoved(int offs, int len, Line lineAffected, String removedText)
                {
                    handleNewText(-1, null, null, Arrays.asList(lineAffected));
                }
            };
            m_textEditor.setFont(Font.decode("Courier-PLAIN-14"));

            m_textEditor.setWordwrap(true);

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
            m_textEditor.addAction("control SPACE", new PopUpMenuAction());
        }
        return m_textEditor;
    }

    protected void handleNewText(int offs, String newText, TextEditor.Line linePreEdit, List<TextEditor.Line> lineOrLinesPostEdit) {

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
        m_dict.reset();
        getTextEditor().setText(value);
        m_textEditor.clearLiveTemplate();
        m_textEditor.addLiveTemplate("a", "<a target=\"_blank\" href=\"$0\">$1</a>$2");
        m_textEditor.addLiveTemplate("b", "<b>$0</b>$1");
        m_textEditor.addLiveTemplate("i", "<i>$0</i>$1");
        m_textEditor.addLiveTemplate("img", "<img src=\"$0\"/>$1");
        m_textEditor.addLiveTemplate("table", "<table>\n$0\n</table>");
        m_textEditor.addLiveTemplate("tr", "<tr>$0</tr>");
        m_textEditor.addLiveTemplate("td", "<td>$0</td>");
        m_textEditor.addLiveTemplate("p", "<pre>$0</pre>");
        m_textEditor.addLiveTemplate("c", "<!--$0-->");
        m_textEditor.addLiveTemplate("s", "\u201c$0\u201d$1");

        if(target instanceof Content)
        {
            Content content = (Content) target;
            Map<String,String> customTemplates = content.resolveCustomLiveTemplates();
            for (Map.Entry<String, String> e : customTemplates.entrySet())
            {
                m_textEditor.addLiveTemplate(e.getKey(), e.getValue());
            }
        }

    }

    private class WordCompleteAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            final TextEditor.Line line = m_textEditor.getCurrentLine();
            final Word word = line.wordAtCaret();
            List<String> words = m_dict.findWords(word.wordToCaret(), 25);
            Point p = m_textEditor.getPointAtIndex(m_textEditor.getCaretPosition() - word.wordToCaret().length());
            if (!words.isEmpty())
            {
                ComboChooser<String> combo = new ComboChooser<String>();
                combo.init(p.x - 2, p.y - 2, m_textEditor, words, word.wordToCaret(), new MyComboChooserClient()
                {
                    public void itemChosen(String item)
                    {
                        m_textEditor.replaceWordAtCaret(word, item);
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
        Dictionary dictionary = DictFileHandler.getDictionary();
        TextChunk chunk = new TextChunk();
        chunk.setText("this is a text chunk\nthis is another line\nthis is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line this is a really long line ");
        ChunkEditor chunkEditor = new ChunkEditor();
        chunkEditor.setNovelloApp(new NovelloApp(null));
        chunkEditor.setValue(chunk.getText(), null);
        SwingUtils.showInFrame(chunkEditor.getComponent());
    }

    private class ThesaurusAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            final TextEditor.Line line = m_textEditor.getCurrentLine();
            final Word word = line.wordAtCaret();
            final String wordToCaret = line.wordToCaret();
            Point p = m_textEditor.getPointAtIndex(m_textEditor.getCaretPosition() - wordToCaret.length());
            Collection<String> options = m_thesaurus.lookup(word.value);
            if (!options.isEmpty())
            {
                ComboChooser<String> combo = new ComboChooser<String>();
                combo.init(p.x - 2, p.y - 2, m_textEditor, options, wordToCaret, new MyComboChooserClient()
                {
                    public void itemChosen(String item)
                    {
                        m_textEditor.replaceWordAtCaret(word, item);
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
            mDocumentApplication.addWordToDict(m_dictType, m_word);
        }
    }

    private class MyComboChooserClient extends SimpleComboChooserClient<String>
    {

        public void selectionChanged(String item)
        {
            if (mDocumentApplication != null)
            {
                mDocumentApplication.setStatusMessage(item);
            }
        }

        public void comboRemoved()
        {
            if (mDocumentApplication != null)
            {
                mDocumentApplication.setStatusMessage("");
            }
        }
    }

    static final Color DARK_GREEN = Color.decode("0x006600");

    private class WikipediaAction extends AbstractAction
    {
        private String m_word;
        private TextEditor.Line m_line;
        private int m_removeStart;
        private int m_removeLength;

        public WikipediaAction(String wordToLookup, TextEditor.Line line, int removeStart, int removeLength)
        {
            super("Lookup: " + wordToLookup, NovelloTreeGraphics.WIKIPEDIA_ICON);
            m_word = wordToLookup;
            m_line = line;
            m_removeStart = removeStart;
            m_removeLength = removeLength;
        }

        public void actionPerformed(ActionEvent e)
        {
            WikipediaResponse response = m_wikipediaService.lookup(m_word.trim());
            final JList list = new JList(new Vector<Object>(response.getQueryResult().getItems()));
            list.setCellRenderer(new DefaultListCellRenderer()
            {
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
                {
                    JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    SwingUtils.setFont(comp);
                    ResultItem item = (ResultItem) value;
                    if (item != null)
                    {
                        HTML html = new HTMLImpl();
                        html.b().color(DARK_GREEN).append(item.getTitle()).b().br();
                        html.color(Color.BLACK).size(3).p(StringUtils.breakText(item.getSnippet(), "<br/>", 65));
                        comp.setText(html.htmlDoc());
                    }
                    return comp;
                }
            });
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane jsp = new JScrollPane(list);
            jsp.setPreferredSize(new Dimension(400, 300));
            final JFrame f = SwingUtils.createFrame(jsp);
            f.setTitle("Result for " + m_word.trim());
            f.setLocationRelativeTo(m_textEditor.getParent());
            f.setVisible(true);
            list.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent e)
                {
                    System.out.println(e.getKeyChar());
                    ResultItem resultItem = (ResultItem) list.getSelectedValue();
                    if (e.getKeyChar() == KeyEvent.VK_ENTER)
                    {
                        if (list.getSelectedValue() != null)
                        {
                            m_wikipediaService.open(resultItem);
                        }
                        f.setVisible(false);
                    }
                    else if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
                    {
                        f.setVisible(false);
                    }
                    else if (e.getKeyChar() == KeyEvent.VK_SPACE)
                    {
                        String link = m_wikipediaService.link((ResultItem) list.getSelectedValue());
                        String insert = String.format("<a target='_blank' href=\"%s\">%s</a>", link, m_word);
                        m_textEditor.remove(m_removeStart, m_removeLength);
                        m_textEditor.insert(m_removeStart, insert);
                        f.setVisible(false);
                    }
                }
            });

        }
    }


    private class PopUpMenuAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            TextEditor textEditor = getTextEditor();
            textEditor.newPopUp();
            final TextEditor.Line line = m_textEditor.getCurrentLine();
            final Word word = line.wordAtCaret(WORD_FOR_SPELLCHECK);
            if (!m_dict.wordOk(word.value))
            {
                m_textEditor.addPopUpAction(new AddWordAction(DictionaryType.local, word.value, line));
                //m_textEditor.addPopUpAction(new AddWordAction(DictionaryType.global, word, line));
            }
            else
            {

                if (m_mainEditor != null)
                {
                    textEditor.addPopUpAction(new GotoAction(m_mainEditor, mDocumentApplication.getDocTree(), "goto"));
                    textEditor.addInsertAction("make split", "\n,");
                }


                String username = mDocumentApplication.getCurrentUser();
                String ts = SimpleDateFormat.getDateInstance().format(System.currentTimeMillis());
                textEditor.addInsertAction("timestamp", "[" + username + " " + ts + "]  ");
            }
            String wordToLookup = m_textEditor.getSelectedText();
            int removeStart = wordToLookup!=null ? m_textEditor.getSelectionStart() : word.start;
            int removeEnd= wordToLookup!=null ? m_textEditor.getSelectionEnd() : word.end;
            int removeLength = removeEnd-removeStart;
            wordToLookup = wordToLookup != null ? wordToLookup : word.value;
            m_textEditor.addPopUpAction(new WikipediaAction(wordToLookup, line, removeStart, removeLength));
            controlSpaceClicked(wordToLookup, line, removeStart, removeLength);
            textEditor.showPopUp();
        }
    }

    /**
     * override to add extra pop up commands
     * @param wordToLookup the selected text or nearest word
     * @param line
     * @param removeStart start of selection, useful if replacing text
     * @param removeLength
     */
    protected void controlSpaceClicked(String wordToLookup, TextEditor.Line line, int removeStart, int removeLength)
    {

    }
}
