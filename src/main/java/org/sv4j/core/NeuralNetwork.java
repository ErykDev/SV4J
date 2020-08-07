package org.sv4j.core;

import lombok.extern.slf4j.Slf4j;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.FileLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.sv4j.exc.MessageReadingException;
import org.sv4j.out.NeuralNetOutput;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Class for loading and initializing Neural network and wordVectors
 */
@Slf4j
public final class NeuralNetwork {

    ComputationGraph multiLayerNetwork;
    WordVectors wordVec;

    private int vectorSize = 300;               //Size of the word vectors. 300 in the Google News model
    int batchSize = 32;
    int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this

    Random rng = new Random(12345); //For shuffling repeatability


    private int cnnLayerFeatureMaps = 100;      //Number of feature maps / channels / depth for each CNN layer
    private PoolingType globalPoolingType = PoolingType.MAX;

    public NeuralNetwork(){
        this.init();
    }

    private void init(){
        log.debug("Initializing NeuralNetwork");
        try {
            multiLayerNetwork = this.loadNetwork();
        }catch (IOException e){
            log.error("Err while loading network. Building new one");

            multiLayerNetwork = this.buildNetworkModel();
        }
            multiLayerNetwork.init();

        try {
            this.wordVec = this.getWordVectors();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param s message to validate
     * @return validation result
     * @throws MessageReadingException unable to read message
     */
    public NeuralNetOutput testSubject(String s) throws MessageReadingException {

        DataSetIterator testIter = NeuralNetwork.getDataSetIterator(wordVec, batchSize, truncateReviewsToLength, rng);

        try {
            INDArray featuresFirstNegative = ((CnnSentenceDataSetIterator)testIter).loadSingleSentence(s);

            INDArray predictions = multiLayerNetwork.outputSingle(featuresFirstNegative);

            return new NeuralNetOutput(predictions.getDouble(0),predictions.getDouble(1));
        }catch (org.nd4j.linalg.exception.ND4JIllegalStateException e){
            throw new MessageReadingException("Can not process:"+s,e);
        }
    }

    private ComputationGraph buildNetworkModel(){
        log.debug("BUILDING MODEL");

        Nd4j.getMemoryManager().setAutoGcWindow(5000);

        return new ComputationGraph(new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.RELU)
                .activation(Activation.LEAKYRELU)
                .updater(new Adam(0.01))
                .convolutionMode(ConvolutionMode.Same)
                .l2(0.0001)
                .graphBuilder()
                .addInputs("input")
                .addLayer("cnn3", new ConvolutionLayer.Builder()
                        .kernelSize(3,vectorSize)
                        .stride(1,vectorSize)
                        .nIn(1)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                .addLayer("cnn4", new ConvolutionLayer.Builder()
                        .kernelSize(4,vectorSize)
                        .stride(1,vectorSize)
                        .nIn(1)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                .addLayer("cnn5", new ConvolutionLayer.Builder()
                        .kernelSize(5,vectorSize)
                        .stride(1,vectorSize)
                        .nIn(1)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                .addVertex("merge", new MergeVertex(), "cnn3", "cnn4", "cnn5")
                .addLayer("globalPool", new GlobalPoolingLayer.Builder()
                        .poolingType(globalPoolingType)
                        .dropOut(0.5)
                        .build(), "merge")
                .addLayer("out", new OutputLayer.Builder()
                        .lossFunction(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX)
                        .nIn(3*cnnLayerFeatureMaps)
                        .nOut(2)    //2 classes: positive or negative
                        .build(), "globalPool")
                .setOutputs("out")
                .build());
    }

    /**
     * @return loaded NeuralNetwork
     * @throws IOException while reading NeuralNetwork model
     */
    private ComputationGraph loadNetwork() throws IOException {
        log.debug("LOADING MODEL");
        return ComputationGraph.load(new ClassPathResource("PreTrainedNet.zip").getFile(),true);
    }

    public static void saveMultiLayerNetwork(NeuralNetwork net) throws IOException {
        log.debug("SAVING MODEL");
        ModelSerializer.writeModel(net.multiLayerNetwork, new ClassPathResource("PreTrainedNet.zip").getFile(), false);
    }

    /**
     * @return loaded words vectors
     * @throws IOException while reading vectors model
     */
    private WordVectors getWordVectors() throws IOException {
        log.debug("LOADING Words2Vec MODEL");
        return WordVectorSerializer
                .loadStaticModel(new ClassPathResource("WordVectors.txt").getFile());
    }

    private static DataSetIterator getDataSetIterator(WordVectors wordVectors, int minibatchSize,
                                                      int maxSentenceLength, Random rng ){

        Map<String,List<File>> reviewFilesMap = new HashMap<>();

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