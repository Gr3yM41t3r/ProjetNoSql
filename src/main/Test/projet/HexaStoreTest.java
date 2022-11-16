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
        System.out.println(hexaStore.print());
        hexaStore.add(1,2,3);
        assertEquals("(1,2,3)\n",hexaStore.print());
    }

    @Test
    void test() throws IOException {
        String stm ="<http://db.uwaterloo.ca/~galuc/wsdbm/User0>\t<http://schema.org/birthDate>\t\"1988-09-24\" .\n"+
                "<http://db.uwaterloo.ca/~galuc/wsdbm/User0>\t<http://schema.org/birthDatde>\t\"1988-09-22\" .\n"
                ;

        DictionnayParser dictionnayParser =  new DictionnayParser();
        InputStream stream = new ByteArrayInputStream(stm.getBytes(StandardCharsets.UTF_8));
        List<Statement> list = dictionnayParser.getRdfHandler().parseOneLine(stream);
        System.out.println(list);

        dictionnayParser.createDictionnay();
        dictionnayParser.createIndexes(list);
        System.out.println(dictionnayParser.getIndex().getSPOIndex().print());
        assertEquals("(1,2,3)\n(1,4,5)\n",dictionnayParser.getIndex().getSPOIndex().print());
        assertEquals("(1,2,3)\n(1,4,5)\n",dictionnayParser.getIndex().getSPOIndex().print());
        assertEquals("(1,2,3)\n(1,4,5)\n",dictionnayParser.getIndex().getSPOIndex().print());
        assertEquals("(1,2,3)\n(1,4,5)\n",dictionnayParser.getIndex().getSPOIndex().print());
        assertEquals("(1,2,3)\n(1,4,5)\n",dictionnayParser.getIndex().getSPOIndex().print());
        assertEquals("(1,2,3)\n(1,4,5)\n",dictionnayParser.getIndex().getSPOIndex().print());
        assertEquals("(1,2,3)\n(1,4,5)\n",dictionnayParser.getIndex().getSPOIndex().print());
        assertEquals("(1,2,3)\n(1,4,5)\n",dictionnayParser.getIndex().getSPOIndex().print());



    }
}