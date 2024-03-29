package neuro.analytics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neuroph.imgrec.ColorMode;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.ImageRecognitionHelper;
import org.neuroph.imgrec.image.Dimension;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import neuro.analytics.easyneurons.imgrec.ImagesLoader;

public class BrainFactory {

	/**
	 * Train a new network with given image files
	 *
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Brain createFromTrainSet(String path, boolean verbose, Integer hidden, double learnrate)
			throws Exception {
		// The trainings data
		System.out.println("Going to train with neurons:"+hidden.toString()+
				" on path:"+path);
		DataSet trainSet; // could be TrainingSet<TrainingElement>
		List<String> labels = new ArrayList<String>();
		Map<String, FractionRgbData> images;

		// The network
		NeuralNetwork net;
		// The network properties
		String netLabel = "NeuroNumber";
		Dimension dimension = new Dimension(30, 30);
		ColorMode colorMode = ColorMode.BLACK_AND_WHITE;
		List<Integer> hiddenLayers = new ArrayList<Integer>();
		// this is the parametrizable value
		hiddenLayers.add(hidden);

		// TODO load files from path
		File dir = new File(path);
		if (!dir.isDirectory()) {
			throw new Exception("Please specify a directory");
		}

		String label;
		File[] content = dir.listFiles();
		images = new HashMap<String, FractionRgbData>();

		if (verbose) {
			System.out.println("processing dir " + dir.getName());
		}

		for (File directory : content) {
			if (verbose) {
				System.out.println("processing dir " + directory.getName());
			}

			if (!directory.isDirectory()) {
				throw new Exception("The directory has to contain directories");
			}

			images.putAll(ImagesLoader.getFractionRgbDataForDirectory(
					directory, dimension));

			label = directory.getName();
			labels.add(label);
		}

		trainSet = ImageRecognitionHelper.createBlackAndWhiteTrainingSet(
				labels, images);

		net = ImageRecognitionHelper.createNewNeuralNetwork(netLabel,
				dimension, colorMode, labels, hiddenLayers,
				TransferFunctionType.SIGMOID);

		// specify learningrull
		MomentumBackpropagation backpropagation = new MomentumBackpropagation();
		// this is parametrizable
		backpropagation.setLearningRate(learnrate);
		backpropagation.setMaxError(0.01);
		backpropagation.setMomentum(0.0);

		net.learn(trainSet, backpropagation);
		// learnInNewThread();

		return new Brain(net);
	}

	/**
	 * Load a ready traines network from a file
	 *
	 * @param loadPath
	 * @return
	 */
	public Brain createFromFile(String loadPath, boolean verbose) {
		NeuralNetwork net = NeuralNetwork.load(loadPath);
		return new Brain(net);
	}
}
