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

        //Main XML Source
        String xmlSourceAddress = args[0];
        Document dom = xp.getDom(xmlSourceAddress);
        MovieParser mp = new MovieParser();
        mp.parseDom(dom);

        //CastXMLSource
//        String castXmlSourceAddress = args[1];
//        Document dom2 = xp.getDom(castXmlSourceAddress);
//        CastParser cp = new CastParser();
//        cp.parseDom(dom2);

        //MovieCastXmlSource(Pass in as programs argument)
        //https://www.jetbrains.com/help/idea/run-debug-configuration-application.html
//        String movieCastXmlSouceAddress = args[2];
//        Document dom3 = xp.getDom(movieCastXmlSouceAddress);
//        MovieCastParser mcp = new MovieCastParser();
//        mcp.parseDom(dom3);

        System.out.println(System.currentTimeMillis() - start);
        //Naive took: 186401 + 1204533 = 1390 Seconds
        //INDEX TOOK 561719 = 561 Seconds
    }

}
