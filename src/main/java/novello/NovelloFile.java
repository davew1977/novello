/*
 *
 * Date: 2009-nov-22
 * Author: davidw
 *
 */
package novello;

public class NovelloFile
{
    private FileType m_fileType;
    private String m_path;

    public NovelloFile(FileType fileType, String path)
    {
        m_fileType = fileType;
        m_path = path;
    }

    public NovelloFile()
    {
    }

    public FileType getFileType()
    {
        return m_fileType;
    }

    public void setFileType(FileType fileType)
    {
        m_fileType = fileType;
    }

    public String getPath()
    {
        return m_path;
    }

    public void setPath(String path)
    {
        m_path = path;
    }

    public String toString()
    {
        return m_path + (m_fileType == FileType.SVN ? " (svn)":"");
    }
}
