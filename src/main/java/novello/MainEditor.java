/*
 *
 * Date: 2009-nov-15
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.utils.html.BrowserView;
import com.xapp.application.utils.html.HTML;
import com.xapp.application.utils.html.HTMLImpl;
import novello.wordhandling.DictFileHandler;
import novello.wordhandling.DictionaryImpl;
import novello.widgets.ChunkEditor;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MainEditor extends JSplitPane
{
    NovelloApp m_novelloApp;
    BrowserView m_htmlRenderer;
    ChunkEditor m_chunkEditor;
    private TextChunk m_chunk = new TextChunk();
    private Content m_parentContent;
    private Pattern WORD_COUNT_PATTERN = Pattern.compile("\\s+");
    private final JScrollPane m_jsp1;
    private final JScrollPane m_jsp2;

    public MainEditor(NovelloApp novelloApp)
    {
        super(VERTICAL_SPLIT);
        m_novelloApp = novelloApp;
        m_htmlRenderer = new BrowserView();
        m_chunkEditor = new ChunkEditor();
        m_chunkEditor.setNovelloApp(m_novelloApp);
        m_chunkEditor.setMainEditor(this);

        m_jsp1 = new JScrollPane(m_htmlRenderer);
        m_jsp1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        m_jsp1.setPreferredSize(new Dimension(300, 400));

        add(m_jsp1);

        m_jsp2 = m_chunkEditor.getComponent();
        add(m_jsp2);


        m_chunkEditor.getTextEditor().addAction("control S", new SaveAction(this, novelloApp));
        m_chunkEditor.getTextEditor().addAction("control Q", new QuitAction());
        m_chunkEditor.getTextEditor().addAction("alt RIGHT", new StepAction(Direction.forward));
        m_chunkEditor.getTextEditor().addAction("alt LEFT", new StepAction(Direction.back));
        m_chunkEditor.getTextEditor().addAction("F2", new Find(Direction.forward, new FindMispelt()));
        m_chunkEditor.getTextEditor().addAction("shift F2", new Find(Direction.back, new FindMispelt()));

        m_jsp1.getVerticalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
        m_jsp2.getVerticalScrollBar().addAdjustmentListener(new MyAdjustmentListener());

        DictionaryImpl dictionary = DictFileHandler.loadDictionary("en_uk");
        dictionary.addWords(m_novelloApp.getBook().getLocalDictionary());
        m_chunkEditor.setDict(dictionary);

        //word count updater
        m_chunkEditor.getTextEditor().getDoc().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                updateWordCount();
            }

            public void removeUpdate(DocumentEvent e)
            {
                updateWordCount();
            }

            public void changedUpdate(DocumentEvent e)
            {

            }
        });
    }

    public void setChunk(TextChunk textChunk, Content parentContent)
    {
        store();
        m_chunk = textChunk;
        m_parentContent = parentContent;
        render();
        m_chunkEditor.setValue(textChunk.getText(), parentContent);
        updateWordCount();
    }

    public void render()
    {
        HTML html = new HTMLImpl();
        html.setStyle(m_novelloApp.getBook().getStyleSheet());
        html.p(m_chunk.getText());
        int value = m_jsp2.getVerticalScrollBar().getValue();
        m_htmlRenderer.setHTML(html);
        m_jsp2.getVerticalScrollBar().setValue(value);
    }

    private void updateWordCount()
    {
        if (m_chunk != null)
        {
            String text = m_chunkEditor.getTextEditor().getText();
            int count = WORD_COUNT_PATTERN.split(text).length;
            m_novelloApp.getAppContainer().setStatusMessage("word count: " + count);
        }
    }

    public void store()
    {
        if (m_chunk != null)
        {
            m_chunk.setText(m_chunkEditor.getValue());
            if (m_novelloApp.shouldSplit(m_chunk))
            {
                TextChunk chunk = m_chunk;
                m_chunk = null;
                m_novelloApp.doSplit(chunk);
            }
        }
    }

    private class StepAction extends AbstractAction
    {
        private Direction m_type;

        private StepAction(Direction type)
        {
            super(type.toString());
            m_type = type;
        }

        public void actionPerformed(ActionEvent e)
        {
            m_novelloApp.getAppContainer().expand(m_novelloApp.getBook().step(m_type, m_parentContent));
            m_chunkEditor.getTextEditor().requestFocus();
        }
    }

    private class Find extends AbstractAction
    {
        private Direction m_type;
        private FindStrategy m_findStrategy;

        private Find(Direction type, FindStrategy findStrategy)
        {
            m_type = type;
            m_findStrategy = findStrategy;
        }

        public void actionPerformed(ActionEvent e)
        {
            store();
            TextChunk startChunk = m_chunk;
            TextChunk currentChunk = m_chunk;
            Content currentContent = m_parentContent;
            boolean found = false;
            boolean stop = false;
            int cursor = m_chunkEditor.getTextEditor().getCaretPosition();
            while (!found && !stop)
            {
                System.out.println(currentChunk);
                Matcher m = m_findStrategy.getPattern().matcher(currentChunk.getText().substring(cursor));
                while (m.find())
                {
                    if (m_findStrategy.accept(m.group()))
                    {
                        if (currentChunk != startChunk)
                        {
                            m_novelloApp.getAppContainer().expand(currentChunk);
                        }
                        m_chunkEditor.getTextEditor().requestFocus();
                        int startPos = cursor + m.start();
                        int endPos = cursor + m.end();
                        m_chunkEditor.getTextEditor().setCaretPosition(startPos);
                        m_chunkEditor.getTextEditor().setSelectionStart(startPos);
                        m_chunkEditor.getTextEditor().setSelectionEnd(endPos);
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    currentContent = m_novelloApp.getBook().stepCircular(m_type, currentContent);
                    currentChunk = currentContent.latest();
                    cursor = 0;
                }
                if (startChunk == currentChunk)
                {
                    stop = true;
                }
            }

        }
    }

    private class QuitAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            m_novelloApp.getAppContainer().quit();
        }
    }

    private class FindMispelt implements FindStrategy
    {
        public Pattern getPattern()
        {
            return m_chunkEditor.WORD_FOR_SPELLCHECK;
        }

        public boolean accept(String s)
        {
            return !m_chunkEditor.m_dict.wordOk(s);
        }
    }

    private class MyAdjustmentListener implements AdjustmentListener
    {
        private boolean m_ignore;

        public void adjustmentValueChanged(AdjustmentEvent e)
        {
            //work out percentage
            if (!m_ignore)
            {
                JScrollPane me = e.getSource() == m_jsp2.getVerticalScrollBar() ? m_jsp2 : m_jsp1;
                JScrollPane other = e.getSource() == m_jsp2.getVerticalScrollBar() ? m_jsp1 : m_jsp2;

                int myMax = me.getVerticalScrollBar().getMaximum() - me.getVerticalScrollBar().getVisibleAmount();
                int otherMax = other.getVerticalScrollBar().getMaximum() - other.getVerticalScrollBar().getVisibleAmount();
                float proportion = e.getValue() / (float) myMax;
                m_ignore = true;
                other.getVerticalScrollBar().setValue((int) (otherMax * proportion));
                m_ignore = false;
            }
        }
    }
}
