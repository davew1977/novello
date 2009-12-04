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

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BookFileSVN)) return false;
        if (!super.equals(o)) return false;

        BookFileSVN that = (BookFileSVN) o;

        if (m_checkoutFolder != null ? !m_checkoutFolder.equals(that.m_checkoutFolder) : that.m_checkoutFolder != null)
            return false;
        if (m_svnPassword != null ? !m_svnPassword.equals(that.m_svnPassword) : that.m_svnPassword != null)
            return false;
        if (m_svnUsername != null ? !m_svnUsername.equals(that.m_svnUsername) : that.m_svnUsername != null)
            return false;

        return true;
    }

    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (m_checkoutFolder != null ? m_checkoutFolder.hashCode() : 0);
        result = 31 * result + (m_svnUsername != null ? m_svnUsername.hashCode() : 0);
        result = 31 * result + (m_svnPassword != null ? m_svnPassword.hashCode() : 0);
        return result;
    }
}
