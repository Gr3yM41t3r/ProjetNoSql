package qengine.program;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class RDFHandler extends AbstractRDFHandler {
    List<Statement> statements;

    public RDFHandler() {
        this.statements = new ArrayList<>();
    }

    @Override
    public void handleStatement(Statement st) {

        statements.add(st);
    }

    public List<Statement> getStatments() {
        return this.statements;
    }

    public List<Statement> parseData(String dataFile) throws IOException {
        this.statements.clear();
        try (Reader dataReader = new FileReader(dataFile)) {
            // On va parser des données au format ntriples
            RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
            // On utilise notre implémentation de handler
            rdfParser.setRDFHandler(this);
            // Parsing et traitement de chaque triple par le handler
            rdfParser.parse(dataReader, null);
            return this.getStatments();

        }
    }


    /**
     * FOr Debug Only
    *
    * **/
    public List<Statement> parseOneLine(InputStream inputStream) throws IOException {
        this.statements.clear();
        // On va parser des données au format ntriples
        RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
        // On utilise notre implémentation de handler
        rdfParser.setRDFHandler(this);
        // Parsing et traitement de chaque triple par le handler
        rdfParser.parse(inputStream, null);
        return this.getStatments();


    }


}
