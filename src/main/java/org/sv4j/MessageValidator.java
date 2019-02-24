package org.sv4j;

import org.sv4j.core.NeuralNetwork;
import org.sv4j.exc.MessageReadingException;
import org.sv4j.out.NeuralNetOutput;

/**
 * Main class for message validation
 */
public final class MessageValidator {

    /**
     * Variable for initializing NeuralNetwork
     */
    private NeuralNetwork nn;

    public MessageValidator(){
        nn = new NeuralNetwork();
    }


    /**
     * @param s Message to validate
     * @return validation results
     */
    public boolean isSpam(String s){
        try {
            return nn.testSubject(s).isSpam();
        } catch (MessageReadingException e) {
            return true;
        }
    }

    /**
     * @param s Message to validate
     * @return validation results
     * */
    public NeuralNetOutput getOutputFor(String s) throws MessageReadingException {
        return nn.testSubject(s);
    }
}