package projet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static String dataFile;
    private static String queriesFile;
    private static String output;
    private static Integer warmPercentage=-1;
    private static Boolean executeJena = false;
    private static Boolean shuffle = false;
    private static  Boolean saveResulats = false;

    private static  Boolean saveJenaResulats = false;

    public static void main(String[] args) throws Exception {
        checkSyntaxe(args);
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "-queries":
                    queriesFile = args[i + 1];
                    break;
                case "-data":
                    dataFile = args[i + 1];
                    break;
                case "-output":
                    output = args[i + 1];
                    break;
                case "-warm":
                    try {
                        warmPercentage = Integer.valueOf(args[i + 1]);
                        if (warmPercentage > 100 || warmPercentage < 0) {
                            throw new Exception("value must be between 0 and 100");
                        }
                    } catch (Exception e) {
                        throw new Exception("value must be between 0 and 100");
                    }
                    break;
                case "-shuffle":
                    shuffle = true;
                    break;
                case "-jena":
                    executeJena = true;
                    break;
                case "-export_query_results":
                    saveResulats = true;
                    break;
                case "-export_jena_resulats":
                    saveJenaResulats = true;
                    break;

            }

        }
        if (dataFile == null) {
            throw new Exception("dataFile argument  is missing");
        }
        if (output == null) {
            throw new Exception("output argument  is missing");
        }
        if (queriesFile == null) {
            throw new Exception("queriesFile argument  is missing");
        }
        if(saveJenaResulats  &&!executeJena){
            throw new Exception("to save jena results you need to add -jena arguments");

        }
        if (executeJena) {
            System.out.print("WARNING: activating jena verification will make program run for longer, resuming in " + 3 + " sec");
            Thread.sleep(3000);
        }
        Evaluation timeBenchmark = new Evaluation();

        start(dataFile, queriesFile, output, warmPercentage, executeJena, shuffle, saveResulats,saveJenaResulats, timeBenchmark);

    }
    public static void checkSyntaxe(String[] args) throws Exception {
        ArrayList<String> possibleArg=new ArrayList<>(List.of("-queries","-data","-output","-jena","-warm","-shuffle","-export_query_results","-export_jena_resulats"));
        ArrayList<String> argumentPassed=new ArrayList<>();
        for(String st : args){
            if(st.startsWith("-")){
                argumentPassed.add(st);
            }
        }
        for (String arg:argumentPassed) {
            if (!possibleArg.contains(arg.toLowerCase())){
                System.out.println();
                throw new Exception("unknow keyword "+arg+"\n"+"possible args are "+possibleArg.toString());

            }
        }


    }

    public static void start(String datafile, String queriesfile, String outputfile, Integer warmPercentage, Boolean jenaCheck,
                             Boolean shuffle, Boolean saveResulats, Boolean saveJenaResulats, Evaluation evaluationData) throws IOException {
        long startTime = System.currentTimeMillis();
        DictionnayParser dictionnayParser = new DictionnayParser(datafile, evaluationData);
        dictionnayParser.parseStatementList();
        dictionnayParser.createDictionnay();
        dictionnayParser.createIndexes(dictionnayParser.getStatementsList());
        RequestParser requestParser = new RequestParser(queriesfile, dictionnayParser, outputfile, jenaCheck,saveResulats,saveJenaResulats, evaluationData);
        //requestParser.parseAndGetEmptyQueries();
        requestParser.parseAndgetDuplicateQueries();
        /*long endTime = System.currentTimeMillis();
        evaluationData.addBenchmarkData("temps_total (ms)", String.valueOf(endTime - startTime));
        File file = new File(outputfile + "/evaluation_data.csv");
        PrintWriter outFile = new PrintWriter(file);

        System.err.println("queries that matched jena' s: "+requestParser.getQueriesThatDidMatch());
        System.err.println("queries that didnt matche jena' s: "+requestParser.getQueriesThatDidntMatch());
        for (String key : evaluationData.getData().keySet()) {
            outFile.print(key+",");
            System.out.println(key + " : " + evaluationData.getData().get(key) );
        }
        outFile.println();

        for (String key : evaluationData.getData().keySet()) {
            outFile.print(evaluationData.getData().get(key)+",");
        }
        outFile.close();*/

    }
}
