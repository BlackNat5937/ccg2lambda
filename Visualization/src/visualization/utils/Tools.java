package visualization.utils;

import com.sun.istack.internal.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import visualization.Main;
import visualization.utils.formula.Formula;
import visualization.utils.formula.FormulaParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tools {
    /**
     * Base name for the windows.
     */
    public static final String windowTitleBase = "ccg2lambda Visualize";
    public static final Number[] windowSize = {
            800, 600
    };
    public static Number[] titledSize = {
            0, 0
    };
    /**
     * Config file path.
     */
    public static final File configFile = new File("./config/conf.ini");
    /**
     * Config C&C path file
     */
    public static final File configCandC = new File("../en/candc_location.txt");
    /**
     * Config easyCCG path file
     */
    public static final File configEasyCCG = new File("../en/easyccg_location.txt");
    /**
     * Config EN parser path file
     */
    public static final File configENParserLocation = new File("../en/parser_location.txt");
    /**
     * Config JA parser path file
     */
    public static final File configJAParserLocation = new File("../ja/parser_location_ja.txt");
    /**
     * Output modes available. None means all.
     */
    public static final String[] outputModesOption = {
            "--graph",
            "--drt",
    };
    /**
     * Input templates supported. Required for file loading.
     */
    public static final String[] inputModeOption = {
            "--classic",
            "--event",
    };
    /**
     * Option to get html output on the console.
     */
    public static String htmlOutputOption = "--htmlout";

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
     * @param xmlSemanticsFile the file in which to get the formulas
     * @return a list containing all the formulas in the file
     */
    public static List<Formula> getSemanticsFormulas(File xmlSemanticsFile) {
        List<Formula> formulas = new ArrayList<>();
        FormulaParser formulaParser = Formula.getParser();
        try {
            DocumentBuilderFactory dBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dBuilderFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlSemanticsFile);
            doc.getDocumentElement().normalize();

            NodeList sentences = doc.getElementsByTagName(xmlSemanticsTagName);

            for (int i = 0; i < sentences.getLength(); i++) {
                Node sentenceSemantics = sentences.item(i);
                String formulaId = sentenceSemantics.getAttributes().getNamedItem(xmlSemanticsRootAttributeName).getTextContent();
                NodeList parts = sentenceSemantics.getChildNodes();
                String lambda = "";
                String sentence = "";

                if (Main.applicationMode == ApplicationModes.UI) {
                    File sentenceFile = new File("../sentences.txt");
                    if (sentenceFile.isFile() && sentenceFile.canRead()) {
                        BufferedReader br = new BufferedReader(new FileReader(sentenceFile));
                        int line = 0;
                        String readLine = br.readLine();
                        while (line < i && readLine != null) {
                            readLine = br.readLine();
                            line++;
                        }
                        sentence = readLine != null ? readLine : "";
                    }
                }

                int j = 0;
                Element e;
                Node tmp;
                do {
                    tmp = parts.item(j);
                    j++;
                } while (j < parts.getLength() && tmp.getNodeType() != Node.ELEMENT_NODE);
                e = (Element) tmp;
                if (e != null) {
                    lambda = e.getAttribute(xmlSemElementAttributeName);
                }

                lambda = FormulaParser.simplifyLambda(lambda);
                Formula formula = formulaParser.parse(lambda, sentence);
                formulas.add(formula);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        Main.xmlSemanticsFile = xmlSemanticsFile;
        return formulas;
    }

    /**
     * Enum for the available operation modes of the application.
     */
    public enum ApplicationModes {
        /**
         * the classic mode, with UI input and automated processing using ccg2lambda
         */
        UI,
        /**
         * the viewing mode, using a any.sem.xml file generated by ccg2lambda
         */
        VIEWER,
        /**
         * the pipeline mode, outputting the representation as a HTML document on stdout
         */
        PIPELINE
    }

    /**
     * Enum for the available representation modes of the application.
     */
    public enum RepresentationModes {
        /**
         * the basic mode, generates all available representations
         */
        ALL(""),
        /**
         * graph mode, generates only the graph representation
         */
        GRAPH(outputModesOption[0]),
        /**
         * drt mode, generates only the drt representation
         */
        DRT(outputModesOption[1]);

        /**
         * The possible values for this enum.
         */
        private static final RepresentationModes[] vals = RepresentationModes.values();
        /**
         * The option associated with a mode.
         */
        private final String option;

        /**
         * Default constructor; takes a string to associate an option with an enum value.
         *
         * @param option the option to associate the enum value with
         */
        RepresentationModes(String option) {
            this.option = option;
        }

        /**
         * Get Visualization mode associated with an option.
         *
         * @param str the option
         * @return the VisualizationMode corresponding to the option
         */
        public static RepresentationModes fromString(@NotNull String str) {
            int i = 0;
            while (i < vals.length)
                if (vals[i].option.equals(str))
                    return vals[i];
            return ALL;
        }
    }

    /**
     * Enum for the templates types.
     */
    public enum TemplateType {
        CLASSIC(inputModeOption[0]),
        EVENT(inputModeOption[1]);

        /**
         * The possible values for this enum.
         */
        private static final TemplateType[] vals = TemplateType.values();
        /**
         * The option associated with this template type.
         */
        private final String option;

        /**
         * Default constructor; takes a string to associate with TemplateType value.
         *
         * @param option the option to associate the enum values with
         */
        TemplateType(String option) {
            this.option = option;
        }

        public static TemplateType fromString(@NotNull String str) {
            return Arrays.stream(vals).filter(templateType -> templateType.option.equals(str)).findFirst().orElse(null);
        }
    }

    /**
     * Enum for the parser types.
     */
    public enum ParserType {
        CANDC,
        ALL,
        JA
    }

}
