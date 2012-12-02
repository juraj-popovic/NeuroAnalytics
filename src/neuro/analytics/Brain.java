package neuro.analytics;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.neuroph.imgrec.ImageRecognitionPlugin;
import org.neuroph.imgrec.ImageSizeMismatchException;
import org.neuroph.core.NeuralNetwork;


public class Brain {
	public static final String NL = System.getProperty("line.separator");

	private NeuralNetwork network;

	public Brain(NeuralNetwork net) {
		this.network = net;
	}

	public void train(boolean verbose) {

		// This method might to something, if we want to do incremental training
		// learn the training set
		// this.network.learn(trainingSet);
	}

	// gives an answer in form of HashMap, which number has which probability,
	// when asked on certail number file
	public HashMap<String, Double> ask(String filePath, boolean verbose)
			throws ImageSizeMismatchException, IOException {
		if(verbose) System.out.println("asking on file:"+filePath);
		ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin) network
				.getPlugin(ImageRecognitionPlugin.class);

		HashMap<String, Double> output = imageRecognition
				.recognizeImage(new File(filePath));

		if (verbose) {
			System.out.println("The network returned: " + output.toString());
		}

		return output;

	}

	// not used currently
	public void sleepAt(String storePath, boolean verbose) {
		network.save(storePath);
	}

}
