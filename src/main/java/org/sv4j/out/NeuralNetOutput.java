package org.sv4j.out;

import org.slf4j.LoggerFactory;


public final class NeuralNetOutput {

    private static org.slf4j.Logger Log = LoggerFactory.getLogger(NeuralNetOutput.class);

    /**
     * Spam score
     */
    private double _SpamScore;

    /**
     * Non spam score
     */
    private double _PosScore;


    /**
     * @param spamScore spam label output
     * @param posScore positive label score
     */
    public NeuralNetOutput(double spamScore, double posScore) {
        _SpamScore = spamScore;
        _PosScore = posScore;
    }

    /**
     * @return result of classification
     */
    public boolean isSpam(){
        return Double.compare(_SpamScore,_PosScore) < 0;
    }

    /**
     * @return spam score
     */
    public double getSpamScore() {
        return _SpamScore;
    }

    /**
     * @return non spam score
     */
    public double getPosScore() {
        return _PosScore;
    }

    /**
     * printing Neural network output
     */
    public void printValues(){
        Log.info("\nPositive:"+_PosScore+"\nSpam:"+_SpamScore+"\nisSpam():"+this.isSpam());
    }

    /**
     * @return Neural network output serialized to json
     */
    public String toJson(){
        return "{\nPositive:"+_PosScore+",\nSpam:"+_SpamScore+"\n}";
    }
}