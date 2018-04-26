package visualization.utils.formula;

import java.util.Scanner;

public class EventParser extends BaseParser {
    private static void registerVariable(String token) {
        if (!token.isEmpty()) {
            token = token.trim();
            String[] parts = token.split(" ");
            //if the token is indeed a variable declaration,
            if (parts[0].equals("exists") && parts.length == 2) {
                String[] declaration = parts[1].split("\\.");
                //then it is either an event,
                if (declaration[0].matches("e\\d*")) {
                }
                //or a standard variable.
                else {
                }
            }
        }
    }

    @Override
    public Formula parse(String lambda, String sentence) {
        parseResult = new Formula(ClassicParser.simplifyLambda(lambda), sentence);

        eventNumber = 0;
        conjunctionNumber = 0;

        String token;
        String varId;
        String varName;

        Scanner sc = new Scanner(parseResult.getLambda());
        sc.useDelimiter("&");
        do {
            token = sc.next();
            // if the token is a declaration of a variable,
            if (token.matches(varDeclaration)) {
                registerVariable(token);
            }
        } while (sc.hasNext());
        return null;
    }
}
