package projet;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.algebra.In;
import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.Str;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import qengine.program.RDFHandler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class RequestParser {
    static final String baseURI = null;

    /**
     * Votre répertoire de travail où vont se trouver les fichiers à lire
     */
    private final String workingDir = "data/";

    /**
     * Fichier contenant les requêtes sparql
     */
    private String queryFile;

    /**
     * Fichier contenant des données rdf
     */
    private final String dataFile = workingDir + "sample_data.nt";

    private DictionnayParser dictionnayParser;


    public RequestParser(String queryFile, DictionnayParser dictionnayParser) {
        this.queryFile = workingDir + queryFile;
        this.dictionnayParser = dictionnayParser;
    }

    public void processAQuery(ParsedQuery query) {
        List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
        List<Integer> reponse = new ArrayList<>();
        int rightApproach=this.getMissingVariable(patterns.get(0));
        for (int i = 0; i < patterns.size(); i++) {
            switch (rightApproach){
                case 1://object needed
                case 2://subject needed
                case 3://Predicate needed
                default://error
            }
            String obj = patterns.get(i).getObjectVar().getValue().toString();
            String prd = patterns.get(i).getPredicateVar().getValue().toString();
            Integer object = this.dictionnayParser.getDictionnaireInverse().get(obj);
            Integer predicate = this.dictionnayParser.getDictionnaireInverse().get(prd);

            if (this.dictionnayParser.getIndex().getPOSIndex().getHexastore().get(predicate).get(object) != null){
                if (reponse.isEmpty()){
                    reponse.addAll(this.dictionnayParser.getIndex().getPOSIndex().getHexastore().get(predicate).get(object));
                }else {
                    reponse.retainAll(this.dictionnayParser.getIndex().getPOSIndex().getHexastore().get(predicate).get(object));

                }

            }

        }
        Set<Integer> listfinale= new HashSet<>(reponse);
        reponse.clear();
        reponse.addAll(listfinale);
        for (int i = 0; i < reponse.size(); i++) {
            System.out.println(this.dictionnayParser.getDictionnaire().get(reponse.get(i)));
        }


    }

    public void parseQueries() throws FileNotFoundException, IOException {
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


                    processAQuery(query); // Traitement de la requête, à adapter/réécrire pour votre programme

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
    public int getMissingVariable(StatementPattern statementPattern){
        if(statementPattern.getObjectVar().getValue()==null){
            System.out.println("object needed");
            return 1;
        } else if (statementPattern.getSubjectVar().getValue()==null) {
            System.out.println("Subject needed");
            return 2;

        } else if (statementPattern.getPredicateVar().getValue()==null) {
            System.out.println("Predicate needed");
            return 3;
        }
        return 0 ;

    }

    public void getPredicateAnswers(){}
    public void getObjectAnswers(){}
    public void getSubjectAnswers(){}

}
