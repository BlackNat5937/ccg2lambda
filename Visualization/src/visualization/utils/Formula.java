package visualization.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a formula output by ccg2lambda.
 */
public class Formula {
    /**
     * The actors of this formula. They are the ones to initiate or to endure events.
     */
    private Map<String, String> actors = new HashMap<>();
    /**
     * The events of this formula. They represent actions and have effect on the actors.
     */
    private Map<String, String> events = new HashMap<>();

    private Formula() {

    }

    /**
     * Parses the formula and creates the actors and the events.
     */
    public static Formula parse(String formula) {
        Formula f = new Formula();

        Pattern varNamePattern = Pattern.compile("_\\w+");
        Pattern varIdPattern = Pattern.compile("(\\w+\\.)|(\\w+,\\w+)");

        String token;
        String varId;
        String varName;

        int eventNumber = 0;

        Scanner sc = new Scanner(formula);
        sc.useDelimiter("&");
        do {
            token = sc.next();
            if ("&".equals(token)) {
            } else if (token.matches(".*exists \\w+\\..*")) {
                Matcher m = varIdPattern.matcher(token);
                Matcher n = varNamePattern.matcher(token);
                if (m.find() && n.find()) {
                    varId = m.group();
                    varId = varId.substring(0, varId.length() - 1);
                    varName = n.group().substring(1);

                    f.actors.put(varId, varName);
                }
            } else if (token.matches(".*Prog\\(.*")) {
                Matcher m = varNamePattern.matcher(token);
                if (m.find()) {
                    varName = m.group();
                    varId = "e" + eventNumber;
                    eventNumber++;

                    f.events.put(varId, varName);
                }
            }
        } while (sc.hasNext());
        return f;
    }
}
