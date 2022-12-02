package projet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;





import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

public class test {
    public static void main(String[] args) throws IOException {
        String data ="/home/e20210000431/Bureau/qengine-master/data/100K.nt";
        String queries ="/home/e20210000431/Bureau/qengine-master/data/sample_query.queryset";
        String output ="";
        Jena jena = new Jena(data,queries,output);
        jena.loadModel();
        jena.parseQueries();

    }

}