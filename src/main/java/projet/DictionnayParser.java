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

    private final String baseURI = null;
    /**
     * Votre répertoire de travail où vont se trouver les fichiers à lire
     */
    private final String workingDir = "data/";
    /**
     * Fichier contenant les requêtes sparql
     */
    private  String queryFile = workingDir + "sample_query.queryset";
    /**
     * Fichier contenant des données rdf
     */
    private  String dataFile ;
    private final Map<Integer, String> dictionnaire;
    private final Map<String, Integer> dictionnaireInverse;
    private final RDFHandler rdfHandler;
    private final HexaStore index = new HexaStore();
    private List<Statement> statementsList;

    public DictionnayParser(String df) {
        this.dictionnaire = new HashMap<>();
        this.dictionnaireInverse = new HashMap<>();
        this.rdfHandler = new RDFHandler();
        this.dataFile = workingDir+df;
    }


    public void parseStatementList() throws IOException {
        this.statementsList = rdfHandler.parseData(dataFile);
    }

    //-----------------------Dictionnary Utils -----------------------------------

    /*****
     * TODO: Use AtomicInteger
     * inster of integer to autoIncrement key
     * @throws IOException
     *
     */
    public void createDictionnay() throws IOException {
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
        for (Statement st : this.statementsList) {
            if (!dictionnaire.containsValue(st.getSubject().toString())) {
                dictionnaire.put(max + 1, st.getSubject().toString());
                max++;
            }
            if (!dictionnaire.containsValue(st.getPredicate().toString())) {
                dictionnaire.put(max + 1, st.getPredicate().toString());
                max++;
            }
            if (!dictionnaire.containsValue(st.getObject().toString())) {
                dictionnaire.put(max + 1, st.getObject().toString());
                max++;
            }

        }
        createDictionnaireInverser();
    }


    public void createDictionnaireInverser() {
        for (Map.Entry<Integer, String> entry : dictionnaire.entrySet()) {
            this.dictionnaireInverse.put(entry.getValue(), entry.getKey());
        }

    }

    public void printDictionnary() {
        for (Integer integer : dictionnaire.keySet()) {
            String key = integer.toString();
            String value = dictionnaire.get(integer);
            System.out.println("(" + key + "," + value + ")");
        }
        for (String str : dictionnaireInverse.keySet()) {
            String key = str;
            String value = dictionnaireInverse.get(str).toString();
            System.out.println("(" + key + "," + value + ")");
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
    }


    public void saveIndexes(String filePath) throws IOException {
        String line = "";
        List<Statement> statementList = rdfHandler.parseData(dataFile);
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
}
