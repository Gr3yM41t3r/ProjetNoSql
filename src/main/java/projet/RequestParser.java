package projet;

import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class RequestParser {


    private final String queryFile;
    private final DictionnayParser dictionnayParser;
    private final List<Integer> reponse = new ArrayList<>();
    private final String outputPath;
    private Jena jena;
    private final Evaluation timeBenchmark;

    private long totalqueryTime = 0;

    private final boolean exportQueryResult;
    private final boolean exportJenaResult;

    private int queriesThatDidntMatch = 0;
    private int queriesThatDidMatch = 0;
    private int queriesWithoutAnswer = 0;
    private int queriesWithAnswer = 0;


    public RequestParser(String queryFile, DictionnayParser dictionnayParser, String output, Boolean compareToJena, Boolean exportQueryResult, Boolean exportJenaResult, Evaluation timeBenchmark) throws IOException {
        this.queryFile = queryFile;
        this.dictionnayParser = dictionnayParser;

        this.outputPath = output;
        this.timeBenchmark = timeBenchmark;
        this.exportQueryResult = exportQueryResult;
        this.exportJenaResult = exportJenaResult;
        if (compareToJena) {
            jena = new Jena(dictionnayParser.getDataFile(), queryFile, output);
            jena.loadModel();
        }
    }

    public void processAQuery(ParsedQuery query, String querynotrParsed, PrintWriter resultOutput, PrintWriter jenaResultOutput) throws IOException {
        long startTime = System.currentTimeMillis();
        List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
        int rightApproach = this.getMissingVariable(patterns.get(0));
        int a;
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
                    a = getPredicateAnswer(patterns.get(i));
                    break;
            }
            if (a == -1) {
                reponse.clear();
                break;
            }
        }
        Set<String> reponseSet = new HashSet<>();
        //System.out.println("---------------------------");

        //resultOutput.println("---------------------------");
        //resultOutput.println(querynotrParsed);
        for (Integer integer : reponse) {
            reponseSet.add(this.dictionnayParser.getDictionnaire().get(integer));
            if (exportQueryResult) {
                resultOutput.println(this.dictionnayParser.getDictionnaire().get(integer));
            }

        }


        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        totalqueryTime += duration;
        // System.out.println("query time : " +duration+" (ms)");
        if (jena != null) {
            compareResultsToJena(querynotrParsed, reponseSet, jenaResultOutput);
        }
        reponse.clear();


    }

    public void getEmptyQueries(ParsedQuery query, String querynotrParsed,PrintWriter emptyQueriesOutPutFile) throws IOException {

        List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
        int rightApproach = this.getMissingVariable(patterns.get(0));
        int a;
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
                    a = getPredicateAnswer(patterns.get(i));
                    break;
            }
            if (a == -1) {
                reponse.clear();
                break;
            }
        }
        if (reponse.size() == 0) {
            emptyQueriesOutPutFile.println(querynotrParsed);
            queriesWithoutAnswer++;
        }else {
            queriesWithAnswer++;
        }
        reponse.clear();

    }

    public void compareResultsToJena(String queryString, Set<String> answerSet, PrintWriter jenaResultOutput) throws IOException {
        Set<String> jenasResponse = jena.processAQuery(queryString);
        jenaResultOutput.println("---------------------------");
        jenaResultOutput.println(queryString);
        if (jenasResponse.equals(answerSet)) {
            queriesThatDidMatch++;
            if (exportJenaResult) {
                for (String result : jenasResponse) {
                    jenaResultOutput.println(result);
                }
            }
        } else {
            queriesThatDidntMatch++;

        }
    }


    public void parseQueries(boolean shuffle, Integer warmPercentage) throws IOException {
        System.out.println("start");
        File resultfile = new File(this.outputPath + "/query_result.txt");
        PrintWriter outFile = new PrintWriter(new FileOutputStream(resultfile, false));
        File jenaReslatFile = new File(this.outputPath + "/jena_query_result.txt");
        PrintWriter jenaOutFile = new PrintWriter(new FileOutputStream(jenaReslatFile, false));
        ArrayList<String> querylist = new ArrayList<>();
        File[] listOfQueries = new File(this.queryFile).listFiles();
        for (int j = 0; j < listOfQueries.length; j++) {
            querylist.clear();
            System.out.print("\r + traitement de " + listOfQueries[j].getName() + " " + j + "/" + listOfQueries.length);
            try (Stream<String> lineStream = Files.lines(Paths.get(listOfQueries[j].getPath()))) {
                SPARQLParser sparqlParser = new SPARQLParser();
                Iterator<String> lineIterator = lineStream.iterator();
                StringBuilder queryString = new StringBuilder();
                long startTime = System.currentTimeMillis();
                int index = 0;
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    queryString.append(line);
                    if (line.trim().endsWith("}")) {
                        querylist.add(queryString.toString());
                        queryString.setLength(0);
                    }
                }
                if (shuffle || warmPercentage > 0) {
                    System.out.println("Collection shuffeled");
                    Collections.shuffle(querylist);
                }
                if (warmPercentage > 0) {
                    System.out.println("warming up on " + warmPercentage + "% (" + (warmPercentage * querylist.size()) / 100 + " triplets) of dataset");
                    for (int i = 0; i < (warmPercentage * querylist.size()) / 100; i++) {
                        ParsedQuery parsedQuery = sparqlParser.parseQuery(querylist.get(i), null);
                        System.out.println();
                        processAQuery(parsedQuery, querylist.get(i), outFile, jenaOutFile);
                    }
                } else {

                    for (int i = 0; i < querylist.size(); i++) {
                        //System.out.print("\r File "+j +"/"+listOfQueries.length+"  querie  n "+i +"/"+querylist.size());
                        int duplicate = Collections.frequency(querylist, querylist.get(i));
                        if (duplicate > 1) {
                            System.out.println(duplicate);
                        }
                        ParsedQuery parsedQuery = sparqlParser.parseQuery(querylist.get(i), null);
                        processAQuery(parsedQuery, querylist.get(i), outFile, jenaOutFile);
                    }
                }

                long endTime = System.currentTimeMillis();
                timeBenchmark.addBenchmarkData("temps_total_eval_workload (ms)", String.valueOf(endTime - startTime));
                timeBenchmark.addBenchmarkData("temps_lecture_requetes (ms)", String.valueOf((endTime - startTime) - totalqueryTime));
                timeBenchmark.addBenchmarkData("temps_total_requetes (ms)", String.valueOf(totalqueryTime));
                timeBenchmark.addBenchmarkData("nombre_requetes", String.valueOf(querylist.size()));
            }
        }

        outFile.close();
        jenaOutFile.close();
    }

    public void parseAndGetEmptyQueries() throws IOException {
        File resultfile = new File(this.outputPath + "/empty_queries.txt");
        PrintWriter emptyQueriesOutPutFile = new PrintWriter(new FileOutputStream(resultfile, false));
        ArrayList<String> querylist = new ArrayList<>();
        File[] listOfQueries = new File(this.queryFile).listFiles();
        for (int j = 0; j < listOfQueries.length; j++) {
            querylist.clear();
            try (Stream<String> lineStream = Files.lines(Paths.get(listOfQueries[j].getPath()))) {
                SPARQLParser sparqlParser = new SPARQLParser();
                Iterator<String> lineIterator = lineStream.iterator();
                StringBuilder queryString = new StringBuilder();
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    queryString.append(line);
                    if (line.trim().endsWith("}")) {
                        querylist.add(queryString.toString());
                        queryString.setLength(0);
                    }
                }
                    for (int i = 0; i < querylist.size(); i++) {
                        ParsedQuery parsedQuery = sparqlParser.parseQuery(querylist.get(i), null);
                        getEmptyQueries(parsedQuery, querylist.get(i),emptyQueriesOutPutFile);
                    }

            }
        }
        emptyQueriesOutPutFile.close();
        int totalquerie = queriesWithAnswer+queriesWithoutAnswer;
        System.out.println(totalquerie);
        System.out.println(queriesWithoutAnswer);
        System.out.println((queriesWithoutAnswer*100)/totalquerie);


    }

    public void parseAndgetDuplicateQueries() throws IOException {
        File resultfile = new File(this.outputPath + "/duplicate_queries.txt");
        PrintWriter emptyQueriesOutPutFile = new PrintWriter(new FileOutputStream(resultfile, false));
        ArrayList<String> querylist = new ArrayList<>();
        File[] listOfQueries = new File(this.queryFile).listFiles();
        for (int j = 0; j < listOfQueries.length; j++) {
            querylist.clear();
            try (Stream<String> lineStream = Files.lines(Paths.get(listOfQueries[j].getPath()))) {
                SPARQLParser sparqlParser = new SPARQLParser();
                Iterator<String> lineIterator = lineStream.iterator();
                StringBuilder queryString = new StringBuilder();
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    queryString.append(line);
                    if (line.trim().endsWith("}")) {
                        querylist.add(queryString.toString());
                        queryString.setLength(0);
                    }
                }
                int duplicate=0;
                for (int i = 0; i < querylist.size(); i++) {

                    if (Collections.frequency(querylist, querylist.get(i))> 1) {
                        duplicate++;
                    }
                }

            }
        }
        emptyQueriesOutPutFile.close();
        int totalquerie = queriesWithAnswer+queriesWithoutAnswer;
        System.out.println(totalquerie);
        System.out.println(queriesWithoutAnswer);
        System.out.println((queriesWithoutAnswer*100)/totalquerie);
        System.out.println("duplicate :" +1);


    }




















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
        if (predicate == null || subject == null) {
            return -1;
        }
        List<Integer> responses = this.getObjectAnswersFromSmallestMap(subject, predicate);
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
        if (object == null || subject == null) {
            return -1;
        }
        List<Integer> responses = this.getPredicateAnswersFromSmallestMap(subject, object);
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
        if (object == null || predicate == null) {
            return -1;
        }
        List<Integer> responses = this.getSubjectAnswersFromSmallestMap(object, predicate);
        if (responses != null) {
            if (reponse.isEmpty()) {
                reponse.addAll(responses);
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


    public List<Integer> getObjectAnswersFromSmallestMap(int subject, int predicate) {
        if (this.dictionnayParser.getIndex().getSPOIndex().getHexastore().size() > this.dictionnayParser.getIndex().getPSOIndex().getHexastore().size()) {
            return this.dictionnayParser.getIndex().getPSOIndex().getHexastore().get(predicate).get(subject);
        } else {
            return this.dictionnayParser.getIndex().getSPOIndex().getHexastore().get(subject).get(predicate);
        }
    }

    public List<Integer> getSubjectAnswersFromSmallestMap(int object, int predicate) {
        if (this.dictionnayParser.getIndex().getOPSIndex().getHexastore().size() > this.dictionnayParser.getIndex().getPOSIndex().getHexastore().size()) {
            return this.dictionnayParser.getIndex().getPOSIndex().getHexastore().get(predicate).get(object);

        } else {
            return this.dictionnayParser.getIndex().getOPSIndex().getHexastore().get(object).get(predicate);
        }
    }

    public List<Integer> getPredicateAnswersFromSmallestMap(int subject, int object) {
        if (this.dictionnayParser.getIndex().getSPOIndex().getHexastore().size() > this.dictionnayParser.getIndex().getOSPIndex().getHexastore().size()) {
            return this.dictionnayParser.getIndex().getSOPIndex().getHexastore().get(object).get(subject);
        } else {
            return this.dictionnayParser.getIndex().getSOPIndex().getHexastore().get(subject).get(object);
        }
    }

    public int getQueriesThatDidntMatch() {
        return queriesThatDidntMatch;
    }

    public int getQueriesThatDidMatch() {
        return queriesThatDidMatch;
    }
}
