package projet.hexastore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spo {
    private Map<Integer,Map<Integer,List<Integer>>> spoList ;

    public Spo(){
        this.spoList = new HashMap<>();
    }

    public Map<Integer, Map<Integer, List<Integer>>> getSpoList() {
        return spoList;
    }

}
