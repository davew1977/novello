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
import com.xapp.application.editor.widgets.TextEditor;
import com.xapp.objectmodelling.tree.TreeNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;

import novello.wordhandling.DictFileHandler;

public class MainEditor extends JSplitPane
{
    private NovelloApp m_novelloApp;
    private BrowserView m_browserView;
    private ChunkEditor m_chunkEditor;
    private TextChunk m_chunk;
    private Content m_parentContent;

    public MainEditor(NovelloApp novelloApp)
    {
        super(VERTICAL_SPLIT);
        m_novelloApp = novelloApp;
        m_browserView = new BrowserView();
        m_chunkEditor = new ChunkEditor();

        final JScrollPane jsp = new JScrollPane(m_browserView);
        jsp.setPreferredSize(new Dimension(300, 400));

        add(jsp);

        final JScrollPane jsp2 = m_chunkEditor.getComponent();
        add(jsp2);


        m_chunkEditor.getTextEditor().addAction("control S", new SaveAction());
        m_chunkEditor.getTextEditor().addAction("control SPACE", new PopUpMenuAction());
        m_chunkEditor.getTextEditor().addAction("alt RIGHT", new StepAction(StepType.next));
        m_chunkEditor.getTextEditor().addAction("alt LEFT", new StepAction(StepType.previous));

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

        m_chunkEditor.setDict(DictFileHandler.loadBritish());
    }

    public void setChunk(TextChunk textChunk, Content parentContent)
    {
        store();
        m_chunk = textChunk;
        m_parentContent = parentContent;
        render();
        m_chunkEditor.setValue(textChunk.getText(), null);
    }

    private void render()
    {
        HTML html = new HTMLImpl();
        updateWordCount();
        html.p(m_chunk.getText());
        m_browserView.setHTML(html);
    }

    private void updateWordCount()
    {
        if (m_chunk!=null)
        {
            int count = m_chunk.getText().split("\\s+").length;
            m_novelloApp.getAppContainer().setStatusMessage("word count: " + count);
        }
    }

    private void store()
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


    private class SaveAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            store();
            render();
            m_novelloApp.getAppContainer().save();
        }
    }


    private class PopUpMenuAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            TextEditor textEditor = m_chunkEditor.getTextEditor();
            textEditor.newPopUp();
            textEditor.addPopUpAction(new GotoAction(m_novelloApp.getBook().getSection(), "goto"));
            textEditor.addInsertAction("make split", "-->split");
            textEditor.showPopUp();
        }
    }

    private class GotoAction extends AbstractAction
    {
        Object m_subject;

        public GotoAction(Section section, String name)
        {
            super(name);
            m_subject = section;
        }

        public void actionPerformed(ActionEvent e)
        {
            if (m_subject instanceof Section)
            {
                Section section = (Section) m_subject;

                TextEditor textEditor = m_chunkEditor.getTextEditor();
                textEditor.newPopUp();

                if(section instanceof Content)
                {
                    Content content = (Content) section;
                    m_novelloApp.getAppContainer().expand(content);
                }
                else
                {
                    java.util.List<TreeNode> children = section.getChildren();
                    for (TreeNode child : children)
                    {
                        textEditor.addPopUpAction(new GotoAction((Section) child, child.getName()));
                    }
                    textEditor.showPopUp();
                }
            }
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
        }
    }
}
