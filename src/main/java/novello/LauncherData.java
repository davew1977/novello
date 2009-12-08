/*
 *
 * Date: 2009-dec-02
 * Author: davidw
 *
 */
package novello;

import novello.startup.BookFile;
import novello.startup.BookFileSVN;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

import com.xapp.marshalling.Unmarshaller;
import com.xapp.marshalling.Marshaller;

public class LauncherData
{
    private List<BookFile> m_recentlyOpened = new ArrayList<BookFile>();
    private BookFile m_lastOpened;
    private static File LAUNCHER_FILE = new File(NovelloLauncher.HOME_DIR, "launcher-data.xml");

    public List<BookFile> getRecentlyOpened()
    {
        return m_recentlyOpened;
    }

    public void setRecentlyOpened(List<BookFile> recentlyOpened)
    {
        m_recentlyOpened = recentlyOpened;
    }

    public BookFile getLastOpened()
    {
        return m_lastOpened;
    }

    public void setLastOpened(BookFile lastOpened)
    {
        m_lastOpened = lastOpened;
    }

    public static LauncherData load()
    {
        if(LAUNCHER_FILE.exists())
        {
            return Unmarshaller.load(LauncherData.class, LAUNCHER_FILE.getAbsolutePath());
        }
        else
        {
            LauncherData data = new LauncherData();
            BookFile b = new BookFileSVN("https://novello.svn.sourceforge.net/svnroot/novello/books/christmas_carol/christmas-carol.xml", NovelloLauncher.HOME_DIR.getAbsolutePath(), "", "");
            data.getRecentlyOpened().add(b);
            return data;
        }
    }

    public void addRecentlyOpened(BookFile bookfile)
    {
        BookFile match = null;
        for (BookFile ro : m_recentlyOpened)
        {
            if(ro.getLocation().equals(bookfile.getLocation()))
            {
                match = ro;
            }
        }
        if(match!=null)
        {
            m_recentlyOpened.remove(match);
        }
        m_recentlyOpened.add(bookfile);
    }

    public static void save(LauncherData launcherData)
    {
        new Marshaller<LauncherData>(LauncherData.class).marshal(LAUNCHER_FILE, launcherData);
    }
}
