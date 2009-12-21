/*
 *
 * Date: 2009-dec-16
 * Author: davidw
 *
 */
package novello.help;

import com.xapp.application.utils.SwingUtils;
import com.xapp.utils.FileUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ReferenceCard extends Box
{
    private JEditorPane m_liveTemplatePane;
    private JEditorPane m_keyCommandPane;

    public ReferenceCard()
    {
        super(BoxLayout.PAGE_AXIS);
        m_liveTemplatePane = new JEditorPane("text/html", FileUtils.readURL("/novello/help/livetemplates.html"));
        TitledBorder titleBorder = BorderFactory.createTitledBorder("Live Templates");
        titleBorder.setTitleColor(Color.decode("0x006600"));
        m_liveTemplatePane.setBorder(titleBorder);
        add(m_liveTemplatePane);
        m_keyCommandPane = new JEditorPane("text/html", FileUtils.readURL("/novello/help/key commands.html"));
        titleBorder = BorderFactory.createTitledBorder("Key Commands");
        titleBorder.setTitleColor(Color.decode("0x006600"));
        m_keyCommandPane.setBorder(titleBorder);

        add(m_keyCommandPane);
        SwingUtils.setFont(this , Font.decode("Tahoma-BOLD-12"));

        m_liveTemplatePane.setPreferredSize(new Dimension(260,250));
        m_keyCommandPane.setPreferredSize(new Dimension(260,600));
    }

    public JScrollPane wrapInScrollPane()
    {
        JScrollPane jsp = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setPreferredSize(new Dimension(280,600));
        return jsp;
    }

    public static void main(String[] args)
    {
        SwingUtils.DEFAULT_FONT = Font.decode("Tahoma-11");
        JScrollPane content = new ReferenceCard().wrapInScrollPane();
        SwingUtils.showInFrame(content);
    }
}
