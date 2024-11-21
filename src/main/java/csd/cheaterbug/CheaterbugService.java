package csd.cheaterbug;

import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Service;

import csd.cheaterbug.Entity.ProbabilityObservationMap;
import csd.cheaterbug.Entity.Response;
import csd.cheaterbug.Entity.Result;

@Service
public class CheaterbugService {
    /**
     * Given a list of results, this method calculates the probability of the player achieving this result
     * @param results List of Result objects
     * @return Response object containing the probabilities of expected and cheat
     */
    public Response getAnalysis(List<Result> results) {
        final String EXPECTED_TYPE = "expected";
        ProbabilityObservationMap expectedProbabilityObservations = calculateProbObsMap(results, EXPECTED_TYPE);

        final String CHEAT_TYPE = "cheat";
        ProbabilityObservationMap cheatProbabilityObservations = calculateProbObsMap(results, CHEAT_TYPE);

        
        return new Response(
            new TreeMap<>() {{
                put("actual", Double.toString(getActualProbabilityByType(results, EXPECTED_TYPE)));
                put("5thPercentile", expectedProbabilityObservations.getNthPercentileProbability(0.05));
            }},
            new TreeMap<>() {{
                put("actual", Double.toString(getActualProbabilityByType(results, CHEAT_TYPE)));
                put("90thPercentile", cheatProbabilityObservations.getNthPercentileProbability(0.90));
            }});       
    }

    /**
     * Calculate the probability and observation map for a given list of results for expected/cheat type
     * 
     * @param results List of Result objects
     * @param type "expected" or "cheat"
     * @return ProbabilityObservationMap object containing the probabilities and observations
     */
    private ProbabilityObservationMap calculateProbObsMap(final List<Result> results, String type) {
        List<Result> resultsCopy = new ArrayList<>(results);

        // Use first Result object to initialise probabilityObservations map, and remove from results list
        Result firstResult = resultsCopy.get(0);
        resultsCopy.remove(0);

        Double initialProbability = 1.0;
        Integer initialCount = 1;
        ProbabilityObservationMap probabilityObservations = addResultsToProbObsMap(new ProbabilityObservationMap(), initialProbability, firstResult, initialCount, type);

        for (Result result : resultsCopy) {
            probabilityObservations = updateProbObsMap(probabilityObservations, result, type);
        }

        return probabilityObservations;
    }

    /**
     * Get the actual probability of the player achieving the results for expected/cheat type
     * 
     * @param results List of Result objects
     * @param type "expected" or "cheat"
     * @return Double
     */
    private Double getActualProbabilityByType(List<Result> results, String type) {
        Double probability = 1.0;
        for (Result result : results) {
            probability *= getProbabilityByType(result, type);
        }
        return probability;
    }

    /**
     * Get the probability of the player achieving the result for expected/cheat type
     * 
     * @param result Result object
     * @param type "expected" or "cheat"
     * @return Double
     */
    private Double getProbabilityByType(Result result, String type) {
        if (result.getActualScore() == 0.0) {
            return result.getPLoseType(type);
        } else if (result.getActualScore() == 1.0) {
            return result.getPWinType(type);
        } else {
            return result.getPDrawType(type);
        }
    }

    /**
     * Adds all probabilitiy of outcomes to the probabilityObservations map
     * 
     * @param probabilityObservations ProbabilityObservationMap object containing exisiting probabilities and observations
     * @param probability Double probability of the outcome
     * @param firstResults Result object
     * @param count Integer count of observations for this probability thus far
     * @param type "expected" or "cheat"
     * @return ProbabilityObservationMap object containing updated probabilities and observations
     */
    private ProbabilityObservationMap addResultsToProbObsMap(ProbabilityObservationMap probabilityObservations, Double probability, Result firstResults, Integer count, String type) {
        probabilityObservations.addEntry(probability * firstResults.getPLoseType(type), count);
        probabilityObservations.addEntry(probability * firstResults.getPDrawType(type), count);
        probabilityObservations.addEntry(probability * firstResults.getPWinType(type), count);
        return probabilityObservations;
    }

    /**
     * Updates the probabilityObservations map with the new result
     * 
     * @param probabilityObservations ProbabilityObservationMap object containing exisiting probabilities and observations
     * @param result Result object
     * @param type "expected" or "cheat"
     * @return ProbabilityObservationMap object containing updated probabilities and observations
     */
    private ProbabilityObservationMap updateProbObsMap(ProbabilityObservationMap probabilityObservations, Result result, String type) {
        ProbabilityObservationMap newProbObsMap = new ProbabilityObservationMap();

        for(Map.Entry<Double, Integer> observation : probabilityObservations.getEntries()) {
            addResultsToProbObsMap(newProbObsMap, observation.getKey(), result, observation.getValue(), type);
        }

        return newProbObsMap;
    }
}
