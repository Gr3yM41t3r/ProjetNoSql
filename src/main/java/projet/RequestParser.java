package projet;

import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.Str;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

public class RequestParser {
    static final String baseURI = null;

    /**
     * Votre répertoire de travail où vont se trouver les fichiers à lire
     */
    private final String workingDir = "data/";
    /**
     * Fichier contenant des données rdf
     */
    private final String dataFile = workingDir + "sample_data.nt";
    List<Integer> reponse = new ArrayList<>();

    private final String queryFile;
    private final DictionnayParser dictionnayParser;
    private final String outputFolder;
    private final File outputFile;
    private  Jena jena;



    public RequestParser(String queryFile, DictionnayParser dictionnayParser,String of,Boolean bool) throws IOException {
        this.queryFile =  queryFile;
        this.dictionnayParser = dictionnayParser;
        this.outputFolder=of;
        outputFile = new File(outputFolder+"/self_solutions.txt");
        outputFile.createNewFile();
        if (bool){
             jena = new Jena(dictionnayParser.getDataFile(),queryFile,of);
            jena.loadModel();
        }
    }

    public void processAQuery(ParsedQuery query,String querynotrParsed) throws IOException {
        List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
        int rightApproach = this.getMissingVariable(patterns.get(0));
        int a ;
        for (int i = 0; i < patterns.size(); i++) {
            a = 0;
            switch (rightApproach) {
                case 1:
                    a = getObjectAnswers(patterns.get(i));
                    break;
                case 2:
                    a = getSubjectAnswers(patterns.get(i));
                    break;
                case 3:
                     a=getPredicateAnswer(patterns.get(i));
                    break;
            }
            if (a == -1) {
                reponse.clear();
                break;
            }
        }
        Set<Integer> listfinale = new HashSet<>(reponse);
        Set<String> reponses = new HashSet<>();
        reponse.clear();
        reponse.addAll(listfinale);
        System.out.println("---------------------------");
        for (int i = 0; i < reponse.size(); i++) {
            System.out.println(this.dictionnayParser.getDictionnaire().get(reponse.get(i)));
            reponses.add(this.dictionnayParser.getDictionnaire().get(reponse.get(i)));
            Files.writeString(outputFile.toPath(), this.dictionnayParser.getDictionnaire().get(reponse.get(i))+"\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        }
        if (jena.processAQuery(querynotrParsed).containsAll(reponses)){
            System.out.println(true);
        }
        reponse.clear();


    }


    public void parseQueries() throws IOException {
        /**
         * Try-with-resources
         *
         * @see <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
         */
        /*
         * On utilise un stream pour lire les lignes une par une, sans avoir à toutes les stocker
         * entièrement dans une collection.
         */
        try (Stream<String> lineStream = Files.lines(Paths.get(this.queryFile))) {
            SPARQLParser sparqlParser = new SPARQLParser();
            Iterator<String> lineIterator = lineStream.iterator();
            StringBuilder queryString = new StringBuilder();
            while (lineIterator.hasNext())
                /*
                 * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par un '}'
                 * On considère alors que c'est la fin d'une requête
                 */ {
                String line = lineIterator.next();
                queryString.append(line);
                if (line.trim().endsWith("}")) {
                    ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);
                    //System.err.println(query.getTupleExpr());
                    processAQuery(query,queryString.toString()); // Traitement de la requête, à adapter/réécrire pour votre programme
                    queryString.setLength(0); // Reset le buffer de la requête en chaine vide
                }
            }
        }
    }

    /***
     * cette méthode detecte automatique la variable inconnue v0 (elle peut être soit le subject, predicate ou object )
     * le but est de bien determiner le choix de la structure hexastore à utiliser
     *
     */
    public int getMissingVariable(StatementPattern statementPattern) {
        if (statementPattern.getObjectVar().getValue() == null) {
            return 1;
        } else if (statementPattern.getSubjectVar().getValue() == null) {
            return 2;
        } else if (statementPattern.getPredicateVar().getValue() == null) {
            return 3;
        }
        return 0;

    }


    public int getObjectAnswers(StatementPattern statementPattern) {
        String sbj = statementPattern.getSubjectVar().getValue().toString();
        String prd = statementPattern.getPredicateVar().getValue().toString();
        Integer subject = this.dictionnayParser.getDictionnaireInverse().get(sbj);
        Integer predicate = this.dictionnayParser.getDictionnaireInverse().get(prd);
        List<Integer> responses= this.getObjectAnswersFromSmallestMap(subject,predicate);

        if (responses != null) {
            if (reponse.isEmpty()) {
                reponse.addAll(responses);
            } else {
                reponse.retainAll(responses);
            }
            return 1;
        } else {
            return -1;
        }
    }

    public int getPredicateAnswer(StatementPattern statementPattern) {
        String sbj = statementPattern.getSubjectVar().getValue().toString();
        String obj = statementPattern.getObjectVar().getValue().toString();
        Integer subject = this.dictionnayParser.getDictionnaireInverse().get(sbj);
        Integer object = this.dictionnayParser.getDictionnaireInverse().get(obj);
        List<Integer> responses= this.getPredicateAnswersFromSmallestMap(subject,object);
        if (responses != null) {
            if (reponse.isEmpty()) {
                reponse.addAll(responses);
            } else {
                reponse.retainAll(responses);
            }
            return 1;
        } else {
            return -1;
        }
    }

    public int getSubjectAnswers(StatementPattern statementPattern) {
        String obj = statementPattern.getObjectVar().getValue().toString();
        String prd = statementPattern.getPredicateVar().getValue().toString();
        Integer object = this.dictionnayParser.getDictionnaireInverse().get(obj);
        Integer predicate = this.dictionnayParser.getDictionnaireInverse().get(prd);
        List<Integer> responses= this.getSubjectAnswersFromSmallestMap(object,predicate);
        if (responses!= null) {
            if (reponse.isEmpty()) {
                reponse.addAll(responses);
                System.err.println(1);
                return 1;
            } else {
                reponse.retainAll(responses);
                if (reponse.isEmpty()) {
                    return -1;
                }
                return 1;
            }
        } else {
            return -1;
        }
    }


    public List<Integer> getObjectAnswersFromSmallestMap(int subject, int predicate){
        if (this.dictionnayParser.getIndex().getSPOIndex().getHexastore().size() > this.dictionnayParser.getIndex().getPSOIndex().getHexastore().size()) {
            return this.dictionnayParser.getIndex().getPSOIndex().getHexastore().get(predicate).get(subject);
        } else {
            return this.dictionnayParser.getIndex().getSPOIndex().getHexastore().get(subject).get(predicate);
        }
    }

    public List<Integer> getSubjectAnswersFromSmallestMap(int object, int predicate){
        if (this.dictionnayParser.getIndex().getOPSIndex().getHexastore().size() > this.dictionnayParser.getIndex().getPOSIndex().getHexastore().size()) {
            return this.dictionnayParser.getIndex().getPOSIndex().getHexastore().get(predicate).get(object);
        } else {
            return this.dictionnayParser.getIndex().getOPSIndex().getHexastore().get(object).get(predicate);
        }
    }

    public List<Integer> getPredicateAnswersFromSmallestMap(int subject, int object){
        if (this.dictionnayParser.getIndex().getSPOIndex().getHexastore().size() > this.dictionnayParser.getIndex().getOSPIndex().getHexastore().size()) {
            return this.dictionnayParser.getIndex().getSOPIndex().getHexastore().get(object).get(subject);
        } else {
            return this.dictionnayParser.getIndex().getSOPIndex().getHexastore().get(subject).get(object);
        }
    }
}
