package visualization.utils.formula;

import visualization.utils.formula.node.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * ¨Parser for classic-template lambdas.
 *
 * @author Gaétan Basile
 */
public class ClassicParser extends BaseParser {
    ClassicParser() {
    }

    /**
     * Parses the lambda and creates the actors and the events.
     */
    @Override
    public Formula parse(String lambda, String sentence) {
        parseResult = new Formula(FormulaParser.simplifyLambda(lambda), sentence);

        eventNumber = 0;
        conjunctionNumber = 0;

        String token;
        String varId = null;
        String varName = null;

        String tmp = parseResult.getLambda();
        String disjunctionScope = "";
        boolean disjunctionTreated = false;
        if (tmp.contains("|")) {
            //the disjunction scope is saved for later, and isn't processed by the scanner
            disjunctionScope = getDisjunctionScope(tmp.indexOf("|"), tmp);

            //if the subject is in the disjonction it is necessary to process it first
            if (tmp.contains("exists x." + disjunctionScope)) {
                String[] subj = disjunctionScope.split("\\|");
                String subj1 = subj[0].substring(subj[0].indexOf('_') + 1).substring(0, subj[0].substring(subj[0].indexOf('_') + 1).indexOf('('));
                String subj2 = subj[1].substring(subj[1].indexOf('_') + 1).substring(0, subj[1].substring(subj[1].indexOf('_') + 1).indexOf('('));

                parseResult.getActors().put("x", new Actor("x", subj1 + "|" + subj2));
                disjunctionTreated = true;
                tmp = tmp.replace("exists x.", "");
            }
            tmp = tmp.replace(disjunctionScope, "");
        }

        Scanner sc = new Scanner(tmp);
        sc.useDelimiter("&");
        do {
            token = sc.next();
            // if the token is a &, do not do anything
            if (!"&".equals(token)) {
                // if the token is an actor declaration, add it to the actors map
                if (token.matches(varDeclaration)) {
                    addVar(token, varId, varName);
                }
                // if the token is an event, add it to the events map
                else if (token.matches(".*Prog\\(.*")) {
                    addEvent(token, varId, varName);
                }
                // if it is not anything else, then it is a conjunction, we need to add it to the conjunctions map
                else {
                    addConj(token, varId, varName);
                }
            }
        } while (sc.hasNext());

        //check if there is any disjunction in the sentence
        if (disjunctionScope.contains("|") && !disjunctionTreated) {
            String[] args = disjunctionScope.split("\\|");
            String[] args1 = args[0].split("&");
            String[] args2 = args[1].split("&");

            for (String subScope : disjunctionScope.split("\\|")) {
                for (String s : subScope.split("&")) {
                    if (s.matches(varDeclaration)) {
                        addVar(s, varId, varName);
                    } else if (s.matches(".*Prog\\(.*")) {
                        addEvent(s, varId, varName);
                    } else {
                        addConj(s, varId, varName);
                    }
                }
            }

            //case PROG || Prog
            if (containsProgArg(args1) && containsProgArg(args2) && !containsConjArg(args1) && !containsConjArg(args2)) {
                Disjunction disjunction = new Disjunction();
                String prog1 = getProgArg(args1);
                String prog2 = getProgArg(args2);

                String name1 = prog1.substring(prog1.indexOf('_') + 1).substring(0, prog1.substring(prog1.indexOf('_') + 1).indexOf('('));
                String name2 = prog2.substring(prog2.indexOf('_') + 1).substring(0, prog2.substring(prog2.indexOf('_') + 1).indexOf('('));

                disjunction.setArg1(parseResult.getEvents().get(getEventConjKey(parseResult.getLambda().indexOf(name1), name1)));
                disjunction.setArg2(parseResult.getEvents().get(getEventConjKey(parseResult.getLambda().indexOf(name2), name2)));

                String nameOrigin = prog1.substring(prog1.indexOf('_') + 1).substring(prog1.substring(prog1.indexOf('_') + 1).indexOf('(') + 1, prog1.substring(prog1.indexOf('_') + 1).indexOf(')'));

                disjunction.setOrigin(parseResult.getActors().get(nameOrigin));

                parseResult.getDisjunctions().add(disjunction);
            }

            //case Conj || Conj
            else if (containsConjArg(args1) && containsConjArg(args2)) {
                Disjunction disjunction = new Disjunction();
                String conj1 = getConjArgs(args1);
                String conj2 = getConjArgs(args2);

                String name1 = conj1.substring(conj1.indexOf('_') + 1, conj1.indexOf('('));
                String name2 = conj2.substring(conj2.indexOf('_') + 1, conj2.indexOf('('));

                disjunction.setArg1(parseResult.getConjunctions().get(getEventConjKey(parseResult.getLambda().indexOf(conj1), name1)));
                disjunction.setArg2(parseResult.getConjunctions().get(getEventConjKey(parseResult.getLambda().indexOf(conj2), name2)));

                String nameOrigin = conj1.substring(conj1.indexOf('(') + 1, conj1.indexOf(','));

                disjunction.setOrigin(parseResult.getActors().get(nameOrigin));

                parseResult.getDisjunctions().add(disjunction);
            }


        }

        //check if there is any negation in the sentence
        if (parseResult.getLambda().contains("-")) {
            int indexStart = parseResult.getLambda().indexOf('-');
            int indexEnd = indexStart;
            int cptBracket = 0;
            boolean firstTime = true;

            while ((firstTime || cptBracket > 0) && indexEnd < parseResult.getLambda().length()) {
                if (parseResult.getLambda().charAt(indexEnd) == '(') {
                    if (firstTime) {
                        firstTime = false;
                    }
                    cptBracket++;
                }
                if (parseResult.getLambda().charAt(indexEnd) == ')') {
                    cptBracket--;
                }
                indexEnd++;
            }

            String scope = parseResult.getLambda().substring(indexStart, indexEnd);
            String subj = parseResult.getLambda().trim().substring(parseResult.getLambda().trim().indexOf(" "), parseResult.getLambda().trim().indexOf(".")).trim();
            Negation n = getNegationFromScope(scope, subj);
            parseResult.getNegations().add(n);

        }
        if (parseResult.getLambda().contains("not")) {
            Negation n = new Negation();
            String substring = parseResult.getLambda().substring(parseResult.getLambda().indexOf("_not"));
            String ssubstring = substring.substring(substring.indexOf("(_") + 2, substring.indexOf("))"));
            n.getNegated().add(parseResult.getActors().get(parseResult.getActorByName(ssubstring)));
            parseResult.getNegations().add(n);
        }


        return parseResult;
    }

    /**
     * Parse the scope of the negation
     *
     * @param scope oooo
     * @return the node Negation containing all nodes that are negated
     */
    public Negation getNegationFromScope(String scope, String subj) {
        Negation n = new Negation();

        // two cases : exists or directly the event being negated

        if (scope.matches(varDeclaration)) {
            //Case exists
            String subScope = scope.substring(scope.indexOf("_"), scope.indexOf("))")) + ")";
            String[] subStringScope = subScope.split("&");
            for (String s : subStringScope) {
                s = s.trim();
                boolean isEvent = false;

                //exceptionnal cases : Prog(_walk(x), exists z2.(_park(z2), etc...
                if (s.matches(varDeclaration)) {
                    s = s.substring(s.indexOf('_'));
                } else if (s.matches(".*Prog\\(.*")) {
                    isEvent = true;
                    s = s.substring(s.indexOf('_'));
                }

                int iStart = s.indexOf('(') + 1;
                int iEnd = s.indexOf(')');
                String ssubstring = s.substring(iStart, iEnd);
                if (isEvent) {
                    String eventName = s.substring(s.indexOf('_') + 1, s.indexOf('('));
                    int indexEvent = parseResult.getLambda().indexOf(s);
                    String eventKey = getEventConjKey(indexEvent, eventName);
                    n.getNegated().add(parseResult.getEvents().get(eventKey));
                } else {
                    if (s.contains(",")) {
                        //conjunction
                        String conjName = s.substring(s.indexOf('_') + 1, s.indexOf('('));
                        String conjParams = ssubstring;
                        int indexConj = parseResult.getLambda().indexOf("_" + conjName + "(" + conjParams + ")");
                        String conjKey = getEventConjKey(indexConj, conjName);
                        n.getNegated().add(parseResult.getConjunctions().get(conjKey));
                    } else {
                        if (!ssubstring.equals(subj)) {
                            //add to negation
                            //check actors
                            if (parseResult.getActors().containsKey(ssubstring)) {
                                n.getNegated().add(parseResult.getActors().get(ssubstring));
                            }
                        } else {
                            String key = getEventConjKey(parseResult.getLambda().indexOf(s), s.substring(s.indexOf('_') + 1, s.indexOf('(')));
                            n.getNegated().add(parseResult.getConjunctions().get(key));
                        }
                    }
                }
            }
        } else if (scope.contains("&")) {
            String[] subScope = scope.split("&");
            for (String s : subScope) {
                s = s.trim();
                String name = s.substring(s.indexOf('_') + 1);
                name = name.substring(0, name.indexOf('('));
                int index = parseResult.getLambda().indexOf(s);
                if (s.matches(".*Prog\\(.*")) {
                    //event
                    String key = getEventConjKey(index, name);
                    n.getNegated().add(parseResult.getEvents().get(key));
                } else {
                    //conj
                    String key = getEventConjKey(index, name);
                    n.getNegated().add(parseResult.getConjunctions().get(key));
                }

            }
        } else {
            //HERE
            String conjName = scope.substring(scope.indexOf('_') + 1);
            conjName = conjName.substring(0, conjName.indexOf('('));
            int indexConj = parseResult.getLambda().indexOf(scope);
            String conjKey = getEventConjKey(indexConj, conjName);
            n.getNegated().add(parseResult.getConjunctions().get(conjKey));
        }
        return n;
    }
    
    public String getEventConjKey(int indexEvent, String event) {
        String searchedString = parseResult.getLambda().substring(0, indexEvent);
        int lastIndex = 0;
        int count = 1;

        while (lastIndex != -1) {
            lastIndex = searchedString.indexOf(event, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += event.length();
            }
        }
        return "e" + event + count;
    }

    public String getDisjunctionScope(int index, String sentence) {
        boolean firstTime = true;
        int countLeft = 1;
        int countRight = 1;
        int indexLeft = index;
        int indexRight = index;

        while ((firstTime || countLeft != 0) && indexLeft > 0) {
            if (sentence.charAt(indexLeft) == ')') {
                countLeft++;
                firstTime = false;
            } else if (sentence.charAt(indexLeft) == '(') {
                countLeft--;
            }
            indexLeft--;
        }

        firstTime = true;

        while ((firstTime || countRight != 0) && indexRight > 0) {
            if (sentence.charAt(indexRight) == '(') {
                countRight++;
                firstTime = false;
            } else if (sentence.charAt(indexRight) == ')') {
                countRight--;
            }
            indexRight++;
        }

        return sentence.substring(indexLeft, indexRight);
    }


    public boolean containsProgArg(String[] args) {
        boolean res = false;
        for (String s : args) {
            if (s.matches(".*Prog\\(.*")) {
                res = true;
            }
        }
        return res;
    }

    public boolean containsConjArg(String[] args) {
        boolean res = false;
        for (String s : args) {
            if (s.contains(",")) {
                res = true;
            }
        }
        return res;
    }

    public String getProgArg(String[] args) {
        String res = "";
        for (String s : args) {
            if (s.matches(".*Prog\\(.*")) {
                res = s;
            }
        }
        return res;
    }

    public String getConjArgs(String[] args) {
        String res = "";
        for (String s : args) {
            if (s.contains(",")) {
                res = s;
            }
        }
        return res;
    }

    public void addVar(String token, String varId, String varName) {
        Matcher m = varIdPattern.matcher(token);
        Matcher n = varNamePattern.matcher(token);
        if (m.find() && n.find()) {
            varId = m.group();
            varId = varId.substring(0, varId.length() - 1);
            varName = n.group().substring(1);

            parseResult.getActors().put(varId, new Actor(varId, varName));
        }
    }

    public void addEvent(String s, String varName, String varId) {
        Matcher m = varNamePattern.matcher(s);
        Matcher n = varIdPattern.matcher(s);
        String actorId;

        if (m.find() && n.find()) {
            varName = m.group().substring(1);
            //varId = "e" + eventNumber;
            varId = getEventConjKey(parseResult.getLambda().indexOf('_' + varName + '(' + n.group().substring(1)), varName);
            actorId = n.group();
            actorId = actorId.substring(1, actorId.length() - 1);
            // if the actorId starts with "_" then it is a proper noun and we have to search for it in the map
            if (actorId.startsWith("_")) {
                actorId = parseResult.getActorByName(actorId.substring(1));
            }
            eventNumber++;
            parseResult.getEvents().put(varId, new Event(varId, varName, parseResult.getActors().get(actorId)));
        }
    }

    public void addConj(String s, String varId, String varName) {
        String joinedId;
        Matcher m = varNamePattern.matcher(s);
        Matcher n = varIdPattern.matcher(s);

        if (m.find() && n.find()) {
            varName = m.group().substring(1);
            varId = getEventConjKey(parseResult.getLambda().indexOf('_' + varName + '(' + n.group().substring(1)), varName);
            conjunctionNumber++;

            joinedId = n.group();
            joinedId = joinedId.substring(1, joinedId.length() - 1);
            String[] ids = joinedId.split(",");
            List<Actor> joined = new ArrayList<>();
            for (String id : ids) {
                if (!(id.startsWith("e") || id.startsWith("c"))) {
                    // if the id stats with "_" then it is a proper noun and we have to search for it in the map
                    if (id.startsWith("_")) {
                        id = parseResult.getActorByName(id.substring(1));
                    }
                    // else we just add it to the list of actors joined
                    joined.add(parseResult.getActors().get(id));
                }
            }
            parseResult.getConjunctions().put(varId, new Conjunction(varId, varName, joined.toArray(new BaseNode[0])));
        }
    }
}
