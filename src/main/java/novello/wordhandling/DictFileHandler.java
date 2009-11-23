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

public class DictFileHandler
{
    public static Dictionary load(String zip)
    {
        try
        {
            Dictionary dict = new Dictionary();
            ZipFile z = new ZipFile(zip);
            Enumeration<? extends ZipEntry> enumeration = z.entries();
            while (enumeration.hasMoreElements())
            {
                ZipEntry entry =  enumeration.nextElement();
                if(entry.getName().endsWith("txt"))
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
        Dictionary dictionary = DictFileHandler.load("dictionaries/british.zip");
        List<String> words = dictionary.findWords("H");
        System.out.println("words = " + words);
    }

    public static Dictionary loadBritish()
    {
        return DictFileHandler.load("dictionaries/british.zip");
    }
}
