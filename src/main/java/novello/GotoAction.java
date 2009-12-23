/*
 *
 * Date: 2009-dec-21
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.editor.widgets.TextEditor;
import com.xapp.objectmodelling.tree.TreeNode;

import javax.swing.*;
import java.awt.event.ActionEvent;

class GotoAction extends AbstractAction
{
    Object m_subject;
    private MainEditor m_mainEditor;

    public GotoAction(MainEditor mainEditor, Section section, String name)
    {
        super(name);
        m_mainEditor = mainEditor;
        m_subject = section;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (m_subject instanceof Section)
        {
            Section section = (Section) m_subject;

            TextEditor textEditor = m_mainEditor.m_chunkEditor.getTextEditor();
            textEditor.newPopUp();

            if(section instanceof Content)
            {
                Content content = (Content) section;
                m_mainEditor.m_novelloApp.getAppContainer().expand(content);
            }
            else
            {
                java.util.List<TreeNode> children = section.getChildren();
                for (TreeNode child : children)
                {
                    textEditor.addPopUpAction(new GotoAction(m_mainEditor, (Section) child, child.getName()));
                }
                textEditor.showPopUp();
            }
        }
    }
}
