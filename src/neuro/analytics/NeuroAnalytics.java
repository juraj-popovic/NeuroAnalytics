package neuro.analytics;

import java.io.IOException;
import java.util.HashMap;

import org.neuroph.imgrec.ImageSizeMismatchException;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;

public class NeuroAnalytics {


	private String mode = null;
	private String storePath = null;
	private String loadPath = null;
	private String filePath = null;
	private boolean verbose = false;
	private Brain curBrain = null;

	/*
	public static void oldmain(String[] args) throws Exception {

		CmdLineParser cmd = new CmdLineParser();
		Option<String> modeOption = cmd.addStringOption('m', "mode");
		Option<String> loadPathOption = cmd.addStringOption('l', "loadPath");
		Option<String> storePathOption = cmd.addStringOption('s', "storePath");
		Option<String> filePathOption = cmd.addStringOption('f', "filePath");
		Option<Boolean> verboseOption = cmd.addBooleanOption('v', "verbose");
		Option<Integer> hiddenOption = cmd.addIntegerOption('h', "hidden");
		cmd.parse(args);

		String mode = cmd.getOptionValue(modeOption, "tell");
		String loadPath = cmd.getOptionValue(loadPathOption);
		String storePath = cmd.getOptionValue(storePathOption);
		String filePath = cmd.getOptionValue(filePathOption);
		Integer hidden = cmd.getOptionValue(hiddenOption);
		boolean verbose = cmd.getOptionValue(verboseOption, false);

		NeuroAnalytics app = new NeuroAnalytics(mode, loadPath, storePath, filePath,
				verbose, hidden);
		app.run();
	}
	*/

	public static void main(String[] args) throws Exception {
		System.out.println("Entering analytics!");
		final int  low = 20;
		final int high = 80;
		final int step = 20;
		Integer hid = low;
		NeuroAnalytics instance = new NeuroAnalytics(null, "Letters", null, null,false);
		while ( hid <= high) {
			System.out.println("\nGoing to make new brain with "+hid.toString()+"neurons");
			instance.curBrain = instance.trainAndReady(hid);
//			HashMap<String, Double> result = instance.curBrain.ask(tellFileName(0), false);
			double accuracy = instance.evaluateAccuracy();
			System.out.println("For hidden neurons:"+hid+" is accuracy: "+accuracy);
			hid+=step;
			}
	}

	public double evaluateAccuracy() throws Exception {
		double forall = 0;
		for(Integer number = 0; number<=9; number++) {
			if (verbose) System.out.println("\n going to guess:"+number.toString());
			double acc = 0;
			HashMap<String, Double> result = curBrain.ask(tellFileName(number), verbose);
			acc+=curBrain.evaluateAccuracyForNumber(result, number.toString());
			if (verbose) System.out.print("|acc for "+number.toString()+":"+Double.toString(acc));
			forall+=acc;
		}
		return forall;
	}

	private static String tellFileName(Integer num){
		return "Letters/"+num.toString()+"/"+num.toString()+"_1.png";
	}

	public NeuroAnalytics(String mode, String loadPath, String storePath,
			String filePath, boolean verbose) {
		this.mode = mode;
		this.loadPath = loadPath;
		this.storePath = storePath;
		this.filePath = filePath;
		this.verbose = verbose;
	}

	/*
	public void run() throws Exception {

		if (mode.compareTo("learn") == 0) {
			System.out.println("Start programm in learn mode.");

			if (loadPath == null || storePath == null) {
				System.out.println("Please specify a directory with images '--loadPath' and a place where to store the network '--filePath'.");
				return;
			}

			if (hidden == null)	{
				System.out.println("Please specify number of hidden neurons");
				return;
			}

			if (verbose) {
				System.out.println("Learning from: " + loadPath + ".");
				System.out.println("And later storring the results at: "
						+ storePath + ".");
			}
			trainAndSleep();
		} else if (mode.compareTo("tell") == 0) {
			System.out.println("Start programm in tell mode.");

			if (loadPath == null || filePath == null) {
				System.out.println("Please specify a network '--loadPath' and an imageFile '--filePath'.");
				return;
			}

			if (verbose) {
				System.out.println("Loading the network from: " + loadPath
						+ ".");
				System.out
						.println("And trying to recognice: " + filePath + ".");
			}
			ask();
		} else {
			System.out.println("I don't know, what you mean with '" + mode
					+ "' and my brain can't help me!");
		}

		System.out.println("Done.");
	}
	*/

	/*
	private void trainAndSleep() throws Exception {
		System.out.println("Create a new brain and train it ...");
		BrainFactory factory = new BrainFactory();
		Brain brain = factory.createFromTrainSet(loadPath, verbose, hidden);

		System.out.println("Putting the brain to sleep.");
		brain.sleepAt(storePath, verbose);
	}
	*/


	private Brain trainAndReady(Integer hidden) throws Exception {
		System.out.println("Create a new brain and train it ...");
		BrainFactory factory = new BrainFactory();
		return factory.createFromTrainSet(loadPath, verbose, hidden);
	}

	/*
	private void ask() throws Exception {
		System.out.println("Wakeing up the brain ...");
		BrainFactory factory = new BrainFactory();
		Brain brain = factory.createFromFile(loadPath, verbose);

		HashMap<String, Double> result = brain.ask(filePath, verbose);
		brain.interprete(result, verbose);
	}
	*/
}
