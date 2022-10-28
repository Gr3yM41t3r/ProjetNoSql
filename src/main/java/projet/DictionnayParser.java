package projet;

import com.opencsv.CSVWriter;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.algebra.In;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionnayParser {

    private Map<Integer,String> dictionnaire;
    private Map<String,Integer> dictionnaireInverse;
    private RDFParser rdfParser ;
    private final String baseURI = null;

    /**
     * Votre répertoire de travail où vont se trouver les fichiers à lire
     */
    private final String workingDir = "data/";

    /**
     * Fichier contenant les requêtes sparql
     */
    private final String queryFile = workingDir + "sample_query.queryset";

    /**
     * Fichier contenant des données rdf
     */
    private final String dataFile = workingDir + "sample_data.nt";

    public DictionnayParser(){
        this.dictionnaire =  new HashMap<>();
        this.dictionnaireInverse = new HashMap<>();
        this.rdfParser =new RDFParser(this.dataFile);

    }


    public void createDictionnay() throws IOException {
        List<Statement> statementList = rdfParser.parseData();
        int max;
        for (Statement st:statementList) {
            if (this.dictionnaire.isEmpty()){
                max = 0;
            }else {
                max = Collections.max(dictionnaire.keySet());
            }
            if (!dictionnaire.containsValue(st.getSubject().toString())){
                dictionnaire.put( max+1,st.getSubject().toString());
                max++;
            }
            if (!dictionnaire.containsValue(st.getPredicate().toString())){
                dictionnaire.put(max+1,st.getPredicate().toString());
                max++;
            }
            if (!dictionnaire.containsValue(st.getObject().toString())){
                dictionnaire.put(max+1,st.getObject().toString());
                max++;
            }

        }
        createDictionnaireInverser();
    }

    public void createDictionnaireInverser(){
        for (Map.Entry<Integer, String> entry : dictionnaire.entrySet()) {
            this.dictionnaireInverse.put(entry.getValue(),entry.getKey());

        }

    }
    public void printDictionnary(){
        for (Integer integer: dictionnaire.keySet()) {
            String key = integer.toString();
            String value = dictionnaire.get(integer).toString();
            System.out.println("("+key + "," + value+")");
        }
        for (String  str: dictionnaireInverse.keySet()) {
            String key = str.toString();
            String value = dictionnaireInverse.get(str).toString();
            System.out.println("("+key + "," + value+")");
        }
    }


    public  void writeCsv(String filePath)
    {
        File file = new File(filePath);
        try {
            FileWriter outputfile = new FileWriter(file);
            String line="";
            List<Statement> statementList = rdfParser.parseData();
            for (Statement st:statementList) {
                line = "("+this.dictionnaireInverse.get(st.getSubject().toString())+","+this.dictionnaireInverse.get(st.getPredicate().toString())+","
                        +this.dictionnaireInverse.get(st.getObject().toString())+")"+"\n";
                outputfile.write(line);
            }

            // closing writer connection
            outputfile.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void hexastore(String filePath) throws IOException {
        String line="";
        List<Statement> statementList = rdfParser.parseData();
        try {
            File spo = new File(filePath+"/spo");
            File pso = new File(filePath+"/pso");
            File osp = new File(filePath+"/osp");
            File sop = new File(filePath+"/sop");
            File pos = new File(filePath+"/pos");
            File ops = new File(filePath+"ops");
            FileWriter spoOutput = new FileWriter(spo);
            FileWriter psoOutput = new FileWriter(pso);
            FileWriter ospOutput = new FileWriter(osp);
            FileWriter sopOutput = new FileWriter(sop);
            FileWriter posOutput = new FileWriter(pos);
            FileWriter opsOutput = new FileWriter(ops);
            for (Statement st:statementList) {
                line = "("+this.dictionnaireInverse.get(st.getSubject().toString())+","+
                        this.dictionnaireInverse.get(st.getPredicate().toString())+","
                        +this.dictionnaireInverse.get(st.getObject().toString())+")"+"\n";
                spoOutput.write(line);


                line = "("+this.dictionnaireInverse.get(st.getPredicate().toString())+","+
                        this.dictionnaireInverse.get(st.getSubject().toString())+","
                        +this.dictionnaireInverse.get(st.getObject().toString())+")"+"\n";
                psoOutput.write(line);


                line = "("+this.dictionnaireInverse.get(st.getObject().toString())+","+
                        this.dictionnaireInverse.get(st.getSubject().toString())+","
                        +this.dictionnaireInverse.get(st.getPredicate().toString())+")"+"\n";
                ospOutput.write(line);


                line = "("+this.dictionnaireInverse.get(st.getSubject().toString())+","+
                        this.dictionnaireInverse.get(st.getObject().toString())+","
                        +this.dictionnaireInverse.get(st.getPredicate().toString())+")"+"\n";
                sopOutput.write(line);


                line = "("+this.dictionnaireInverse.get(st.getPredicate().toString())+","+
                        this.dictionnaireInverse.get(st.getObject().toString())+","
                        +this.dictionnaireInverse.get(st.getSubject().toString())+")"+"\n";
                posOutput.write(line);


                line = "("+this.dictionnaireInverse.get(st.getObject().toString())+","+
                        this.dictionnaireInverse.get(st.getPredicate().toString())+","
                        +this.dictionnaireInverse.get(st.getSubject().toString())+")"+"\n";
                opsOutput.write(line);

            }
            spoOutput.close();
            psoOutput.close();
            ospOutput.close();
            sopOutput.close();
            spoOutput.close();
            posOutput.close();
            opsOutput.close();



        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
