package csd.cheaterbug;

import lombok.Getter;

@Getter
public class Result {
    private static final Double BASE_K = 1.7;

    // Based on entered results via API
    private Double expectedScore;
    private Double actualScore;
    private Double cheatScore;

    // Based on expected score
    private Double pWinExpected;
    private Double pDrawExpected;
    private Double pLoseExpected;

    // Based on cheat score
    private Double pWinCheat;
    private Double pDrawCheat;
    private Double pLoseCheat;

    public Result(Double expectedScore, Double actualScore) {
        this.expectedScore = expectedScore;
        this.actualScore = actualScore;
        calculateCheatScore();
        predictOutcomeProbabilities(expectedScore);
        predictOutcomeProbabilities(cheatScore);
    }

    /**
     * Set the cheat score based on this formula:
     * cheatScore = 1 - (1 - expectedScore)^k
     * where k = baseK + alpha * |0.5 - expectedScore|
     * This allows k to be dyanmic and have a higher impact for greater differences in expected score
     * 
     */
    private void calculateCheatScore() {
        this.cheatScore = 1 - Math.pow(1 - this.expectedScore, BASE_K + Math.abs(1.0 - this.expectedScore));
    }

    /**
     * Set the outcome probabilities based on the predicted score
     * 
     * @param predictedScore
     */
    private void predictOutcomeProbabilities(final Double predictedScore) {
        Double winWeight = calculateWeightOfScore(1.0, predictedScore);
        Double drawWeight = calculateWeightOfScore(0.5, predictedScore);;
        Double loseWeight = calculateWeightOfScore(0.0, predictedScore);;
        Double totalWeight = winWeight + drawWeight + loseWeight;

        if (predictedScore.equals(expectedScore)) {
            this.pWinExpected = winWeight / totalWeight;
            this.pDrawExpected = drawWeight / totalWeight;
            this.pLoseExpected = loseWeight / totalWeight;
            return;
        } else {
            this.pWinCheat = winWeight / totalWeight;
            this.pDrawCheat = drawWeight / totalWeight;
            this.pLoseCheat = loseWeight / totalWeight;
            return;
        }
    }

    /**
     * Calculate the weight of the score based on the distance from the predicted score
     * 
     * @param score Outcome score 1.0 for win, 0.5 for draw, 0.0 for lose
     * @param predictedScore The predicted score
     * @return The weight of the score based on the distance from the predicted score
     */
    private Double calculateWeightOfScore(final Double score, final Double predictedScore) {
        Double distance = Math.abs(score - predictedScore);
        return 1 / distance + 0.01;
    }

    public Double getPLoseType(String type) {
        if (type.equals("expected")) {
            return pLoseExpected;
        } else {
            return pLoseCheat;
        }
    }

    public Double getPDrawType(String type) {
        if (type.equals("expected")) {
            return pDrawExpected;
        } else {
            return pDrawCheat;
        }
    }

    public Double getPWinType(String type) {
        if (type.equals("expected")) {
            return pWinExpected;
        } else {
            return pWinCheat;
        }
    }

    @Override
    public String toString() {
        return "Result [expectedScore=" + expectedScore + ", actualScore=" + actualScore + ", cheatScore=" + cheatScore
                + ", pWinExpected=" + pWinExpected + ", pDrawExpected=" + pDrawExpected + ", pLoseExpected="
                + pLoseExpected + ", pWinCheat=" + pWinCheat + ", pDrawCheat=" + pDrawCheat + ", pLoseCheat="
                + pLoseCheat + "]";
    }
}
