package neuro.analytics;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
	enum evalMode {simple, firstmatch, relative};

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
		CmdLineParser cmd = new CmdLineParser();
		Option<String> paramOption = cmd.addStringOption('p', "param");
		Option<Double> lowOption = cmd.addDoubleOption('l', "low");
		Option<Double> highOption = cmd.addDoubleOption('h', "high");
		Option<Double> stepOption = cmd.addDoubleOption('s', "step");
		cmd.parse(args);
		final Double  low = cmd.getOptionValue(lowOption);
		final Double high = cmd.getOptionValue(highOption);
		final Double step = cmd.getOptionValue(stepOption);
		String param = cmd.getOptionValue(paramOption);
		if (param==null || high == null || low == null || step==null) {
			System.out.println("Specify all params! -l(num) - h(num) -s(num) -p('hidden'|'lrate')");
			return;
		}
		Double next = low;
		// defaults, the parameter examined will be changed
		// from low to high by step
		Integer hidden = 50;
		double learnrate = 0.1;
		System.out.println("Entering analytics! Going to manipulare param "+param+"within bounds"
				+low.toString()+" to "+high.toString()+" by "+step.toString());
		NeuroAnalytics instance = new NeuroAnalytics(null, "Trainset", null, null,false);
		while ( next <= high) {
			System.out.println("\nGoing to make new brain with param "+param+": "+next.toString());
			if (param.compareTo("hidden")== 0) {
				hidden = next.intValue();
			}
			else if (param.compareTo("lrate")== 0) {
				learnrate = next;
			}
			instance.curBrain = instance.trainAndReady(hidden, learnrate);
			double simAccuracy = instance.evaluateAccuracy(evalMode.simple);
			System.out.println("For param "+param+":"+next.toString()+" is simple accuracy: "+simAccuracy);
			double fmAccuracy = instance.evaluateAccuracy(evalMode.firstmatch);
			System.out.println("For param "+param+":"+next.toString()+" is firstmatch accuracy: "+fmAccuracy);
			double relAccuracy = instance.evaluateAccuracy(evalMode.relative);
			System.out.println("For param "+param+":"+next.toString()+" is relative accuracy: "+relAccuracy);
			next+=step;
			}
	}

	public double evaluateAccuracy(evalMode howEval) throws Exception {
		double forall = 0;
		for(Integer number = 0; number<=9; number++) {
			if (verbose) System.out.println("\nGoing to guess:"+number.toString());
			double acc = 0;
			HashMap<String, Double> result = curBrain.ask(tellFileName(number), verbose);
			switch(howEval) {
				case simple: acc+=evaluateSimpleAccuracy(result, number.toString());break;
				case relative: acc+=evaluateRelativeAccuracy(result, number.toString());break;
				case firstmatch: acc+=evaluateFMAccuracy(result, number.toString());break;
			}
			if(verbose) System.out.print("\nacc for "+number.toString()+":"+Double.toString(acc));
			forall+=acc;
		}
		return forall;
	}
	/*
    static void testAccuracy() {
		HashMap<String, Double> res = new HashMap<String, Double>();
		res.put("1", 0.89);
		res.put("2", 0.79);
		res.put("3", 0.40);
		res.put("4", 0.30);
		res.put("5", 0.20);
		res.put("6", 0.10);
		System.out.println("simple: "+evaluateSimpleAccuracy(res, "6").toString());
		System.out.println("fm: "+evaluateFMAccuracy(res, "6").toString());
		System.out.println("rel: "+evaluateRelativeAccuracy(res,"6").toString());
	}
	*/

	public Double evaluateSimpleAccuracy(HashMap<String, Double> result, String wanted) {
		return result.get(wanted);
	}

	public Double evaluateRelativeAccuracy(HashMap<String, Double> result, String wanted) {
		ValueComparator bvc = new ValueComparator(result);
		TreeMap<String, Double> sortedResult = new TreeMap<String, Double>(bvc);
		sortedResult.putAll(result);
		Entry<String, Double> properHit = sortedResult.ceilingEntry(wanted);
		Entry<String, Double> bestWrongHit;
		if(verbose) System.out.println("\nrelative accuracy for: "+wanted);
		if(verbose) System.out.println("proper key value "+properHit.getKey()+" "+properHit.getValue().toString());
		if(verbose) System.out.println("first key value "+sortedResult.firstEntry().getKey()+" "+sortedResult.firstEntry().getValue().toString());
		if (properHit.getKey() == sortedResult.firstEntry().getKey()) {
			bestWrongHit = sortedResult.higherEntry(wanted);
		//	System.out.println("lower key value "+bestWrongHit.getKey()+" "+bestWrongHit.getValue().toString());
		}
		else bestWrongHit = sortedResult.firstEntry();
		if(verbose) System.out.println("best wrong key value "+bestWrongHit.getKey()+" "+bestWrongHit.getValue().toString());
		return properHit.getValue() - bestWrongHit.getValue();
	}

	public Double evaluateFMAccuracy(HashMap<String, Double> result, String wanted) {
		ValueComparator bvc = new ValueComparator(result);
		TreeMap<String, Double> sortedResult = new TreeMap<String, Double>(bvc);
		sortedResult.putAll(result);
		Entry<String, Double> besthit = sortedResult.firstEntry();
		if(verbose) System.out.println("\nFMacc: best "+besthit.getKey()+" wanted"+wanted);
		if (besthit.getKey().compareTo(wanted)==0) return 1.0;
		return 0.0;
	}

	private static String tellFileName(Integer num){
		return "Evalset/"+num.toString()+"_1.png";
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


	private Brain trainAndReady(Integer hidden, double learnrate) throws Exception {
		System.out.println("Create a new brain and train it ...");
		BrainFactory factory = new BrainFactory();
		return factory.createFromTrainSet(loadPath, verbose, hidden, learnrate);
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

	static class ValueComparator implements Comparator<String> {

		Map<String, Double> base;

		public ValueComparator(Map<String, Double> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a) > base.get(b)) {
				return -1;
			} else if (base.get(a) < base.get(b)) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
