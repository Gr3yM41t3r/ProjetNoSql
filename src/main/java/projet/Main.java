package projet;

import org.eclipse.rdf4j.model.Statement;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        DictionnayParser dictionnayParser = new DictionnayParser();
        dictionnayParser.parseStatementList();
        dictionnayParser.createDictionnay();
        dictionnayParser.printDictionnary();
        dictionnayParser.saveDictionnary("data/out/");
        dictionnayParser.createIndexes(dictionnayParser.getStatementsList());
        dictionnayParser.saveIndexes("data/out/");



    }
}
