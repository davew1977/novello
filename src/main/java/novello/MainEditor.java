/*
 *
 * Date: 2009-nov-15
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.editor.widgets.TextEditor;
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

public class MainEditor extends JSplitPane
{
    NovelloApp m_novelloApp;
    private BrowserView m_browserView;
    ChunkEditor m_chunkEditor;
    private TextChunk m_chunk = new TextChunk();
    private Content m_parentContent;
    private Pattern WORD_COUNT_PATTERN = Pattern.compile("\\s+");

    public MainEditor(NovelloApp novelloApp)
    {
        super(VERTICAL_SPLIT);
        m_novelloApp = novelloApp;
        m_browserView = new BrowserView();
        m_chunkEditor = new ChunkEditor();
        m_chunkEditor.setNovelloApp(m_novelloApp);

        final JScrollPane jsp = new JScrollPane(m_browserView);
        jsp.setPreferredSize(new Dimension(300, 400));

        add(jsp);

        final JScrollPane jsp2 = m_chunkEditor.getComponent();
        add(jsp2);


        m_chunkEditor.getTextEditor().addAction("control S", new SaveAction(this, novelloApp));
        m_chunkEditor.getTextEditor().addAction("control Q", new QuitAction());
        m_chunkEditor.getTextEditor().addAction("control SPACE", new PopUpMenuAction());
        m_chunkEditor.getTextEditor().addAction("alt RIGHT", new StepAction(StepType.next));
        m_chunkEditor.getTextEditor().addAction("alt LEFT", new StepAction(StepType.previous));
        m_chunkEditor.getTextEditor().addAction("F2", new FindError(StepType.next));

        jsp.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
        {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                jsp2.getVerticalScrollBar().setValue(e.getValue());
            }
        });
        jsp2.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
        {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                jsp.getVerticalScrollBar().setValue(e.getValue());
            }
        });

        Dictionary dictionary = DictFileHandler.loadDictionary("en_uk");
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
        m_chunkEditor.setValue(textChunk.getText(), null);
        updateWordCount();
    }

    public void render()
    {
        HTML html = new HTMLImpl();
        html.p(m_chunk.getText());
        m_browserView.setHTML(html);
    }

    private void updateWordCount()
    {
        if (m_chunk!=null)
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


    private class PopUpMenuAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            TextEditor textEditor = m_chunkEditor.getTextEditor();
            textEditor.newPopUp();
            textEditor.addPopUpAction(new GotoAction(MainEditor.this, m_novelloApp.getBook().getSection(), "goto"));
            textEditor.addInsertAction("make split", "-->split");
            m_chunkEditor.addPopupActions();

            textEditor.showPopUp();
        }
    }

    public enum StepType
    {
        next, previous
    }

    private class StepAction extends AbstractAction
    {
        private StepType m_type;

        private StepAction(StepType type)
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

    private class FindError extends AbstractAction
    {
        private StepType m_type;

        private FindError(StepType type)
        {
            super(type.toString());
            m_type = type;
        }

        public void actionPerformed(ActionEvent e)
        {
            store();
            TextChunk startChunk = m_chunk;
            TextChunk currentChunk = m_chunk;
            Content currentContent = m_parentContent;
            boolean found = false;
            boolean stop = false;
            int cursor= m_chunkEditor.getTextEditor().getCaretPosition();
            while(!found && !stop)
            {
                System.out.println(currentChunk);
                Matcher m = m_chunkEditor.WORD.matcher(currentChunk.getText());
                while(m.find())
                {
                    if(m.end()>cursor && !m_chunkEditor.m_dict.wordOk(m.group()))
                    {
                        if(currentChunk!=startChunk)
                        {
                            m_novelloApp.getAppContainer().expand(currentChunk);
                        }
                        m_chunkEditor.getTextEditor().requestFocus();
                        m_chunkEditor.getTextEditor().setCaretPosition(m.start());
                        m_chunkEditor.getTextEditor().setSelectionStart(m.start());
                        m_chunkEditor.getTextEditor().setSelectionEnd(m.end());
                        found=true;
                        break;
                    }
                }
                if(!found)
                {
                    currentContent = m_novelloApp.getBook().step(m_type, currentContent);
                    currentChunk = currentContent.latest();
                    cursor = 0;
                }
                if(startChunk==currentChunk)
                {
                    stop=true;
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
}
