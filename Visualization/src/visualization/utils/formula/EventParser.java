package visualization.utils.formula;

import visualization.utils.formula.node.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for event-template lambdas.
 *
 * @author Gaétan Basile
 */
public class EventParser extends BaseParser {
    /**
     * Pattern for the declaration of an events subject
     */
    private static final String eventSubjectDeclaration = "\\(Subj\\(\\w+\\) = \\w+\\).*";
    /**
     * List of already used variable identifiers
     */
    private Map<String, Integer> usedIdentifiers;

    EventParser() {
    }

    @Override
    public Formula parse(String lambda, String sentence) {
        parseResult = new Formula(FormulaParser.simplifyLambda(lambda.trim()), sentence);

        Map<String, BaseNode> nodes = new HashMap<>();
        parse(parseResult.getLambda(), nodes);

        actorNumber = 0;
        eventNumber = 0;
        conjunctionNumber = 0;

        usedIdentifiers = new HashMap<>();
        renameDuplicateIdentifiers(parseResult.getLambda());
        //recursiveRenameDuplicateIdentifiers(parseResult.getLambda());

        String token;
        String varId;
        String varName;

        Scanner sc = new Scanner(parseResult.getLambda());
        sc.useDelimiter("&");
        do {
            token = sc.next();
            token = token.trim();
            if (!token.isEmpty()) {
                // if the token is a declaration of a variable,
                if (token.matches(varDeclaration)) {
                    registerVariable(token);
                }
                // or if it is a declaration of the subject of an event variable,
                else if (token.matches(eventSubjectDeclaration)) {
                    registerEventSubject(token);
                }
                // or if it is an equality declaration,
                else if (token.matches("\\(+\\w+ = \\w+\\)+")) {
                    registerSynonym(token);
                }
                // or if it anything else (a conjunction),
                else {
                    registerConjunction(token);
                }
            }
        } while (sc.hasNext());
        return parseResult;
    }

    public Formula parse(String lambda, Map<String, BaseNode> nodes) {
        List<String> scopes = getScopes(lambda);
        scopes.forEach(scope -> {
            int scopeStartIndex = scope.indexOf('('), scopeEndIndex = getClosingBracketIndex(scope);
            Scanner sc = new Scanner(scope);
            sc.useDelimiter("&");
            String token;
            boolean firstExists = true;

            do {
                String varName;
                String varId;
                token = sc.next().trim();
                if (token.contains("exists")) {
                    if (token.matches(varDeclaration)) {
                        varId = token.split("\\.")[0].split(" ")[1].trim();
                        varName = token.split("\\.")[1].split("\\)")[0].substring(1).split("\\(")[0].trim();
                        if (firstExists) {
                            firstExists = false;
                            BaseNode newNode;
                            if (varId.startsWith("e")) {
                                newNode = new Event(varId, varName);
                                parseResult.getEventEvents().add((Event) newNode);
                            } else {
                                newNode = new Actor(varId, varName);
                                parseResult.getEventActors().add((Actor) newNode);
                            }
                            nodes.put(varId, newNode);
                            System.out.println(varId + " : " + varName);
                        } else if (nodes.get(varId) == null) {
                            parse(scope.substring(scopeStartIndex, scopeEndIndex), nodes);
                        }
                    }
                } else if (token.matches(eventSubjectDeclaration)) {
                    registerEventSubject(nodes, nodes, token);
                } else {

                }
            } while (sc.hasNext());
        });
        return parseResult;
    }

    private void registerEventSubject(Map<String, BaseNode> actors, Map<String, BaseNode> events, String token) {
        String varId;
        String[] parts = token.split("\\)");
        if (parts[0].contains("Subj") && parts.length >= 2) {
            varId = parts[0].substring(1);
            varId = varId.substring(varId.indexOf("(") + 1);

            String subjId = parts[1].substring(parts[1].length() - 1);

            Actor subject = (Actor) actors.get(subjId);
            Event event = (Event) events.get(varId);

            event.setSubject(subject);
            event.getActors().add(subject);
        }
    }

    private void registerSynonym(String token) {
        token = token.replace("(", "").replace(")", "");
        String[] parts = token.split("=");

        String firstS = parts[0].trim(), secondS = parts[1].trim();
        BaseNode firstN, secondN;

        firstN = parseResult.getActors().get(firstS);
        if (firstN == null)
            firstN = parseResult.getEvents().get(firstS);
        if (firstN == null)
            firstN = parseResult.getConjunctions().get(firstS);

        secondN = parseResult.getActors().get(secondS);
        if (secondN == null)
            secondN = parseResult.getEvents().get(secondS);
        if (secondN == null)
            secondN = parseResult.getConjunctions().get(secondS);

        firstN.getEqualities().add(secondN);
        secondN.getEqualities().add(firstN);
    }

    private void registerVariable(String token) {
        String[] parts = token.split(" ");
        //if the token is indeed a variable declaration,
        if (parts[0].equals("exists") && parts.length >= 2) {
            String[] declaration = parts[1].split("\\.");

            String varId = declaration[0];
            if (declaration[1].startsWith("(")) {
                declaration[1] = declaration[1].substring(1);
            }
            String varName = declaration[1].substring(
                    declaration[1].indexOf("_") + 1,
                    declaration[1].indexOf('('));
            //then it is either an event,
            if (declaration[0].matches("e\\d*")) {
                parseResult.getEvents().put(varId, new Event(varId, varName));
            }
            //or a standard variable.
            else {
                parseResult.getActors().put(varId, new Actor(varId, varName));
            }
        }
    }

    private void registerEventSubject(String token) {
        String[] parts = token.split("\\)");
        if (parts[0].contains("Subj") && parts.length >= 2) {
            String varId;
            varId = parts[0].substring(1);
            varId = varId.substring(varId.indexOf("(") + 1);

            String subjId = parts[1].substring(parts[1].length() - 1);

            Actor subject = parseResult.getActors().get(subjId);

            Event event = parseResult.getEvents().get(varId);
            event.setSubject(subject);
            event.getActors().add(subject);
        }
    }

    private void registerConjunction(String token) {
        String[] parts = token.split("\\(");
        if (parts[0].startsWith("_") && parts.length >= 2) {
            String varId = "c" + conjunctionNumber;
            String varName = parts[0].substring(1);
            conjunctionNumber++;

            String[] joined = parts[1].split("\\)");
            joined = joined[0].split(",");

            List<FormulaNode> joinedNodes = new ArrayList<>();
            for (String s : joined) {
                FormulaNode node;
                //its an event
                if (s.startsWith("e")) node = parseResult.getEvents().get(s);
                    //its a conjunction
                else if (s.startsWith("c")) node = parseResult.getConjunctions().get(s);
                    //its an actor
                else node = parseResult.getActors().get(s);
                if (node != null)
                    joinedNodes.add(node);
            }
            parseResult.getConjunctions().put(varId,
                    new Conjunction(varId, varName, joinedNodes.toArray(new FormulaNode[0])));
        }
    }

    private String renameDuplicateIdentifiers(String lambda) {
        lambda = lambda.trim();

        String exists = "exists";
        String original = lambda;
        int nextExistsIndex;
        nextExistsIndex = original.indexOf(exists);
        if (nextExistsIndex != -1) {
            original = original.substring(nextExistsIndex);
            String declaration = original.split("\\.")[0].trim();
            String identifier = declaration.split(" ")[1].trim();
            String scope = getDeclarationScope(0, original);
            usedIdentifiers.put(identifier, usedIdentifiers.getOrDefault(identifier, 0) + 1);
            String newScope = renameDuplicateIdentifiers(scope);
            original = original.replace(scope, newScope);
            if (usedIdentifiers.getOrDefault(identifier, 0) > 1) {
                String pattern = varIdPattern.pattern().replace("\\w", identifier);
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(original);
                String replacement = original;
                String newIdentifier = getUnusedIdentifier(identifier);
                while (m.find()) {
                    String found = m.group();
                    String replaced = found.replace(identifier, newIdentifier);
                    replacement = replacement.replace(found, replaced);
                }
                lambda = lambda.replace(original, replacement);
            }
        }
        return lambda;
    }

    private String getUnusedIdentifier(String identifier) {
        String res = "";
        int id = 0;
        // if the variable is an event
        if (identifier.startsWith("e")) {
            res = "e" + id;
        } else {
            res += identifier.charAt(0);
            res += id;
        }
        do {
            id++;
            // if the variable is an event
            if (identifier.startsWith("e")) {
                res = "e" + id;
            } else {
                char first = res.charAt(0);
                res = "";
                res += first;
                res += id;
            }
        } while (usedIdentifiers.get(res) != null);
        usedIdentifiers.put(res, 1);
        return res;
    }

    private List<String> getScopes(String lambda) {
        List<String> scopes = new ArrayList<>();
        String subLambda = lambda;
        int nextExistsIndex;
        do {
            nextExistsIndex = subLambda.indexOf("exists");
            if (nextExistsIndex != -1) {
                subLambda = subLambda.substring(nextExistsIndex);
                int endOfScopeIndex = getClosingBracketIndex(subLambda);
                String scope = subLambda.substring(subLambda.indexOf("exists"), endOfScopeIndex + 1);
                scopes.add(scope);
                subLambda = subLambda.substring(endOfScopeIndex + 1);
            }
        } while (nextExistsIndex != -1);
        return scopes;
    }

    /**
     * Returns the scope (the text in brackets) of a variable declaration.
     *
     * @param startIndex the index of the "exists" keyword for this declaration
     * @param lambda     the lambda to get the scope from
     * @return the inner text of the declaration, which may contain other declarations; null if lambda is incorrect
     */
    private String getDeclarationScope(int startIndex, String lambda) {
        if (startIndex == -1) return null;
        int brackets = 0;
        lambda = lambda.substring(startIndex);
        int afterFirstBracketIndex = lambda.indexOf('(') + 1;
        if (afterFirstBracketIndex == -1) return null;
        char[] charArray = lambda.toCharArray();
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char c = charArray[i];
            if (c == '(')
                brackets++;
            else if (c == ')')
                brackets--;
            else continue;
            if (brackets == 0)
                return lambda.substring(afterFirstBracketIndex, i).trim();
        }
        return null;
    }

    /**
     * Get the index of the closing bracket corresponding to the first opening bracket in the str.
     *
     * @param str the string to test
     * @return the index of the closing bracket, or -1 if the string does not contain 2k brackets
     */
    private int getClosingBracketIndex(String str) {
        char[] chars = str.toCharArray();
        int bracketsStartIndex = 0, length = chars.length;
        while (bracketsStartIndex < length) {
            char c = chars[bracketsStartIndex];
            if (c == '(')
                break;
            bracketsStartIndex++;
        }
        if (bracketsStartIndex > length)
            return -1;

        Deque<Character> brackets = new ArrayDeque<>();
        for (int index = bracketsStartIndex; index < length; index++) {
            char c = chars[index];
            if (c == '(')
                brackets.push(c);
            else if (c == ')')
                brackets.pop();
            else continue;
            if (brackets.isEmpty())
                return index;
        }
        return -1;
    }

    private int getEndOfScopeIndex(int firstBracketIndex, String lambda) {
        int brackets = 0;
        lambda = lambda.substring(firstBracketIndex);
        char[] charArray = lambda.toCharArray();
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char c = charArray[i];
            if (c == '(')
                brackets++;
            else if (c == ')')
                brackets--;
            else continue;
            if (brackets == 0)
                return i + firstBracketIndex;
        }
        return -1;
    }
}
