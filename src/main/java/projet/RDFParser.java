package projet;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.algebra.Str;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RDFParser {
    private final String dataFile;
    private final  String baseURI = null;


    public RDFParser(String url){
        this.dataFile = url;
    }


    public List<Statement> parseData() throws IOException {
        List<Statement> statementList = new ArrayList<>();
        File initialFile = new File(dataFile);
        InputStream targetStream = Files.newInputStream(initialFile.toPath());
        RDFFormat format = RDFFormat.NTRIPLES;
        try (GraphQueryResult res = QueryResults.parseGraphBackground(targetStream, baseURI, format)) {
            while (res.hasNext()) {
                statementList.add(res.next());
            }

        }
        catch (RDF4JException e) {
            // handle unrecoverable error
        }
        finally {
            targetStream.close();
        }
        return statementList;
    }

}
