package csd.cheaterbug;

import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class CheaterbugService {

    private static final Logger logger = LoggerFactory.getLogger(CheaterbugService.class);

    /**
     * Given a list of results, this method calculates the probability of the player achieving this result
     * @param results List of Result objects
     * @return Response object containing the probabilities of expected and cheat
     */
    public Response getAnalysis(List<Result> results) {
        String expectedType = "expected";
        ProbabilityObservationMap expectedProbabilityObservations = calculateProbObsMap(results, expectedType);
        logger.info("Expected Probability Observations: " + expectedProbabilityObservations.toString());

        String cheatType = "cheat";
        ProbabilityObservationMap cheatProbabilityObservations = calculateProbObsMap(results, cheatType);
        logger.info("Cheat Probability Observations: " + cheatProbabilityObservations.toString());
        
        return new Response(
            new TreeMap<>() {{
                put("actual", Double.toString(getActualProbability(results, expectedType)));
                put("1stPercentile", expectedProbabilityObservations.getNthPercentileProbability(0.01));
                put("5thPercentile", expectedProbabilityObservations.getNthPercentileProbability(0.05));
            }},
            new TreeMap<>() {{
                put("actual", Double.toString(getActualProbability(results, cheatType)));
                put("80thPercentile", cheatProbabilityObservations.getNthPercentileProbability(0.80));
                put("90thPercentile", cheatProbabilityObservations.getNthPercentileProbability(0.90));
                put("95thPercentile", cheatProbabilityObservations.getNthPercentileProbability(0.95));
                put("99thPercentile", cheatProbabilityObservations.getNthPercentileProbability(0.99));
            }});       
    }

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

    private Double getActualProbability(List<Result> results, String type) {
        Double probability = 1.0;
        for (Result result : results) {
            if (result.getActualScore() == 0.0) {
                probability *= result.getPLoseType(type);
            } else if (result.getActualScore() == 1.0) {
                probability *= result.getPWinType(type);
            } else {
                probability *= result.getPDrawType(type);
            }
        }
        return probability;
    }

    private ProbabilityObservationMap addResultsToProbObsMap(ProbabilityObservationMap probabilityObservations, Double probability, Result firstResults, Integer count, String type) {
        probabilityObservations.addEntry(probability * firstResults.getPLoseType(type), count);
        probabilityObservations.addEntry(probability * firstResults.getPDrawType(type), count);
        probabilityObservations.addEntry(probability * firstResults.getPWinType(type), count);
        return probabilityObservations;
    }

    private ProbabilityObservationMap updateProbObsMap(ProbabilityObservationMap probabilityObservations, Result result, String type) {
        ProbabilityObservationMap newProbObsMap = new ProbabilityObservationMap();

        for(Map.Entry<Double, Integer> observation : probabilityObservations.getEntries()) {
            addResultsToProbObsMap(newProbObsMap, observation.getKey(), result, observation.getValue(), type);
        }

        return newProbObsMap;
    }
}
