package projet;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

public class Jena {

    private final String data;
    private final String queries;
    private final String outputFolder;

    private Model model;


    public Jena(String data, String queries, String output) throws IOException {
        this.data = data;
        this.queries = queries;
        this.outputFolder = output;
        this.model = null;


    }

    public void parseQueries() throws IOException {
        try (Stream<String> lineStream = Files.lines(Paths.get(this.queries))) {
            Iterator<String> lineIterator = lineStream.iterator();
            StringBuilder queryString = new StringBuilder();
            while (lineIterator.hasNext()) {
                String line = lineIterator.next();
                queryString.append(line);
                if (line.trim().endsWith("}")) {
                    processAQuery(queryString.toString()); // Traitement de la requête, à adapter/réécrire pour votre programme
                    queryString.setLength(0); // Reset le buffer de la requête en chaine vide
                }
            }
        }
    }

    public void loadModel(){
        this.model = FileManager.get().loadModel(this.data);

    }

    public Set<String> processAQuery(String queryString) throws IOException {
        Set<String> reponse = new HashSet<>();
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet set =queryExecution.execSelect();
        while (set.hasNext()){
            QuerySolution solution = set.nextSolution();
            reponse.add(String.valueOf(solution.get(solution.varNames().next())));
        }

        return  reponse;


    }
}
