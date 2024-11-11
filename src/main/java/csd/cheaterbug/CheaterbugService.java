package csd.cheaterbug;

import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import csd.cheaterbug.Entity.ProbabilityObservationMap;
import csd.cheaterbug.Entity.Response;
import csd.cheaterbug.Entity.Result;

@Service
public class CheaterbugService {

    private static final Logger logger = LoggerFactory.getLogger(CheaterbugService.class);

    /**
     * Analyzes a list of results and calculates probabilities for expected and cheat scenarios.
     * @param results List of Result objects
     * @return Response object with calculated probabilities
     */
    public Response getAnalysis(List<Result> results) {
        ProbabilityObservationMap expectedObservations = calculateProbObsMap(results, "expected");
        logger.info("Expected Probability Observations: {}", expectedObservations);

        ProbabilityObservationMap cheatObservations = calculateProbObsMap(results, "cheat");
        logger.info("Cheat Probability Observations: {}", cheatObservations);

        return new Response(
            createProbabilityMap(results, expectedObservations, "expected", 0.01, 0.05),
            createProbabilityMap(results, cheatObservations, "cheat", 0.80, 0.90, 0.95, 0.99)
        );
    }

    private ProbabilityObservationMap calculateProbObsMap(List<Result> results, String type) {
        ProbabilityObservationMap probabilityObservations = new ProbabilityObservationMap();
        double initialProbability = 1.0;
        int initialCount = 1;

        Result firstResult = results.get(0);
        probabilityObservations = addEntries(probabilityObservations, initialProbability, firstResult, initialCount, type);

        for (Result result : results.subList(1, results.size())) {
            probabilityObservations = updateProbObsMap(probabilityObservations, result, type);
        }
        return probabilityObservations;
    }

    private Map<String, String> createProbabilityMap(List<Result> results, ProbabilityObservationMap observations, String type, double... percentiles) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("actual", Double.toString(getActualProbability(results, type)));
        for (double percentile : percentiles) {
            map.put((int)(percentile * 100) + "thPercentile", observations.getNthPercentileProbability(percentile));
        }
        return map;
    }

    private double getActualProbability(List<Result> results, String type) {
        return results.stream()
                .mapToDouble(result -> result.getActualScore() == 0.0 ? result.getPLoseType(type) :
                        result.getActualScore() == 1.0 ? result.getPWinType(type) : result.getPDrawType(type))
                .reduce(1.0, (a, b) -> a * b);
    }

    private ProbabilityObservationMap addEntries(ProbabilityObservationMap probObsMap, double probability, Result result, int count, String type) {
        probObsMap.addEntry(probability * result.getPLoseType(type), count);
        probObsMap.addEntry(probability * result.getPDrawType(type), count);
        probObsMap.addEntry(probability * result.getPWinType(type), count);
        return probObsMap;
    }

    private ProbabilityObservationMap updateProbObsMap(ProbabilityObservationMap probObsMap, Result result, String type) {
        ProbabilityObservationMap newProbObsMap = new ProbabilityObservationMap();
        probObsMap.getEntries().forEach(observation ->
                addEntries(newProbObsMap, observation.getKey(), result, observation.getValue(), type)
        );
        return newProbObsMap;
    }
}
