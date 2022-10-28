package projet;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        DictionnayParser dictionnayParser = new DictionnayParser();
        dictionnayParser.createDictionnay();
        dictionnayParser.printDictionnary();
        dictionnayParser.writeCsv("data/out/test.csv");
        dictionnayParser.hexastore("data/out/");
    }
}
