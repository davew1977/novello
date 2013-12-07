/*
 *
 * Date: 2009-nov-20
 * Author: davidw
 *
 */
package novello.wordhandling;

import net.sf.xapp.utils.FileUtils;
import novello.NovelloLauncher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DictFileHandler
{
    private static Dictionary instance;

    public static DictionaryImpl loadFromClasspath(String url)
    {
        File f;
        try
        {
            f = new File(DictFileHandler.class.getResource(url).toURI());
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
        return load(f);
    }

    public static DictionaryImpl load(File f)
    {
        DictionaryImpl dict = new DictionaryImpl();
        try
        {
            ZipFile z = new ZipFile(f);
            Enumeration<? extends ZipEntry> enumeration = z.entries();
            while (enumeration.hasMoreElements())
            {
                ZipEntry entry = enumeration.nextElement();
                if (entry.getName().endsWith("txt"))
                {
                    String words = FileUtils.readInputToString(z.getInputStream(entry));
                    dict.addWords(words.split("\n"));
                }
            }
            return dict;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args)
    {
        DictionaryImpl dictionary = DictFileHandler.loadFromClasspath("/british.zip");
        List<String> words = dictionary.findWords("H");
        System.out.println("words = " + words);
    }

    public static Dictionary getDictionary()
    {
        if(instance == null)
        {
            //hardcode british english
            File cache = new File(NovelloLauncher.HOME_DIR, "_NOVELLO_CACHE");
            File f = new File(cache, "british.zip");
            if (!f.exists())
            {
                cache.mkdirs();
                FileUtils.downloadFile(f, "http://novello.sourceforge.net/webstart/dictionaries/british.zip");
            }
            instance =  new DictionaryCache(load(f));
        }
        return instance;
    }
}
