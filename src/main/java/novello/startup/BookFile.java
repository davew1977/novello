/*
 *
 * Date: 2009-nov-22
 * Author: davidw
 *
 */
package novello.startup;

import novello.FileType;
import com.xapp.objectmodelling.annotations.ValidImplementations;

@ValidImplementations({BookFileSVN.class})
public class BookFile
{
    private String m_location;

    public BookFile(String location)
    {
        m_location = location;
    }

    public BookFile()
    {
    }

    public String getLocation()
    {
        return m_location;
    }

    public void setLocation(String location)
    {
        m_location = location;
    }

    public String toString()
    {
        return m_location;
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BookFile)) return false;

        BookFile bookFile = (BookFile) o;

        if (m_location != null ? !m_location.equals(bookFile.m_location) : bookFile.m_location != null) return false;

        return true;
    }

    public int hashCode()
    {
        return (m_location != null ? m_location.hashCode() : 0);
    }
}
