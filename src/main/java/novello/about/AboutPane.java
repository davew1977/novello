/*
 *
 * Date: 2009-nov-21
 * Author: davidw
 *
 */
package novello.about;

import com.xapp.application.utils.SwingUtils;
import com.xapp.application.utils.html.HTML;
import com.xapp.application.utils.html.HTMLImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

import novello.NovelloTreeGraphics;

public class AboutPane extends JPanel
{
    public AboutPane()
    {
        HTML html = new HTMLImpl();
        html.append("Novello © 2009 David Webber<br>");
        html.append("This project is open source.<br>");
        html.link("http://novello.sourceforge.net", "http://novello.sourceforge.net");
        html.append("<br>If you like Novello you can donate, <br>contribute with code, or buy my book, \"Oom\"");
        JButton labelButton = new JButton(html.htmlDoc());
        add(labelButton);
        JButton b = new JButton(NovelloTreeGraphics.OOM_ICON);
        add(b);
        SwingUtils.setFont(this);
        b.addActionListener(new OpenLinkAction("http://dj-webber.com"));
        labelButton.addActionListener(new OpenLinkAction("http://novello.sourceforge.net"));
    }


    public static void main(String[] args)
    {
        SwingUtils.showInFrame(new AboutPane());
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
