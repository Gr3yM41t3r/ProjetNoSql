package projet;

import java.io.IOException;

public class Main2 {
    public static void main(String[] args) throws IOException {
        //creation du dictionnaire parser
        //long startTime = System.nanoTime();
        DictionnayParser dictionnayParser = new DictionnayParser("100K.nt");
        // on parse le fichier data
        dictionnayParser.parseStatementList();
        //creer le dictionnaire
        dictionnayParser.createDictionnay();
        //sauvegarder le  dictionnaire dans le fichier data/out/dict__.txt (pas obligatoire)
        dictionnayParser.saveDictionnary("data/out/");
        //creer les index à partir des dictionnaires
        dictionnayParser.createIndexes(dictionnayParser.getStatementsList());

        RequestParser requestParser = new RequestParser("sample_query.queryset",dictionnayParser,"",false);
        requestParser.parseQueries();

       // long endTime = System.nanoTime();
        //long duration = (endTime - startTime);
      //  System.out.println("total execution time : " +duration/1_000_000_000+"s (" +duration/1_000_000+"ms)");






    }
}
