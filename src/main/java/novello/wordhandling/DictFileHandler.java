/*
 *
 * Date: 2009-nov-20
 * Author: davidw
 *
 */
package novello.wordhandling;

import com.xapp.utils.FileUtils;

import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.net.URISyntaxException;

import novello.NovelloLauncher;

public class DictFileHandler
{
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

    public static DictionaryImpl loadDictionary(String language)
    {
        //hardcode british english
        File cache = new File(NovelloLauncher.HOME_DIR, "_NOVELLO_CACHE");
        File f = new File(cache, "british.zip");
        if (!f.exists())
        {
            cache.mkdirs();
            FileUtils.downloadFile(f, "http://novello.sourceforge.net/webstart/dictionaries/british.zip");
        }
        return load(f);
    }
}
