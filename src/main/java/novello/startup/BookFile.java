/*
 *
 * Date: 2009-nov-22
 * Author: davidw
 *
 */
package novello.startup;

import novello.FileType;
import com.xapp.objectmodelling.annotations.ValidImplementations;
import com.xapp.objectmodelling.annotations.PrimaryKey;

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

    @PrimaryKey
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
}
