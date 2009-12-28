/*
 *
 * Date: 2009-nov-22
 * Author: davidw
 *
 */
package novello.startup;

import static com.xapp.application.utils.SwingUtils.*;
import com.xapp.application.utils.SwingUtils;
import com.xapp.marshalling.Marshaller;
import com.xapp.utils.FileUtils;
import com.xapp.utils.StringUtils;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import static javax.swing.Box.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;

import novello.startup.BookFile;
import novello.*;

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
    private JTextField m_saveLocationTF;
    private JButton m_createFileButton;
    private JButton m_saveFileOkButton;
    private JFrame m_jd;
    private StartupScreen.QuickLaunchAction m_quickLaunchAction = new QuickLaunchAction();
    private StartupScreen.LaunchSVNAction m_launchSVNAction = new LaunchSVNAction();

    public StartupScreen(final LauncherData launcherData)
    {
        super();

        Box r1 = createLabelRow(20, "What would you like to do?", 400, "25", null);
        r1.setBorder(BorderFactory.createEtchedBorder());

        m_recentlyOpenedCombo = createRecentlyOpenedCombo(launcherData);
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
        m_password.setAction(m_launchSVNAction);
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

        Box r11 = createLabelRow(30, "Create a new book on your computer:", 400, "20", null);
        m_saveLocationTF = createTF(150,20);
        m_saveLocationTF.setEditable(false);
        m_createFileButton = createButton("Create File...", 90,20);
        Box r12 = createHorizBox(createHorizontalStrut(50),
                createLabel("Location :", 120, null, 20),
                m_saveLocationTF,
                createHorizontalStrut(5), m_createFileButton,createHorizontalGlue());

        m_saveFileOkButton = createButton("OK", 90,20);
        Box r13 = createHorizBox(createHorizontalStrut(332), m_saveFileOkButton, createHorizontalGlue());

        Box bottom = createVertBox(r8, r9, createVerticalStrut(5), r10,createVerticalStrut(10));
        Box lowerbottom = createVertBox(r11,r12,createVerticalStrut(5), r13,createVerticalStrut(10));
        bottom.setBorder(BorderFactory.createEtchedBorder());
        lowerbottom.setBorder(BorderFactory.createEtchedBorder());

        m_mainBox = createHorizBox(createHorizontalStrut(5),
                createVertBox(createVerticalStrut(5), r1,top,middle, bottom,lowerbottom,createVerticalStrut(5)), 
                createHorizontalStrut(5));

        setFont(r2, "Tahoma-12");
        setFont(r4, "Tahoma-12");
        setFont(r5, "Tahoma-12");
        setFont(r6, "Tahoma-12");
        setFont(r7, "Tahoma-12");
        setFont(r9, "Tahoma-12");
        setFont(r10, "Tahoma-12");
        setFont(r12, "Tahoma-12");
        setFont(r13, "Tahoma-12");

        updateViewState();

        m_browseFileButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser c = new JFileChooser(NovelloLauncher.HOME_DIR);
                setFont(c,"Tahoma-12");
                int r = c.showOpenDialog(m_mainBox);
                if(r== JFileChooser.APPROVE_OPTION)
                {
                    m_locationTF.setText(c.getSelectedFile().getAbsolutePath());
                }
            }
        });
        m_createFileButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser c = new JFileChooser(NovelloLauncher.HOME_DIR);
                setFont(c,"Tahoma-12");
                int r = c.showSaveDialog(m_mainBox);
                if(r== JFileChooser.APPROVE_OPTION)
                {
                    m_saveLocationTF.setText(c.getSelectedFile().getAbsolutePath());
                }
            }
        });
        m_browseCheckoutLocationButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser c = new JFileChooser(NovelloLauncher.HOME_DIR);
                c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                setFont(c,"Tahoma-12");
                int r = c.showOpenDialog(m_mainBox);
                if(r== JFileChooser.APPROVE_OPTION)
                {
                    m_checkoutLocation.setText(c.getSelectedFile().getAbsolutePath());
                }
            }
        });
        m_quickLaunchOkbutton.setAction(m_quickLaunchAction);
        m_openSVNBookButton.setAction(m_launchSVNAction);
        m_localFileOkButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                m_bookfile = new BookFile(m_locationTF.getText());
                m_startupCallback.startNovello(m_bookfile);
            }
        });
        m_saveFileOkButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String fileName = m_saveLocationTF.getText();
                File f = new File(fileName);
                if(!f.exists() || SwingUtils.askUser(m_mainBox, "Overwrite " + f.getName() + "?"))
                {
                    Book b = new Book();
                    Section section = new Section();
                    b.setSection(section);
                    section.setName(f.getName().substring(0,f.getName().lastIndexOf(".")));
                    new Marshaller<Book>(Book.class).marshal(f, b);
                    m_bookfile = new BookFile(fileName);
                    m_startupCallback.startNovello(m_bookfile);
                }
            }
        });
        m_checkURLButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String text = m_svnLocationTF.getText();
                if(text==null || text.equals(""))
                {
                    SwingUtils.warnUser(m_mainBox, "please enter a url");
                    return;
                }
                InputStream is = null;
                try
                {
                    is = FileUtils.openStream(text);
                }
                catch (RuntimeException e1)
                {
                    if(e1.getCause() instanceof MalformedURLException)
                    {
                        SwingUtils.warnUser(m_mainBox, "url is should look something like:\n http://www.foo.com/svn/boo/bar.xml");
                        return;
                    }
                    else
                    {
                        throw e1;
                    }
                }
                SwingUtils.warnUser(m_mainBox, is!=null ? "url seems ok" : "cannot access url");
            }
        });
    }

    public void setStartupCallback(StartupCallback startupCallback)
    {
        m_startupCallback = startupCallback;
    }

    private JComboBox createRecentlyOpenedCombo(LauncherData appData)
    {
        BookFile[] bookFiles = appData.getRecentlyOpened().toArray(new BookFile[appData.getRecentlyOpened().size()]);
        final JComboBox combo = new JComboBox(bookFiles);
        combo.setRenderer(new DefaultListCellRenderer()
        {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                String text = null;
                if (value!=null)
                {
                    BookFile bf = (BookFile) value;
                    setToolTipText(bf.getLocation());
                    String[] chunks = bf.getLocation().split("[/\\\\]");
                    text = chunks[chunks.length - 1] + (bf instanceof BookFileSVN ? " (svn)":"");
                }
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });
        combo.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                if(e.getKeyChar()== KeyEvent.VK_ENTER && !combo.isPopupVisible())
                {
                    m_quickLaunchAction.actionPerformed(null);
                }
            }
        });
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

    public JFrame getDialog()
    {
        if(m_jd ==null)
        {
            m_jd = new JFrame("Novello");
            m_jd.setContentPane(m_mainBox);
            m_jd.pack();
            m_jd.setLocationRelativeTo(null);
            m_jd.setIconImage(NovelloTreeGraphics.BOOK_ICON.getImage());
        }
        return m_jd;
    }

    public static void main(String[] args)
    {
        LauncherData ap = new LauncherData();
        ap.getRecentlyOpened().add(new BookFileSVN("http://boo", "folders/boo", "egg", "head"));
        ap.getRecentlyOpened().add(new BookFileSVN("http://foo", "folders/foo", "egg", "head"));
        ap.getRecentlyOpened().add(new BookFile("local/hussl.xml"));
        StartupCallback callback = new StartupCallback()
        {
            public void startNovello(BookFile bookFile)
            {
                System.out.println(bookFile);
            }
        };
        StartupScreen startupScreen = new StartupScreen(ap);
        startupScreen.setStartupCallback(callback);
        startupScreen.getDialog().setVisible(true);
    }

    private class QuickLaunchAction extends AbstractAction
    {
        public QuickLaunchAction()
        {
            super("OK");
        }

        public void actionPerformed(ActionEvent e)
        {
            m_bookfile = (BookFile) m_recentlyOpenedCombo.getSelectedItem();
            if (m_bookfile!=null)
            {
                m_startupCallback.startNovello(m_bookfile);
            }
            else
            {
                SwingUtils.warnUser(m_mainBox, "No book selected");
            }
        }
    }

    private class LaunchSVNAction extends AbstractAction
    {
        private LaunchSVNAction()
        {
            super("OK");
        }

        public void actionPerformed(ActionEvent e)
        {
            String checkoutFolder = m_checkoutLocation.getText();
            String svnLocation = m_svnLocationTF.getText();
            String username = m_username.getText();
            String password = m_password.getText();
            String error = "";
            error += StringUtils.isNullOrEmpty(checkoutFolder) ? "enter checkout folder\n" : "";
            error += StringUtils.isNullOrEmpty(svnLocation) ? "enter svn location\n" : "";
            if(!error.equals(""))
            {
                SwingUtils.warnUser(m_mainBox, error);
                return;
            }
            if(StringUtils.isNullOrEmpty(username))
            {
                if(!SwingUtils.askUser(m_mainBox, "Are you sure you want to launch without credentials?"))
                {
                    return;
                }
            }
            m_bookfile = new BookFileSVN(svnLocation, checkoutFolder, username, password);
            m_startupCallback.startNovello(m_bookfile);
        }
    }
}
