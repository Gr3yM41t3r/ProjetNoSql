package projet;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Jena {

    private final String data;
    private final String queries;
    private final String output;

    private Model model;

    public Jena(String data, String queries, String output) {
        this.data = data;
        this.queries = queries;
        this.output = output;
        this.model = null;
    }

    public void parseQueries() throws IOException {
        try (Stream<String> lineStream = Files.lines(Paths.get(this.queries))) {
            SPARQLParser sparqlParser = new SPARQLParser();
            Iterator<String> lineIterator = lineStream.iterator();
            StringBuilder queryString = new StringBuilder();
            while (lineIterator.hasNext()) {
                String line = lineIterator.next();
                queryString.append(line);
                if (line.trim().endsWith("}")) {
                    //System.err.println(query.getTupleExpr());
                    processAQuery(queryString.toString()); // Traitement de la requête, à adapter/réécrire pour votre programme
                    queryString.setLength(0); // Reset le buffer de la requête en chaine vide
                }
            }
        }
    }

    public void loadModel(){
        this.model = FileManager.get().loadModel(this.data);

    }

    public void processAQuery(String dff) {
        List<String> reponse = new ArrayList<>();
        System.out.println("---------------------------------------------------------------");;
        Query query = QueryFactory.create(dff);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet set =queryExecution.execSelect();
        while (set.hasNext()){
            QuerySolution solution = set.nextSolution();
            reponse.add(String.valueOf(solution.get(solution.varNames().next())));
        }
        Collections.sort(reponse);
        for (String line:reponse) {
            System.out.println(line);
        }
        reponse.clear();


    }
}
