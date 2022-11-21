package projet;

import org.eclipse.rdf4j.model.Statement;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HexaStoreTest {
    Index hexaStore = new Index();
    HexaStore index = new HexaStore();

    @Test
    void getHexastore() {

    }

    @Test
    void add() {
        hexaStore.add(1,2,3);
    }

    @Test
    void print() {

    }

    @Test
    void test() throws IOException {
        //deux sample data
        String stm ="<http://db.uwaterloo.ca/~galuc/wsdbm/User0>\t<http://schema.org/birthDate>\t\"1988-09-24\" .\n"+
                "<http://db.uwaterloo.ca/~galuc/wsdbm/User0>\t<http://schema.org/birthDatde>\t\"1988-09-22\" .\n";

        /*DictionnayParser dictionnayParser =  new DictionnayParser();
        InputStream stream = new ByteArrayInputStream(stm.getBytes(StandardCharsets.UTF_8));
        //parser le sample data
        List<Statement> list = dictionnayParser.getRdfHandler().parseOneLine(stream);

        dictionnayParser.setStatementsList(list);

        //creer le dictionnaire
        dictionnayParser.createDictionnay();
        //creer les indexes

        dictionnayParser.createIndexes(list);
        assertEquals("<1,2,3>\n<1,4,5>\n",dictionnayParser.getIndex().getSPOIndex().print());
        assertEquals("<1,3,2>\n<1,5,4>\n",dictionnayParser.getIndex().getSOPIndex().print());
        assertEquals("<3,2,1>\n<5,4,1>\n",dictionnayParser.getIndex().getOPSIndex().print());
        assertEquals("<3,1,2>\n<5,1,4>\n",dictionnayParser.getIndex().getOSPIndex().print());
        assertEquals("<2,3,1>\n<4,5,1>\n",dictionnayParser.getIndex().getPOSIndex().print());
        assertEquals("<2,1,3>\n<4,1,5>\n",dictionnayParser.getIndex().getPSOIndex().print());*/



    }
}