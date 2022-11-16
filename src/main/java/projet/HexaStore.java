package projet;


/***
 * classe representant la structure HexaStore
 *
 *
 * ***/
public class HexaStore {

    private final Index SPOIndex;
    private final Index PSOIndex;
    private final Index OSPIndex;
    private final Index SOPIndex;
    private final Index POSIndex;
    private final Index OPSIndex;

    public HexaStore() {
        this.SPOIndex = new Index();
        this.PSOIndex = new Index();
        this.OSPIndex = new Index();
        this.SOPIndex = new Index();
        this.POSIndex = new Index();
        this.OPSIndex = new Index();
    }

    public Index getSPOIndex() {
        return SPOIndex;
    }

    public Index getPSOIndex() {
        return PSOIndex;
    }

    public Index getOSPIndex() {
        return OSPIndex;
    }

    public Index getSOPIndex() {
        return SOPIndex;
    }

    public Index getPOSIndex() {
        return POSIndex;
    }

    public Index getOPSIndex() {
        return OPSIndex;
    }
}
