import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class XMLParser {
    public Document getDom(String address) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(address);
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        long start = System.currentTimeMillis();
        XMLParser xp = new XMLParser();

        String xmlSourceAddress = args[0];
        Document dom = xp.getDom(xmlSourceAddress);
        MovieParser mp = new MovieParser();
        mp.parseDom(dom);

        String castXmlSourceAddress = args[1];
        Document dom2 = xp.getDom(castXmlSourceAddress);
        CastParser cp = new CastParser();
        cp.parseDom(dom2);

        String movieCastXmlSouceAddress = args[2];
        Document dom3 = xp.getDom(movieCastXmlSouceAddress);
        MovieCastParser mcp = new MovieCastParser();
        mcp.parseDom(dom3);

        System.out.println(System.currentTimeMillis() - start);
        //Naive took(Just Insert): 1390934 = 1390 Seconds
        //INDEX On Genre(Name) and Stars(Name) (SET UP BEFORE Running parser): TOOK 561719 = 561 Seconds
        //Index On Genre(Name) and Stars(Name) (Place after insertion of the respective table) + Batch Processing when possible: 448386 = 448 Second
    }

}
