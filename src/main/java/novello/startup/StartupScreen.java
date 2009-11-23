/*
 *
 * Date: 2009-nov-22
 * Author: davidw
 *
 */
package novello.startup;

import static com.xapp.application.utils.SwingUtils.*;

import javax.swing.*;
import static javax.swing.Box.*;
import java.awt.*;

import novello.AppData;
import novello.NovelloFile;
import novello.FileType;
import novello.NovelloTreeGraphics;

public class StartupScreen
{
    Box m_mainBox;
    public StartupScreen(AppData appData)
    {
        super();

        Box r1 = createLabelRow(20, "What would you like to do?", 400, "25", null);
        r1.setBorder(BorderFactory.createEtchedBorder());

        NovelloFile[] novelloFiles = appData.getRecentlyOpened().toArray(new NovelloFile[appData.getRecentlyOpened().size()]);
        JComboBox combo = new JComboBox(novelloFiles);
        setComponentSize(150, 20, combo);
        JButton b1 = createButton("OK", 90,20);

        Box r2 = createHorizBox(createHorizontalStrut(50), createLabel("Re-open a book:", 120, null, 20),
                createHorizontalStrut(5), combo, createHorizontalStrut(5), b1, createHorizontalGlue());

        Box top = createVertBox(createVerticalStrut(20), r2, createVerticalStrut(20));
        top.setBorder(BorderFactory.createEtchedBorder());

        Box r3 = createLabelRow(30, "Open book with Subversion:", 400, "20", NovelloTreeGraphics.SVN_ICON);

        JTextField svnLocationTF = createTF(150,20);
        JButton checkURLButton = createButton("Test URL", 90,20);
        Box r4 = createHorizBox(createHorizontalStrut(50), createLabel("SVN location (url):", 120, null, 20),
                svnLocationTF, createHorizontalStrut(5), checkURLButton, createHorizontalGlue());

        JTextField checkoutLocation = createTF(150,20);
        JButton browseFileButton = createButton("Browse...", 90,20);
        Box r5 = createHorizBox(createHorizontalStrut(50),
                createLabel("Location :", 120, null, 20),
                checkoutLocation,
                createHorizontalStrut(5),browseFileButton,createHorizontalGlue());

        JTextField username = createTF(90,20);
        Box r6 = createHorizBox(createHorizontalStrut(50),
                createLabel("SVN Username :", 120, null, 20),
                username,createHorizontalStrut(155),createHorizontalGlue());

        JTextField password = createTF(90,20, true);
        JButton okButton = createButton("OK", 90,20);
        Box r7 = createHorizBox(createHorizontalStrut(50),
                createLabel("SVN Password :", 120, null, 20),
                password,
                createHorizontalStrut(65),okButton,createHorizontalGlue());

        Box middle = createVertBox(r3,r4,
                createVerticalStrut(5), r5,createVerticalStrut(5),
                r6,createVerticalStrut(5),r7, createVerticalStrut(5));
        middle.setBorder(BorderFactory.createEtchedBorder());

        Box r8 = createLabelRow(30, "Open book on your computer:", 400, "20", null);

        JTextField locationTF = createTF(150,20);
        JButton browseFileButton2 = createButton("Browse...", 90,20);
        Box r9 = createHorizBox(createHorizontalStrut(50),
                createLabel("Location :", 120, null, 20),
                locationTF,
                createHorizontalStrut(5),browseFileButton2,createHorizontalGlue());

        JButton localFileOkButton = createButton("OK", 90,20);
        Box r10 = createHorizBox(createHorizontalStrut(332), localFileOkButton, createHorizontalGlue());

        Box bottom = createVertBox(r8, r9, createVerticalStrut(5), r10,createVerticalStrut(10));
        bottom.setBorder(BorderFactory.createEtchedBorder());

        m_mainBox = createHorizBox(createHorizontalStrut(5),
                createVertBox(createVerticalStrut(5), r1,top,middle, bottom,createVerticalStrut(5)), createHorizontalStrut(5));

        setFont(r2, "Tahoma-12");
        setFont(r4, "Tahoma-12");
        setFont(r5, "Tahoma-12");
        setFont(r6, "Tahoma-12");
        setFont(r7, "Tahoma-12");
        setFont(r9, "Tahoma-12");
        setFont(r10, "Tahoma-12");

    }

    private JButton createButton(String label, int w, int h)
    {
        JButton b = new JButton(label);
        setComponentSize(w,h,b);
        return b;
    }

    private JTextField createTF(int w, int h)
    {
        return createTF(w,h,false);
    }
    private JTextField createTF(int w, int h, boolean password)
    {
        JTextField tf = password ? new JPasswordField() : new JTextField();
        setComponentSize(w, h, tf);
        return tf;
    }



    private Box createLabelRow(int indent, String label, int width, String fontSize, ImageIcon svnIcon)
    {
        Box r1 = createHorizontalBox();
        JLabel l = createLabel(label, width, svnIcon, 50);
        r1.add(createHorizontalStrut(indent));
        r1.add(l);
        r1.add(createHorizontalGlue());
        setFont(r1, "Tahoma-" + fontSize);
        return r1;
    }

    private JLabel createLabel(String label, int width, ImageIcon svnIcon, int height)
    {
        JLabel l = new JLabel(label);
        if (svnIcon != null)
        {
            l.setIcon(svnIcon);
        }
        setComponentSize(width, height, l);
        return l;
    }

    private void setComponentSize(int width, int height, JComponent l)
    {
        l.setPreferredSize(new Dimension(width, height));
        l.setMinimumSize(new Dimension(width, height));
        if (l instanceof JTextField || l instanceof JComboBox)
        {
            l.setMaximumSize(new Dimension(Short.MAX_VALUE, height));
        }
    }

    public void openDialog()
    {
        JDialog jd = new JDialog((JFrame) null, "Novello", true);
        jd.setContentPane(m_mainBox);
        jd.pack();
        jd.setLocationRelativeTo(null);
        jd.setVisible(true);
    }

    public static void main(String[] args)
    {
        AppData ap = new AppData();
        ap.getRecentlyOpened().add(new NovelloFile(FileType.SVN, "http://boo"));
        ap.getRecentlyOpened().add(new NovelloFile(FileType.SVN, "http://foo"));
        ap.getRecentlyOpened().add(new NovelloFile(FileType.FILESYSTEM, "local/hussl.xml"));
        new StartupScreen(ap).openDialog();
    }
}
