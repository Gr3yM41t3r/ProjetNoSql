package projet;

import org.eclipse.rdf4j.query.algebra.In;

import java.io.IOException;
import java.util.Objects;

public class Main {
    private static String dataFile;
    private static String queriesFile;
    private static String output;
    private static Boolean executeJena;
    private static Boolean shuffle;
    private static Integer warmPercentage;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()){
                case"-queries":
                    queriesFile=args[i+1];
                    break;
                case"-data":
                    dataFile=args[i+1];
                    break;
                case"-output":
                    output=args[i+1];
                    break;
                case"-warm":
                    try {
                        warmPercentage= Integer.valueOf(args[i+1]);
                        if (warmPercentage>100 || warmPercentage<0){
                            System.err.println("value must be between 0 and 100");
                        }
                    }catch (Exception e){
                        System.err.println("warm value is a number");
                    }
                    break;
                case"-shuffle":
                    shuffle=true;
                    break;
                case"-jena":
                    executeJena=true;
                    break;

            }

        }
        if(dataFile==null){
            throw new Exception("dataFile argument  is missing");
        }
        if(output==null){
            throw new Exception("output argument  is missing");
        }
        if(queriesFile==null){
            throw new Exception("queriesFile argument  is missing");
        }

        start();



    }
    public static void start() throws IOException {
        DictionnayParser dictionnayParser = new DictionnayParser(dataFile);
        dictionnayParser.parseStatementList();
        dictionnayParser.createDictionnay();
        dictionnayParser.createIndexes(dictionnayParser.getStatementsList());
        RequestParser requestParser = new RequestParser(queriesFile,dictionnayParser,output,true);
        requestParser.parseQueries();

    }
}
