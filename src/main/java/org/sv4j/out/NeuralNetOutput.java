package org.sv4j.out;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class NeuralNetOutput {
    /**
     * Spam score
     */
    @Getter
    private double spamScore;

    /**
     * Non spam score
     */
    @Getter
    private double posScore;


    /**
     * @param spamScore spam label output
     * @param posScore positive label score
     */
    public NeuralNetOutput(double spamScore, double posScore) {
        this.spamScore = spamScore;
        this.posScore = posScore;
    }

    /**
     * @return result of classification
     */
    public boolean isSpam(){
        return Double.compare(spamScore, posScore) < 0;
    }

    /**
     * printing Neural network output
     */
    public void printValues(){
        log.info("\nPositive:"+ posScore +"\nSpam:"+ spamScore +"\nisSpam():"+this.isSpam());
    }

    /**
     * @return Neural network output serialized to json
     */
    public String toJson(){
        return "{\nPositive:"+ posScore +",\nSpam:"+ spamScore +"\n}";
    }
}