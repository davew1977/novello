/*
 *
 * Date: 2009-nov-15
 * Author: davidw
 *
 */
package novello;

public class AppData
{
    private String lastSelected;
    private int m_dividerLocation;

    public String getLastSelected()
    {
        return lastSelected;
    }

    public void setLastSelected(String lastEdited)
    {
        lastSelected = lastEdited;
    }

    public void setDividerLocation(int dividerLocation)
    {
        m_dividerLocation = dividerLocation;
    }

    public int getDividerLocation()
    {
        return m_dividerLocation;
    }
}
