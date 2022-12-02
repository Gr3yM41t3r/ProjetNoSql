package projet;

import org.eclipse.rdf4j.model.Statement;
import qengine.program.RDFHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionnayParser {


    private final Map<Integer, String> dictionnaire;
    private final Map<String, Integer> dictionnaireInverse;
    private final RDFHandler rdfHandler;
    private final HexaStore index = new HexaStore();
    private final String dataFile;
    private List<Statement> statementsList;

    private Evaluation timeBenchmark;

    public DictionnayParser(String df, Evaluation timeBenchmark) {
        this.dictionnaire = new HashMap<>();
        this.dictionnaireInverse = new HashMap<>();
        this.rdfHandler = new RDFHandler();
        this.dataFile =  df;
        this.timeBenchmark=timeBenchmark;

    }


    public void parseStatementList() throws IOException {
        timeBenchmark.startTimer();
        this.statementsList = rdfHandler.parseData(this.dataFile);
        timeBenchmark.addBenchmarkData("nombre_triples",String.valueOf(this.statementsList.size()));
        timeBenchmark.addBenchmarkData("temps_lecture_données (ms)", timeBenchmark.endTimer());
    }

    //-----------------------Dictionnary Utils -----------------------------------


    public void createDictionnay() throws IOException {
        timeBenchmark.startTimer();

        if (this.statementsList == null) {
            System.err.println("List null: vous devez initialiser la liste: parseStatementList()");
            return;
        }
        int max;
        if (this.dictionnaire.isEmpty()) {
            max = 0;
        } else {
            max = Collections.max(dictionnaire.keySet());
        }
        int total = this.statementsList.size();
        for (int i =0;i<total;i++) {
            printProgress("Parsing",(i*100)/total);
            if(!dictionnaireInverse.containsKey(statementsList.get(i).getSubject().toString())){
                dictionnaireInverse.put(statementsList.get(i).getSubject().toString(),max);
                max++;
            }
            if(!dictionnaireInverse.containsKey(statementsList.get(i).getPredicate().toString())){
                dictionnaireInverse.put(statementsList.get(i).getPredicate().toString(),max);
                max++;
            }
            if(!dictionnaireInverse.containsKey(statementsList.get(i).getObject().toString())){
                dictionnaireInverse.put(statementsList.get(i).getObject().toString(),max);
                max++;
            }

        }
        createDictionnaireInverser();
        timeBenchmark.addBenchmarkData("temps_creation_dico (ms)", timeBenchmark.endTimer().toString());

    }


    public void createDictionnaireInverser() {
        for (Map.Entry<String, Integer> entry : dictionnaireInverse.entrySet()) {
            this.dictionnaire.put(entry.getValue(), entry.getKey());
        }

    }


    public void saveDictionnary(String filePath) throws IOException {
        File keyValueDict = new File(filePath + "/dict_key_value.txt");
        File valueKeyDict = new File(filePath + "/dict_value_key.txt");
        FileWriter keyValueDictOut = new FileWriter(keyValueDict);
        FileWriter valueKeyDictOut = new FileWriter(valueKeyDict);
        String line = "";
        for (Integer integer : dictionnaire.keySet()) {
            String key = integer.toString();
            String value = dictionnaire.get(integer);
            line = "(" + key + "," + value + ") \n";
            keyValueDictOut.write(line);
        }
        for (String str : dictionnaireInverse.keySet()) {
            String key = str;
            String value = dictionnaireInverse.get(str).toString();
            line = "(" + key + "," + value + ") \n";
            valueKeyDictOut.write(line);
        }
        keyValueDictOut.close();
        valueKeyDictOut.close();
    }


    //-----------------------Indexes Utils -----------------------------------

    public void createIndexes(List<Statement> statementList) {
        timeBenchmark.startTimer();
        if (this.statementsList == null) {
            System.err.println("List null: vous devez initialiser la liste: parseStatementList()");
            return;
        }
        int predicate;
        int object;
        int subject;
        for (Statement st : statementList) {
            predicate = this.dictionnaireInverse.get(st.getPredicate().toString());
            object = this.dictionnaireInverse.get(st.getObject().toString());
            subject = this.dictionnaireInverse.get(st.getSubject().toString());
            this.index.getSPOIndex().add(subject, predicate, object);
            this.index.getPSOIndex().add(predicate, subject, object);
            this.index.getOSPIndex().add(object, subject, predicate);
            this.index.getSOPIndex().add(subject, object, predicate);
            this.index.getPOSIndex().add(predicate, object, subject);
            this.index.getOPSIndex().add(object, predicate, subject);
        }
        timeBenchmark.addBenchmarkData("temps_creation_index (ms)", timeBenchmark.endTimer().toString());
        timeBenchmark.addBenchmarkData("nombre_index", String.valueOf(this.index.getSPOIndex().getHexastore().size()+
                this.index.getPSOIndex().getHexastore().size()+
                this.index.getOSPIndex().getHexastore().size()+
                this.index.getSOPIndex().getHexastore().size()+
                this.index.getPOSIndex().getHexastore().size()+
                this.index.getOPSIndex().getHexastore().size())
        );

    }


    public void saveIndexes(String filePath) throws IOException {
        try {
            File spo = new File(filePath + "/spo");
            File pso = new File(filePath + "/pso");
            File osp = new File(filePath + "/osp");
            File sop = new File(filePath + "/sop");
            File pos = new File(filePath + "/pos");
            File ops = new File(filePath + "ops");
            FileWriter spoOutput = new FileWriter(spo);
            FileWriter psoOutput = new FileWriter(pso);
            FileWriter ospOutput = new FileWriter(osp);
            FileWriter sopOutput = new FileWriter(sop);
            FileWriter posOutput = new FileWriter(pos);
            FileWriter opsOutput = new FileWriter(ops);


            spoOutput.write(this.index.getSPOIndex().print());
            psoOutput.write(this.index.getPSOIndex().print());
            ospOutput.write(this.index.getOPSIndex().print());
            sopOutput.write(this.index.getSOPIndex().print());
            posOutput.write(this.index.getPOSIndex().print());
            opsOutput.write(this.index.getOPSIndex().print());


            spoOutput.close();
            psoOutput.close();
            ospOutput.close();
            sopOutput.close();
            spoOutput.close();
            posOutput.close();
            opsOutput.close();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //---------------------------- Getters Setters----------------------------
    public List<Statement> getStatementsList() {
        return statementsList;
    }

    public void setStatementsList(List<Statement> statementsList) {
        this.statementsList = statementsList;
    }

    public RDFHandler getRdfHandler() {
        return rdfHandler;
    }

    public HexaStore getIndex() {
        return index;
    }

    public String getDataFile() {
        return dataFile;
    }

    public Map<Integer, String> getDictionnaire() {
        return dictionnaire;
    }

    public Map<String, Integer> getDictionnaireInverse() {
        return dictionnaireInverse;
    }


    public void printProgress(String msg,int progess) {
        char[] animationChars = new char[]{'|', '/', '-', '\\'};
        System.out.print(msg+" : " + progess + "% " + animationChars[progess % 4] + "\r");
    }
}
