/*
 *
 * Date: 2009-dec-11
 * Author: davidw
 *
 */
package novello;

import javax.swing.*;
import java.awt.event.ActionEvent;

class SaveAction extends AbstractAction
{
    private MainEditor m_mainEditor;
    private DocumentApplication documentApp;

    public SaveAction(MainEditor mainEditor, DocumentApplication docApp)
    {
        super("Save", NovelloTreeGraphics.SAVE_ICON);
        m_mainEditor = mainEditor;
        documentApp = docApp;
    }

    public void actionPerformed(ActionEvent e)
    {
        save();
    }

    public void save()
    {
        m_mainEditor.store();
        m_mainEditor.render();
        documentApp.getAppContainer().save();
    }
}
