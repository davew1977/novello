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
import novello.wordhandling.Dictionary;
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

public class MainEditor extends JScrollPane
{
    public DocumentApplication mDocumentApplication;
    BrowserView m_htmlRenderer;
    ChunkEditor m_chunkEditor;
    private Text m_chunk = new TextChunk();
    private TextHolder parent;
    private Pattern WORD_COUNT_PATTERN = Pattern.compile("\\s+");
    private final JScrollPane m_jsp1;
    private Popup popUpRenderer;
    //private final JScrollPane m_jsp2;

    public MainEditor(DocumentApplication novelloApp)
    {

        mDocumentApplication = novelloApp;
        m_htmlRenderer = new BrowserView();
        m_chunkEditor = createChunkEditor();
        m_chunkEditor.setNovelloApp(mDocumentApplication);
        m_chunkEditor.setMainEditor(this);

        m_jsp1 = new JScrollPane(m_htmlRenderer);
        m_jsp1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        m_jsp1.setPreferredSize(new Dimension(300, 400));

        //add(m_jsp1);


        setViewportView(m_chunkEditor.getTextEditor());


        m_chunkEditor.getTextEditor().addAction("control S", new SaveAction(this, novelloApp));
        m_chunkEditor.getTextEditor().addAction("control Q", new QuitAction());
        m_chunkEditor.getTextEditor().addAction("alt RIGHT", new StepAction(Direction.forward));
        m_chunkEditor.getTextEditor().addAction("alt LEFT", new StepAction(Direction.back));
        m_chunkEditor.getTextEditor().addAction("F2", new Find(Direction.forward, new FindMispelt()));
        m_chunkEditor.getTextEditor().addAction("shift F2", new Find(Direction.back, new FindMispelt()));
        m_chunkEditor.getTextEditor().addAction("alt M", new Render());

        m_jsp1.getVerticalScrollBar().addAdjustmentListener(new MyAdjustmentListener());
        getVerticalScrollBar().addAdjustmentListener(new MyAdjustmentListener());

        Dictionary dictionary = DictFileHandler.getDictionary();
        dictionary.addWords(mDocumentApplication.getLocalDictionary());

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

    public ChunkEditor createChunkEditor()
    {
        return new ChunkEditor();
    }

    public void setChunk(Text textChunk)
    {
        store();
        m_chunk = textChunk;
        parent = mDocumentApplication.getTextHolder(textChunk);
        render();
        m_chunkEditor.setValue(textChunk.text(), parent);
        updateWordCount();
    }

    public void render()
    {
        HTML html = new HTMLImpl();
        html.setStyle(mDocumentApplication.getStyleSheet());
        html.p(m_chunk.text());
        int value = getVerticalScrollBar().getValue();
        m_htmlRenderer.setHTML(html);
        getVerticalScrollBar().setValue(value);
    }

    private void updateWordCount()
    {
        if (m_chunk != null)
        {
            String text = m_chunkEditor.getTextEditor().getText();
            int count = WORD_COUNT_PATTERN.split(text).length;
            mDocumentApplication.setStatusMessage("word count: " + count);
        }
    }

    public void store()
    {
        if (m_chunk != null)
        {
            m_chunk.setText(m_chunkEditor.getValue());
            if (mDocumentApplication.shouldSplit(m_chunk))
            {
                Text chunk = m_chunk;
                m_chunk = null;
                mDocumentApplication.doSplit(chunk);
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
            mDocumentApplication.expand(mDocumentApplication.step(m_type, parent));
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
            Text startChunk = m_chunk;
            Text currentChunk = m_chunk;
            TextHolder currentHolder = parent;
            boolean found = false;
            boolean stop = false;
            int cursor = m_chunkEditor.getTextEditor().getCaretPosition();
            while (!found && !stop)
            {
                System.out.println(currentChunk);
                Matcher m = m_findStrategy.getPattern().matcher(currentChunk.text().substring(cursor));
                while (m.find())
                {
                    if (m_findStrategy.accept(m.group()))
                    {
                        if (currentChunk != startChunk)
                        {
                            mDocumentApplication.expand(currentChunk);
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
                    currentHolder = mDocumentApplication.step(m_type, currentHolder);
                    currentChunk = currentHolder.content();
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
            mDocumentApplication.quit();
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
                JScrollPane me = e.getSource() == getVerticalScrollBar() ? MainEditor.this : m_jsp1;
                JScrollPane other = e.getSource() == getVerticalScrollBar() ? m_jsp1 : MainEditor.this;

                int myMax = me.getVerticalScrollBar().getMaximum() - me.getVerticalScrollBar().getVisibleAmount();
                int otherMax = other.getVerticalScrollBar().getMaximum() - other.getVerticalScrollBar().getVisibleAmount();
                float proportion = e.getValue() / (float) myMax;
                m_ignore = true;
                other.getVerticalScrollBar().setValue((int) (otherMax * proportion));
                m_ignore = false;
            }
        }
    }

    /**
     * creates a popup containing content renderered as html
     */
    private class Render extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (popUpRenderer==null) {
                JFrame jf = (JFrame) SwingUtilities.getRoot(MainEditor.this);
                popUpRenderer = PopupFactory.getSharedInstance().getPopup(MainEditor.this, m_jsp1,
                        jf.getLocationOnScreen().x + 10, jf.getLocationOnScreen().y + 30);
                popUpRenderer.show();
            }
            else {
                popUpRenderer.hide();
                popUpRenderer = null;
            }
        }
    }
}
