/*
 *
 * Date: 2009-dec-21
 * Author: davidw
 *
 */
package novello;

import com.xapp.application.editor.widgets.TextEditor;
import com.xapp.objectmodelling.tree.Tree;
import com.xapp.objectmodelling.tree.TreeNode;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GotoAction extends AbstractAction
{
    Tree tree;
    private MainEditor m_mainEditor;

    public GotoAction(MainEditor mainEditor, Tree section, String name)
    {
        super(name);
        m_mainEditor = mainEditor;
        tree = section;
    }

    public void actionPerformed(ActionEvent e)
    {
        TextEditor textEditor = m_mainEditor.m_chunkEditor.getTextEditor();
        textEditor.newPopUp();

        if(!tree.hasChildren())
        {
            m_mainEditor.mDocumentApplication.expand(tree);
        }
        else
        {
            java.util.List<TreeNode> children = tree.getChildren();
            for (TreeNode child : children)
            {
                textEditor.addPopUpAction(new GotoAction(m_mainEditor, (Tree) child, child.getName()));
            }
            textEditor.showPopUp();
        }
    }
}
