/*
 *
 * Date: 2010-jan-09
 * Author: davidw
 *
 */
package novello.goodreads;

import com.xapp.utils.FileUtils;
import com.xapp.utils.StringUtils;
import com.xapp.application.api.Launcher;
import com.xapp.marshalling.Unmarshaller;
import com.xapp.marshalling.stringserializers.StringArraySerializer;
import com.xapp.marshalling.stringserializers.StringListSerializer;

import java.util.List;

public class Library
{
    private List<BookMeta> m_books;

    public List<BookMeta> getBooks()
    {
        return m_books;
    }

    public void setBooks(List<BookMeta> books)
    {
        m_books = books;
    }

    public static void main(String[] args)
    {
        Library lib = Unmarshaller.load(Library.class, "booklist.xml");
        StringBuilder sb = new StringBuilder();
        sb.append("<table>\n");
        sb.append("<tr><td>Author</td><td>Title</td><td>link</td><td>Tags</td>\n");
        for (int i = 0; i < lib.getBooks().size(); i++)
        {
            BookMeta bookMeta = lib.getBooks().get(i);

            Object author = bookMeta.getAuthor();
            Object title = bookMeta.getName();
            Object isbn = bookMeta.getIsbn();
            Object link = String.format("<a href=\"http://www.amazon.com/dp/%s\">%s</a>",isbn, isbn);
            String tags = StringUtils.convertToString(bookMeta.getTags());
            sb.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n", i+1,author, title, link, tags));
        }
        sb.append("</table>\n");

        System.out.println(sb);

        String s = "";
        for (BookMeta bookMeta : lib.getBooks())
        {
            s+=bookMeta.getEmail() + ",";
        }

        System.out.println(s);
        
        //find longest name
        int longest=0;
        for (BookMeta bookMeta : lib.getBooks())
        {
            longest = Math.max(bookMeta.getAuthor().length(), longest);
        }
        
        s="";
        for (BookMeta bookMeta : lib.getBooks())
        {
            String a = bookMeta.getAuthor();
            String isbn = bookMeta.getIsbn();
            s+= String.format("<a href=\"http://www.amazon.com/dp/%s\">%s</a>", isbn, a);
            for(int i=0; i<longest+2-a.length(); i++)
            {
                s+=" ";
            }
            s+=bookMeta.getTags() + "\n";
        }
        System.out.println(s);
        Launcher.run(Library.class, "booklist.xml");
    }
}
