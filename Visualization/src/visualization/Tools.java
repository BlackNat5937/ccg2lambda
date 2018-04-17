package visualization;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tools {
    /**
     * Base name for the windows.
     */
    public static String windowTitleBase = "ccg2lambda Visualize";

    /**
     * Name of the tag containing the semantic definitions.
     */
    private static String xmlSemanticsTagName = "semantics";
    /**
     * Name of the attribute containing the id of the element which has the formulas.
     */
    private static String xmlSemanticsRootAttributeName = "root";
    /**
     * Name of the attribute containing the formulas.
     */
    private static String xmlSemElementAttributeName = "sem";

    /**
     * Gets the formulas in the xml file representing the sentences as a xml tree.
     *
     * @param semanticsXmlFile the file in which to get the formulas
     * @return a list containing all the formulas in the file
     */
    public static List<String> getSemanticsFormulas(File semanticsXmlFile) {
        List<String> formulaList = new ArrayList<>();
        try {
            DocumentBuilderFactory dBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dBuilderFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(semanticsXmlFile);
            doc.getDocumentElement().normalize();

            NodeList sentences = doc.getElementsByTagName(xmlSemanticsTagName);

            for (int i = 0; i < sentences.getLength(); i++) {
                Node sentenceSemantics = sentences.item(i);
                String formulaId = sentenceSemantics.getAttributes().getNamedItem(xmlSemanticsRootAttributeName).getTextContent();
                NodeList parts = sentenceSemantics.getChildNodes();
                String formula = "";

                int j = 0;
                Element e = null;
                Node tmp = null;
                do {
                    tmp = parts.item(j);
                    j++;
                } while (j < parts.getLength() && tmp.getNodeType() != Node.ELEMENT_NODE);
                e = (Element) tmp;
                if (e != null) {
                    formula = e.getAttribute(xmlSemElementAttributeName);
                }

                formula = simplifyFormula(formula);
                formulaList.add(formula);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return formulaList;
    }

    /**
     * Simplifies the formula. Replaces some chars and deletes tautologies.
     *
     * @param formula the formula to simplify
     * @return the simplified formula
     */
    private static String simplifyFormula(String formula) {
        String simpFormula = formula.replace("&amp;", "&");
        return simpFormula;
    }
}
