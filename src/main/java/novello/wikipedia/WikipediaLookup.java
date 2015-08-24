/*
 *
 * Date: 2009-dec-29
 * Author: davidw
 *
 */
package novello.wikipedia;

import net.sf.xapp.marshalling.Unmarshaller;
import net.sf.xapp.utils.FileUtils;
import novello.utils.Lookup;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

public class WikipediaLookup implements Lookup<String, WikipediaResponse>
{
    public static final String WIKIPEDIA_API="http://en.wikipedia.org/w/api.php?action=query&list=search&srwhat=text&format=xml&srsearch=%s";
    public static final Unmarshaller<WikipediaResponse> RESULT_UNMARSHALLER = new Unmarshaller<WikipediaResponse>(WikipediaResponse.class);


    public WikipediaResponse lookup(String key)
    {
        String url = String.format(WIKIPEDIA_API, encodeAsURL(key));
        String content = FileUtils.downloadToString(url);
        System.out.println(content);
        //String content = "<?xml version=\"1.0\"?><api><query><searchinfo totalhits=\"303670\" /><search><p ns=\"0\" title=\"Line\" snippet=\"&lt;span class=&#039;searchmatch&#039;&gt;Line&lt;/span&gt; or lines may refer to:  People : Aaran Lines , New Zealand international football (soccer) player. Dick Lines  (born 1938), former Canadian &lt;b&gt;...&lt;/b&gt; \" size=\"4321\" wordcount=\"563\" timestamp=\"2009-12-17T13:29:35Z\" /><p ns=\"0\" title=\"Líně\" snippet=\"&lt;span class=&#039;searchmatch&#039;&gt;Líně&lt;/span&gt; is a village and municipality (obec ) in Plzeň-North District  in the Plzeň Region  of the Czech Republic. The municipality covers an  &lt;b&gt;...&lt;/b&gt; \" size=\"1479\" wordcount=\"179\" timestamp=\"2009-08-20T11:20:54Z\" /><p ns=\"0\" title=\"Line (geometry)\" snippet=\"In Euclidean geometry , a &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; is a straight  curve .  When geometry is used to model  the real world, lines are used to represent straight &lt;b&gt;...&lt;/b&gt; \" size=\"8441\" wordcount=\"1089\" timestamp=\"2009-12-10T17:21:35Z\" /><p ns=\"0\" title=\"Branch line\" snippet=\"A branch &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; is a secondary railway  &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; which branches off a more important through route, usually a main &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; .  short branch &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; may be &lt;b&gt;...&lt;/b&gt; \" size=\"3510\" wordcount=\"477\" timestamp=\"2009-10-31T14:08:59Z\" /><p ns=\"0\" title=\"Poetry\" snippet=\"As an example of how a &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; of meter is defined, in English-language iambic pentameter , each &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; has five metrical feet, and each foot is &lt;b&gt;...&lt;/b&gt; \" size=\"88572\" wordcount=\"12272\" timestamp=\"2009-12-22T00:46:36Z\" /><p ns=\"0\" title=\"Rail transport\" snippet=\"In addition to the previously existing east-west transcontinental &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; in Australia, a &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; from north to south has been constructed.  &lt;b&gt;...&lt;/b&gt; \" size=\"39219\" wordcount=\"5421\" timestamp=\"2009-12-22T13:57:22Z\" /><p ns=\"0\" title=\"East Coast Main Line\" snippet=\"The East Coast Main &lt;span class=&#039;searchmatch&#039;&gt;Line&lt;/span&gt; (ECML) is a 393-mile (632 km) long electrified high-speed railway link between London , Peterborough , Doncaster  &lt;b&gt;...&lt;/b&gt; \" size=\"27560\" wordcount=\"3498\" timestamp=\"2009-12-28T20:27:38Z\" /><p ns=\"0\" title=\"American football positions\" snippet=\"A wide receiver may &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; up on the &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; of scrimmage and be counted as one of the necessary seven players on the &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; in a legal formation &lt;b&gt;...&lt;/b&gt; \" size=\"23175\" wordcount=\"3586\" timestamp=\"2009-12-28T23:09:57Z\" /><p ns=\"0\" title=\"Spectral line\" snippet=\"A spectral &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; is a dark or bright &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; in an otherwise uniform and continuous spectrum , resulting from an excess or deficiency of  &lt;b&gt;...&lt;/b&gt; \" size=\"12471\" wordcount=\"1752\" timestamp=\"2009-12-06T02:11:50Z\" /><p ns=\"0\" title=\"Telephone\" snippet=\"The dial switch  in the base interrupted the &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; current by repeatedly but very briefly disconnecting the &lt;span class=&#039;searchmatch&#039;&gt;line&lt;/span&gt; 1-10 times for each digit &lt;b&gt;...&lt;/b&gt; \" size=\"30070\" wordcount=\"4028\" timestamp=\"2009-12-25T03:53:09Z\" /></search></query><query-continue><search sroffset=\"10\" /></query-continue></api>";
        WikipediaResponse response = RESULT_UNMARSHALLER.unmarshalString(content, Charset.forName("UTF-8"));

        //remove span markup
        List<ResultItem> items = response.getQueryResult().getItems();
        for (ResultItem item : items)
        {
            item.setSnippet(item.getSnippet().replace("<span class='searchmatch'>",""));
            item.setSnippet(item.getSnippet().replace("</span>",""));
        }
        return response;
    }

    public static void main(String[] args)
    {
        String s = encodeAsURL("kjdsadj askdj asd jasd ");
        System.out.println(s);
        WikipediaResponse response = new WikipediaLookup().lookup("web service");
        System.out.println(response.getQueryResult().getItems().get(0).getSnippet());
    }

    private static String encodeAsURL(String str)
    {
        String s = null;
        try
        {
            s = URLEncoder.encode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        return s;
    }
}