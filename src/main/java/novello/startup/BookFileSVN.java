/*
 *
 * Date: 2009-nov-29
 * Author: davidw
 *
 */
package novello.startup;

public class BookFileSVN extends BookFile
{
    private String m_checkoutFolder;
    private String m_svnUsername;
    private String m_svnPassword;

    public BookFileSVN(String location, String checkoutFolder, String svnUsername, String svnPassword)
    {
        super(location);
        m_checkoutFolder = checkoutFolder;
        m_svnUsername = svnUsername;
        m_svnPassword = svnPassword;
    }

    public BookFileSVN()
    {
    }

    public String getCheckoutFolder()
    {
        return m_checkoutFolder;
    }

    public void setCheckoutFolder(String checkoutFolder)
    {
        m_checkoutFolder = checkoutFolder;
    }

    public String getSvnUsername()
    {
        return m_svnUsername;
    }

    public void setSvnUsername(String svnUsername)
    {
        m_svnUsername = svnUsername;
    }

    public String getSvnPassword()
    {
        return m_svnPassword;
    }

    public void setSvnPassword(String svnPassword)
    {
        m_svnPassword = svnPassword;
    }

    public String toString()
    {
        return super.toString() + " (svn)";
    }
}
