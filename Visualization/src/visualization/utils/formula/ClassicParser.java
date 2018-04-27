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
        parseResult = new Formula(BaseParser.simplifyLambda(lambda), sentence);

        eventNumber = 0;
        conjunctionNumber = 0;

        String token;
        String varId;
        String varName;

        Scanner sc = new Scanner(parseResult.getLambda());
        sc.useDelimiter("&");
        do {
            token = sc.next();
            // if the token is a &, do not do anything
            if (!"&".equals(token)) {
                // if the token is an actor declaration, add it to the actors map
                if (token.matches(varDeclaration)) {
                    Matcher m = varIdPattern.matcher(token);
                    Matcher n = varNamePattern.matcher(token);
                    if (m.find() && n.find()) {
                        varId = m.group();
                        varId = varId.substring(0, varId.length() - 1);
                        varName = n.group().substring(1);

                        parseResult.getActors().put(varId, new Actor(varId, varName));
                    }
                }
                // if the token is an event, add it to the events map
                else if (token.matches(".*Prog\\(.*")) {
                    Matcher m = varNamePattern.matcher(token);
                    Matcher n = varIdPattern.matcher(token);
                    String actorId;

                    if (m.find() && n.find()) {
                        varName = m.group().substring(1);
                        //varId = "e" + eventNumber;
                        varId = getEventKey(parseResult.getLambda().indexOf('_' + varName + '(' + n.group().substring(1)),varName);
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
                // if it is not anything else, then it is a conjunction, we need to add it to the conjunctions map
                else {
                    String joinedId;
                    Matcher m = varNamePattern.matcher(token);
                    Matcher n = varIdPattern.matcher(token);

                    if (m.find() && n.find()) {
                        varName = m.group().substring(1);
                        varId = getConjunctionKey(parseResult.getLambda().indexOf('_' + varName + '(' + n.group().substring(1)),varName);
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
        }while (sc.hasNext());

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

        return parseResult;
    }

    /**
     * Parse the scope of the negation
     * @param scope
     * @return the node Negation containing all nodes that are negated
     */
    public Negation getNegationFromScope(String scope, String subj){
        Negation n = new Negation();

        // two cases : exists or directly the event being negated

        if(scope.matches(varDeclaration)){
            //Case exists
            String subScope =  scope.substring(scope.indexOf("_"), scope.indexOf("))")) + ")";
            String[] subStringScope = subScope.split("&");
            for(String s : subStringScope){
                s = s.trim();
                boolean isEvent = false;

                //exceptionnal cases : Prog(_walk(x), exists z2.(_park(z2), etc...
                if(s.matches(varDeclaration)){
                    s = s.substring(s.indexOf('_'));
                }
                else if(s.matches(".*Prog\\(.*")){
                    isEvent = true;
                    s = s.substring(s.indexOf('_'));
                }

                int iStart = s.indexOf('(') + 1;
                int iEnd = s.indexOf(')');
                String ssubstring = s.substring(iStart, iEnd);
                if(isEvent){
                    String eventName = s.substring(s.indexOf('_') + 1, s.indexOf('('));
                    int indexEvent = parseResult.getLambda().indexOf(s);
                    String eventKey = getEventKey(indexEvent,eventName);
                    n.getNegated().add(parseResult.getEvents().get(eventKey));
                }else{
                    if(s.contains(",")){
                        //conjunction
                        String conjName =  s.substring(s.indexOf('_')+1,s.indexOf('('));
                        String conjParams = ssubstring;
                        int indexConj = parseResult.getLambda().indexOf("_" + conjName + "(" + conjParams + ")");
                        String conjKey = getConjunctionKey(indexConj, conjName);
                        n.getNegated().add(parseResult.getConjunctions().get(conjKey));
                    }
                    else{
                        if(!ssubstring.equals(subj)){
                            //add to negation
                            //check actors
                            if(parseResult.getActors().containsKey(ssubstring)){
                                n.getNegated().add(parseResult.getActors().get(ssubstring));
                            }
                        }
                        else{
                            String key = getConjunctionKey(parseResult.getLambda().indexOf(s), s.substring(s.indexOf('_') + 1, s.indexOf('(')));
                            n.getNegated().add(parseResult.getConjunctions().get(key));
                        }
                    }
                }
            }
        }
        else{
            //HERE
            System.out.println("Other case : " + scope);
            String conjName = scope.substring(scope.indexOf('_') + 1, scope.indexOf('('));
            int indexConj = parseResult.getLambda().indexOf(scope);
            String conjKey = getConjunctionKey(indexConj,conjName);
            n.getNegated().add(parseResult.getConjunctions().get(conjKey));
        }
        return n;
    }

    public String getConjunctionKey(int indexConjunction, String conjunction){
        //example :
        String searchedString = parseResult.getLambda().substring(0,indexConjunction);
        int lastIndex = 0;
        int count  = 1;

        while(lastIndex != -1){
            lastIndex = searchedString.indexOf(conjunction, lastIndex);

            if(lastIndex != -1){
                count ++;
                lastIndex += conjunction.length();
            }
        }

        return "c" + conjunction + count;
    }

    public String getEventKey(int indexEvent, String event){
        String searchedString = parseResult.getLambda().substring(0,indexEvent);
        int lastIndex = 0;
        int count  = 1;

        while(lastIndex != -1){
            lastIndex = searchedString.indexOf(event, lastIndex);

            if(lastIndex != -1){
                count ++;
                lastIndex += event.length();
            }
        }

        return "e" + event + count;
    }
}
