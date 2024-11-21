package csd.cheaterbug.Entity;

import java.util.TreeMap;
import java.util.Map;
import java.util.Set;

public class ProbabilityObservationMap {

    private static final Integer KEY_SIGNIFICANT_FIGURES = 5;
    private Map<Double, Integer> map;

    public ProbabilityObservationMap() {
        map = new TreeMap<>();
    }

    /**
     * Add a new entry to the map
     * 
     * @param key Double
     * @param value Integer
     */ 
    public void addEntry(Double key, Integer value) {
        Double roundedKey = roundToSignificantFigure(key, KEY_SIGNIFICANT_FIGURES);

        if (map.containsKey(roundedKey)) {
            map.put(roundedKey, map.get(roundedKey) + 1);
        } else {
            map.put(roundedKey, 1);
        }
    }

    /**
     * Get the entry set of the probability observation map
     * 
     * @return Set of map entries of probability : observations
     */
    public Set<Map.Entry<Double, Integer>> getEntries() {
        return map.entrySet();
    }

    /**
     * Round a number to a certain number of significant figures
     * 
     * @param number Double
     * @param significantFigures Integer
     * @return Double rounded to specified sf
     */
    private Double roundToSignificantFigure(Double number, Integer significantFigures) {
        // prevent log 0
        if (number == 0) {
            return 0.0;
        }

        Double num_digits = Math.ceil(Math.log10(number));
        Integer power = significantFigures - num_digits.intValue();

        Double magnitude = Math.pow(10, power);
        Long shifted = Math.round(number * magnitude);
        return shifted / magnitude;
    }

    /**
     * Get the probability of the nth percentile observation
     * 
     * @param nPercentile Double
     * @return Double
     */
    public String getNthPercentileProbability(Double nPercentile) {
        Integer totalEntries = map.values().stream().mapToInt(Integer::intValue).sum();
        Double nthEntry = totalEntries * (nPercentile);

        if (nthEntry < 1 || nthEntry > totalEntries-1 ) {
            return "Not enough data to calculate percentile probability";  
        }

        return Double.toString(
            getIndexProbability((int) Math.floor(nthEntry)) + 
            getIndexProbability((int) Math.ceil(nthEntry))
            / 2.0);
    }

    /**
     * Get the probability stored at the index
     * 
     * @param index Integer
     * @return Double
     */
    private Double getIndexProbability(Integer index) {
        int current_index = 0;
        for (Double probability : map.keySet()) {
            current_index += map.get(probability);
            if (current_index >= index) {
                return probability;
            }
        }
        return 0.0;
    }
}
