package org.sv4j;


import lombok.Getter;
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
    @Getter
    private NeuralNetwork neuralValidator;

    public MessageValidator(){
        neuralValidator = new NeuralNetwork();
    }

    /**
     * @param s Message to validate
     * @return validation results
     */
    public boolean isSpam(String s) throws MessageReadingException{
        return neuralValidator.testSubject(s).isSpam();
    }

    /**
     * @param s Message to validate
     * @return validation results
     * */
    public NeuralNetOutput getOutputFor(String s) throws MessageReadingException {
        return neuralValidator.testSubject(s);
    }
}