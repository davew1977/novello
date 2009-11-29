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
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import novello.AppData;
import novello.startup.BookFile;
import novello.NovelloTreeGraphics;

public class StartupScreen
{
    StartupCallback m_startupCallback;
    Box m_mainBox;
    private JTextField m_svnLocationTF;
    private JButton m_checkURLButton;
    private JTextField m_checkoutLocation;
    private JButton m_browseCheckoutLocationButton;
    private JTextField m_username;
    private JTextField m_password;
    private JButton m_openSVNBookButton;
    private JButton m_browseFileButton;
    private JButton m_localFileOkButton;
    private JTextField m_locationTF;
    private JButton m_quickLaunchOkbutton;
    private JComboBox m_recentlyOpenedCombo;
    private BookFile m_bookfile;

    public StartupScreen(AppData appData, StartupCallback startupCallback)
    {
        super();
        m_startupCallback = startupCallback;

        Box r1 = createLabelRow(20, "What would you like to do?", 400, "25", null);
        r1.setBorder(BorderFactory.createEtchedBorder());

        m_recentlyOpenedCombo = createRecentlyOpenedCombo(appData);
        setComponentSize(150, 20, m_recentlyOpenedCombo);
        m_quickLaunchOkbutton = createButton("OK", 90,20);

        Box r2 = createHorizBox(createHorizontalStrut(50), createLabel("Re-open a book:", 120, null, 20),
                createHorizontalStrut(5), m_recentlyOpenedCombo, createHorizontalStrut(5), m_quickLaunchOkbutton, createHorizontalGlue());

        Box top = createVertBox(createVerticalStrut(20), r2, createVerticalStrut(20));
        top.setBorder(BorderFactory.createEtchedBorder());

        Box r3 = createLabelRow(30, "Open book with Subversion:", 400, "20", NovelloTreeGraphics.SVN_ICON);

        m_svnLocationTF = createTF(150,20);
        m_checkURLButton = createButton("Test URL", 90,20);
        Box r4 = createHorizBox(createHorizontalStrut(50), createLabel("SVN location (url):", 120, null, 20),
                m_svnLocationTF, createHorizontalStrut(5), m_checkURLButton, createHorizontalGlue());

        m_checkoutLocation = createTF(150,20);
        m_browseCheckoutLocationButton = createButton("Browse...", 90,20);
        Box r5 = createHorizBox(createHorizontalStrut(50),
                createLabel("Location :", 120, null, 20),
                m_checkoutLocation,
                createHorizontalStrut(5), m_browseCheckoutLocationButton,createHorizontalGlue());

        m_username = createTF(90,20);
        Box r6 = createHorizBox(createHorizontalStrut(50),
                createLabel("SVN Username :", 120, null, 20),
                m_username,createHorizontalStrut(155),createHorizontalGlue());

        m_password = createTF(90,20, true);
        m_openSVNBookButton = createButton("OK", 90,20);
        Box r7 = createHorizBox(createHorizontalStrut(50),
                createLabel("SVN Password :", 120, null, 20),
                m_password,
                createHorizontalStrut(65), m_openSVNBookButton,createHorizontalGlue());

        Box middle = createVertBox(r3,r4,
                createVerticalStrut(5), r5,createVerticalStrut(5),
                r6,createVerticalStrut(5),r7, createVerticalStrut(5));
        middle.setBorder(BorderFactory.createEtchedBorder());

        Box r8 = createLabelRow(30, "Open book on your computer:", 400, "20", null);

        m_locationTF = createTF(150,20);
        m_browseFileButton = createButton("Browse...", 90,20);
        Box r9 = createHorizBox(createHorizontalStrut(50),
                createLabel("Location :", 120, null, 20),
                m_locationTF,
                createHorizontalStrut(5), m_browseFileButton,createHorizontalGlue());

        m_localFileOkButton = createButton("OK", 90,20);
        Box r10 = createHorizBox(createHorizontalStrut(332), m_localFileOkButton, createHorizontalGlue());

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

        updateViewState();

        m_browseFileButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser c = new JFileChooser(".");
                setFont(c,"Tahoma-12");
                int r = c.showOpenDialog(m_mainBox);
                if(r== JFileChooser.APPROVE_OPTION)
                {
                    m_locationTF.setText(c.getSelectedFile().getAbsolutePath());
                }
            }
        });
        m_browseCheckoutLocationButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser c = new JFileChooser(".");
                c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                setFont(c,"Tahoma-12");
                int r = c.showOpenDialog(m_mainBox);
                if(r== JFileChooser.APPROVE_OPTION)
                {
                    m_checkoutLocation.setText(c.getSelectedFile().getAbsolutePath());
                }
            }
        });
        m_quickLaunchOkbutton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                m_bookfile = (BookFile) m_recentlyOpenedCombo.getSelectedItem();
                m_startupCallback.startNovello(m_bookfile);
            }
        });
        m_openSVNBookButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String checkoutFolder = m_checkoutLocation.getText();
                String svnLocation = m_svnLocationTF.getText();
                String username = m_username.getText();
                String password = m_password.getText();
                m_bookfile = new BookFileSVN(svnLocation, checkoutFolder, username, password);
                m_startupCallback.startNovello(m_bookfile);
            }
        });
        m_localFileOkButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                m_bookfile = new BookFile(m_locationTF.getText());
                m_startupCallback.startNovello(m_bookfile);
            }
        });
    }

    private JComboBox createRecentlyOpenedCombo(AppData appData)
    {
        BookFile[] bookFiles = appData.getRecentlyOpened().toArray(new BookFile[appData.getRecentlyOpened().size()]);
        JComboBox combo = new JComboBox(bookFiles);
        combo.setSelectedItem(appData.getLastOpened());
        combo.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                if(e.getStateChange()== ItemEvent.SELECTED)
                {
                    BookFile bookFile = (BookFile) e.getItem();
                    if(bookFile instanceof BookFileSVN)
                    {
                        BookFileSVN fileSVN = (BookFileSVN) bookFile;
                        m_svnLocationTF.setText(fileSVN.getLocation());
                        m_checkoutLocation.setText(fileSVN.getCheckoutFolder());
                        m_username.setText(fileSVN.getSvnUsername());
                        m_password.setText(fileSVN.getSvnPassword());
                        m_locationTF.setText(null);
                    }
                    else
                    {
                        m_locationTF.setText(bookFile.getLocation());
                        m_svnLocationTF.setText(null);
                        m_checkoutLocation.setText(null);
                        m_username.setText(null);
                        m_password.setText(null);
                    }
                }
                updateViewState();
            }
        });
        return combo;
    }

    private void updateViewState()
    {
        m_quickLaunchOkbutton.setEnabled(m_recentlyOpenedCombo.getSelectedItem()!=null);
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
        JDialog jd = new JDialog((JFrame) null, "Novello", false);
        jd.setContentPane(m_mainBox);
        jd.pack();
        jd.setLocationRelativeTo(null);
        jd.setVisible(true);
    }

    public static void main(String[] args)
    {
        AppData ap = new AppData();
        ap.getRecentlyOpened().add(new BookFileSVN("http://boo", "folders/boo", "egg", "head"));
        ap.getRecentlyOpened().add(new BookFileSVN("http://foo", "folders/foo", "egg", "head"));
        ap.getRecentlyOpened().add(new BookFile("local/hussl.xml"));
        new StartupScreen(ap, new StartupCallback()
        {
            public void startNovello(BookFile bookFile)
            {
                System.out.println(bookFile);
            }
        }).openDialog();
    }
}
