package projet;

import java.util.HashMap;
import java.util.Map;

public class Evaluation {
    private long startTime;

    private Map<String,String> data ;

    public Evaluation() {
        this.data = new HashMap<>();
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> time) {
        this.data = time;
    }

    public void addBenchmarkData(String key, String value){
        this.data.put(key,String.valueOf(value));
    }

    public void startTimer(){
        startTime = System.currentTimeMillis();

    }
    public String endTimer(){
        return String.valueOf(System.currentTimeMillis()-startTime);

    }

}
