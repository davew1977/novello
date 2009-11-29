/*
 *
 * Date: 2009-nov-15
 * Author: davidw
 *
 */
package novello;

import com.xapp.objectmodelling.annotations.Reference;

import java.util.List;
import java.util.ArrayList;

import novello.startup.BookFile;

public class AppData
{
    private Content m_lastEdited;

    private List<BookFile> m_recentlyOpened = new ArrayList<BookFile>();
    private BookFile m_lastOpened;

    @Reference
    public Content getLastEdited()
    {
        return m_lastEdited;
    }

    public void setLastEdited(Content lastEdited)
    {
        m_lastEdited = lastEdited;
    }

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
}
