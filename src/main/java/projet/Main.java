package projet;

import org.eclipse.rdf4j.model.Statement;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        //creation du dictionnaire parser
        DictionnayParser dictionnayParser = new DictionnayParser("100K.nt");
        // on parse le fichier data
        dictionnayParser.parseStatementList();
        //creer le dictionnaire
        dictionnayParser.createDictionnay();
        //sauvegarder le  dictionnaire dans le fichier data/out/dict__.txt (pas obligatoire)
        dictionnayParser.saveDictionnary("data/out/");
        //creer les index à partir des dictionnaires
        dictionnayParser.createIndexes(dictionnayParser.getStatementsList());

        RequestParser requestParser = new RequestParser("sample_query.queryset",dictionnayParser);
        requestParser.parseQueries();





    }
}
