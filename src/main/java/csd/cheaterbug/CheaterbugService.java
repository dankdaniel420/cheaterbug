package csd.cheaterbug;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CheaterbugService {
    /**
     * Given a list of results, this method calculates the probability of the player achieving this result
     * @param results List of Result objects
     * @return Response object containing the probabilities of expected and cheat
     */
    public Response getAnalysis(List<Result> results) {
        Double expected = 1.0;
        Double cheat = 1.0;

        for (Result result : results) {
            Double expectedProbability = getExpectedProbability(result, result.getActualScore());
            Double cheatProbability = getCheatProbability(result, result.getActualScore());

            expected *= expectedProbability;
            cheat *= cheatProbability;
        }

        return new Response(expected, cheat);
    }

    /**
     * Get the expected probability of the player achieving the actual score
     * @param result Result object
     * @param actualScore Double of actual score
     * @return the expected probability of outcome
     */
    private Double getExpectedProbability(Result result, Double actualScore) {
        if (actualScore == 0.0) {
            return result.getPLoseExpected();
        } else if (actualScore == 1.0) {
            return result.getPWinExpected();
        } else {
            return result.getPDrawExpected();
        }
    }

    /**
     * Get the cheat probability of the player achieving the actual score
     * @param result Result object
     * @param actualScore Double of actual score
     * @return the cheat probability of outcome
     */
    private Double getCheatProbability(Result result, Double actualScore) {
        if (actualScore == 0.0) {
            return result.getPLoseCheat();
        } else if (actualScore == 1.0) {
            return result.getPWinCheat();
        } else {
            return result.getPDrawCheat();
        }
    }
}
