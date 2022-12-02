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

    private File outputFile;
    private PrintWriter oFile;

    public Jena(String data, String queries, String output) throws IOException {
        this.data = data;
        this.queries = queries;
        this.outputFolder = output;
        this.model = null;
        outputFile = new File(outputFolder+"/jena_solutions.txt");
        outputFile.createNewFile(); // if file already exists will do nothing

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
                    System.out.println("amine "+queryString);
                    processAQuery(queryString.toString()); // Traitement de la requête, à adapter/réécrire pour votre programme
                    queryString.setLength(0); // Reset le buffer de la requête en chaine vide
                }
            }
        }
    }

    public void loadModel(){
        this.model = FileManager.get().loadModel(this.data);

    }

    public Set<String> processAQuery(String quer) throws IOException {
        Set<String> reponse = new HashSet<>();
        System.err.println("wa3  "+quer);
        Query query = QueryFactory.create(quer);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet set =queryExecution.execSelect();
        while (set.hasNext()){
            QuerySolution solution = set.nextSolution();
            reponse.add(String.valueOf(solution.get(solution.varNames().next())));
        }

        return  reponse;


    }
}
