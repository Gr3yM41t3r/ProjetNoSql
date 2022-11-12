package projet;

import java.util.List;
import java.util.Map;

public class Index {

    private HexaStore SPOList ;
    private HexaStore PSOList ;
    private HexaStore OSPList ;
    private HexaStore SOPList ;
    private HexaStore POSList ;
    private HexaStore OPSList ;

    public Index() {
        this.SPOList = new HexaStore();
    }

    public HexaStore getSPOList() {
        return SPOList;
    }
}
