package projet;

import java.util.*;

public class Index {

    /**
     * une map de map de liste
     */
    private final Map<Integer, Map<Integer, List<Integer>>> hexastore;

    public Index() {
        this.hexastore = new HashMap<>();
    }

    public Map<Integer, Map<Integer, List<Integer>>> getHexastore() {
        return hexastore;
    }

    public void add(int i1, int i2, int i3) {
        if (hexastore.containsKey(i1)) {
            if (hexastore.get(i1).containsKey(i2)) {
                if (!hexastore.get(i1).get(i2).contains(i3)) {
                    hexastore.get(i1).get(i2).add(i3);
                }
            } else {
                hexastore.get(i1).put(i2, new ArrayList<Integer>() {{
                    add(i3);
                }});
            }
        } else {
            hexastore.put(i1, new HashMap<Integer, List<Integer>>() {{
                put(i2, new ArrayList<Integer>() {{
                    add(i3);
                }});
            }});
        }
    }

    public String print() {
        StringBuilder str = new StringBuilder();
        SortedSet<Integer> keys = new TreeSet<>(hexastore.keySet());
        for (Integer hex : keys) {
            String key = hex.toString();
            Map<Integer, List<Integer>> value = hexastore.get(hex);
            for (Integer hex2 : value.keySet()) {
                String key2 = hex2.toString();
                List<Integer> list = value.get(hex2);
                for (Integer in : list) {
                    str.append("<").append(key).append(",").append(key2).append(",").append(in).append(">").append("\n");
                }
            }
        }
        return str.toString();

    }


}
