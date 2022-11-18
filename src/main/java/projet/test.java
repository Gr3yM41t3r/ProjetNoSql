package projet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class test {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(1);
        list.add(5);

        list2.add(1);
        list2.add(2);
        list2.add(3);
        list2.add(1);
        list2.add(7);
        list.retainAll(list2);
        System.out.println(list);
        Set<Integer> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        System.out.println(list);


    }
}
