/*
 *
 * Date: 2009-nov-21
 * Author: davidw
 *
 */
package novello.help;

import net.sf.xapp.application.utils.SwingUtils;
import net.sf.xapp.application.utils.html.HTML;
import net.sf.xapp.application.utils.html.HTMLImpl;
import novello.NovelloTreeGraphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutPane extends JPanel
{
    private JButton m_labelButton;
    private JButton m_bookButton;

    public AboutPane()
    {
        HTML html = new HTMLImpl();
        html.append("Novello copyright 2009 David Webber<br>");
        html.append("This project is open source.<br>");
        html.link("http://novello.sourceforge.net", "http://novello.sourceforge.net");
        html.append("<br>If you like Novello you can donate, <br>contribute with code, or buy my book, \"Oom\"");
        m_labelButton = new JButton(html.htmlDoc());
        add(m_labelButton);
        m_bookButton = new JButton(NovelloTreeGraphics.OOM_ICON);
        add(m_bookButton);
        SwingUtils.setFont(this);
        m_bookButton.addActionListener(new OpenLinkAction("http://www.amazon.com/dp/1439261873"));
        m_labelButton.addActionListener(new OpenLinkAction("http://novello.sourceforge.net"));

        SwingUtils.setComponentSize(80,80, m_bookButton);
        SwingUtils.setComponentSize(245,80, m_labelButton);
    }


    public static void main(String[] args)
    {
        AboutPane aboutPane = new AboutPane();
        SwingUtils.showInFrame(aboutPane);
        System.out.println(aboutPane.m_labelButton.getSize());
    }

    private class OpenLinkAction implements ActionListener
    {
        private String m_url;

        public OpenLinkAction(String s)
        {
            m_url = s;
        }

        public void actionPerformed(ActionEvent e)
        {

            try
            {
                Desktop.getDesktop().browse(new URI(m_url));
            }
            catch (IOException e1)
            {
                throw new RuntimeException(e1);
            }
            catch (URISyntaxException e1)
            {
                throw new RuntimeException(e1);
            }
        }
    }
}
