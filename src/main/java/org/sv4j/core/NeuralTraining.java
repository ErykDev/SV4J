package org.sv4j.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.FileLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;
import java.util.*;

/**
 * Class meant for training existing NeuralNetwork
 */
@Slf4j
public class NeuralTraining {

    private ComputationGraph _MultiLayerNetwork;
    private ScoreIterationListener _ScoreIterationListener = new ScoreIterationListener(10);
    private WordVectors _WordVec;
    private String messagesPath;

    private int batchSize = 32;
    private int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this

    private Random rng = new Random(12345); //For shuffling repeatability

    /**
     * @param multiLayerNetwork prebuilt neural network
     * @param word2Vec word vectors
     */
    public NeuralTraining(ComputationGraph multiLayerNetwork, WordVectors word2Vec,String messagesPath){
        this._MultiLayerNetwork = multiLayerNetwork;
        this._WordVec = word2Vec;

        _MultiLayerNetwork.setListeners(_ScoreIterationListener);

        this.messagesPath = messagesPath;
    }

    public NeuralTraining(NeuralNetwork neuralNetwork, String messagesPath){
        this._MultiLayerNetwork = neuralNetwork.multiLayerNetwork;
        this._WordVec = neuralNetwork.wordVec;

        _MultiLayerNetwork.setListeners(_ScoreIterationListener);

        this.messagesPath = messagesPath;
    }

    /**
     * Training NeuralNetwork
     * @param epochs number of epochs
     */
    public void training(int epochs) {
        log.info("Training Neural Network");

        DataSetIterator trainIter = getDataSetIterator(true,_WordVec , batchSize, truncateReviewsToLength, rng);
        DataSetIterator testIter = getDataSetIterator(false, _WordVec, batchSize, truncateReviewsToLength, rng);

        for (int i = 0; i < epochs; i++) {
            _MultiLayerNetwork.fit(trainIter);
        }

        System.out.println("Starting Evaluation:");

        Evaluation evaluation = _MultiLayerNetwork.evaluate(testIter);
        System.out.println(evaluation.stats());
    }

    private DataSetIterator getDataSetIterator(boolean isTraining, WordVectors wordVectors, int minibatchSize,
                                                      int maxSentenceLength, Random rng){

        String path = FilenameUtils.concat(messagesPath, (isTraining ? "messages/train/" : "messages/test/"));
        String positiveBaseDir = FilenameUtils.concat(path, "pos");
        String negativeBaseDir = FilenameUtils.concat(path, "neg");

        File filePositive = new File(positiveBaseDir);
        File fileNegative = new File(negativeBaseDir);

        Map<String,List<File>> reviewFilesMap = new HashMap<>();
        reviewFilesMap.put("Positive", Arrays.asList(filePositive.listFiles()));
        reviewFilesMap.put("Spam", Arrays.asList(fileNegative.listFiles()));

        LabeledSentenceProvider sentenceProvider = new FileLabeledSentenceProvider(reviewFilesMap, rng);

        return new CnnSentenceDataSetIterator.Builder()
                .sentenceProvider(sentenceProvider)
                .wordVectors(wordVectors)
                .minibatchSize(minibatchSize)
                .maxSentenceLength(maxSentenceLength)
                .useNormalizedWordVectors(false)
                .build();
    }
}
