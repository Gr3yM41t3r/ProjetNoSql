package projet;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        DictionnayParser dictionnayParser = new DictionnayParser();
        dictionnayParser.createDictionnay();
        dictionnayParser.printDictionnary();

        dictionnayParser.hexastore("data/out/");
    }
}
