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
    private NovelloApp m_novelloApp;

    public SaveAction(MainEditor mainEditor, NovelloApp novelloApp)
    {
        super("Save", NovelloTreeGraphics.SAVE_ICON);
        m_mainEditor = mainEditor;
        m_novelloApp = novelloApp;
    }

    public void actionPerformed(ActionEvent e)
    {
        save();
    }

    public void save()
    {
        m_mainEditor.store();
        m_mainEditor.render();
        m_novelloApp.getAppContainer().save();
    }
}
