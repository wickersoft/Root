/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 *
 * @author wicden
 */
public class LoadAverage implements Runnable {
    
    private static final LinkedList<Long> LOAD_TWO_SEC = new LinkedList<>();
    private static final LoadAverage instance = new LoadAverage();
    
    public static LoadAverage instance() {
        return instance;
    }
    
    public void run() {
        long time = System.nanoTime();
        LOAD_TWO_SEC.addLast(time);
        while(LOAD_TWO_SEC.getFirst() < time - 2000000000) {
            LOAD_TWO_SEC.remove();
        }
    }
    
    public static double getTps() {
        return LOAD_TWO_SEC.size() / 2.0;
    }
    
    public static long getMsAvg() {
        long time = (LOAD_TWO_SEC.getLast() - LOAD_TWO_SEC.getFirst()) / LOAD_TWO_SEC.size() / 1000000;
        return time;
    }
    
    public static long getMsPeak() {
        long time = 0;
        long last = LOAD_TWO_SEC.getFirst();
        for(Long step : LOAD_TWO_SEC) {
            time = Math.max(step - last, time);
            last = step;
        }
        return time / 1000000;
    }
    
    
}
