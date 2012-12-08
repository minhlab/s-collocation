package ngocminh.collocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.Similarity.SimType;
import edu.ucla.sspace.common.WordComparator;
import edu.ucla.sspace.util.MultiMap;
import edu.ucla.sspace.util.Pair;
import edu.ucla.sspace.util.SortedMultiMap;

public class Main {

	private CollocationDetector detector;
	private WordComparator comparator = new WordComparator();
	
	public Main() throws Exception {
	}
	
	public static void main(String[] args) throws Exception {
		CommandLine cmd = parseArguments(args);
		Main main = new Main();
		// load
		if (cmd.hasOption("i")) {
			main.restoreCollocationDetector(cmd.getOptionValue("i"));
		} else if (cmd.hasOption("w")) {
			main.loadUkwac(cmd.getOptionValue("w"));
		} else {
			System.out.println("An input argument is required.");
			return;
		}
		// test
		if (cmd.hasOption("s")) {
			if (cmd.getOptionValue("s").isEmpty()) {
				main.testSemanticSpace(new BufferedWriter(
						new OutputStreamWriter(System.out, Charsets.UTF_8)));
			} else {
				main.testSemanticSpace(cmd.getOptionValue("s"));
			}
		}
		if (cmd.hasOption("ci")) {
			
		}
		// save
		if (cmd.hasOption("o")) {
			main.storeCollocationDetector(cmd.getOptionValue("o"));
		}
	}

	private void storeCollocationDetector(String path) throws IOException {
		CollocationDetectorIO.save(detector, new File(path));
	}
	
	public void restoreCollocationDetector(String path) throws IOException {
		detector = CollocationDetectorIO.load(new File(path));
	}
	
	public void loadUkwac(String path) throws IOException {
		detector = CollocationDetector.fromUkwacCorpus(new File(path));
	}

	private void testColocations(String inputPath, String outputPath) {
		testBackwardCollocation("blonde", "hair");
		testBackwardCollocation("yellow", "flower");
		testBackwardCollocation("pay", "attention");
		testBackwardCollocation("commit", "crime");
		testBackwardCollocation("heavy", "rain");
	}

	public void testSemanticSpace(String outputPath) throws IOException {
		try (BufferedWriter out = Files.newWriter(new File(outputPath), Charsets.UTF_8)) {
			testSemanticSpace(out);
		}
	}

	private void testSemanticSpace(BufferedWriter out) throws IOException {
		SemanticSpace semanticSpace = detector.getSemanticSpace();
		for (String word : getWords(detector, 100)) {
			out.append("\n--- ").append(word).append(" ---");
			for (Entry<Double, String> entry : 
					getMostSimilarDescending(semanticSpace, word)) { 
				out.append(entry.toString()).append("\n");
			}
		}
	}

	private List<Entry<Double, String>> getMostSimilarDescending(
			SemanticSpace semanticSpace, String word) {
		SortedMultiMap<Double, String> similarWords = comparator
				.getMostSimilar(word, semanticSpace, 10, SimType.COSINE);
		List<Entry<Double, String>> entries = 
				new ArrayList<>(similarWords.entrySet());
		Collections.reverse(entries);
		return entries;
	}
	

	private Set<String> getWords(CollocationDetector detector, int count) {
		MultiMap<Integer, Pair<String>> bigrams = 
				detector.getBigramCounts().getMostFrequentBigrams(count);
		HashSet<String> words = new HashSet<>();
		for (Entry<Integer, Pair<String>> entry : bigrams.entrySet()) {
			words.add(entry.getValue().x);
			words.add(entry.getValue().y);
		}
		return words;
	}
	
	private void testBackwardCollocation(String first, String second) {
		System.out.println(detector.getBackwardCollocationScore(first, second));
		System.out.println(detector.getNeighbors());
	}

	private static CommandLine parseArguments(String[] args) {
		Options options = new Options();
		options.addOption(new Option("ci", "collocation-input", true, 
				"Path to a file storing collocations to be tested."));
		options.addOption(new Option("co", "collocation-output", true, 
				"Path to resulting file of collocation test."));
		options.addOption(new Option("s", "semantic", true, 
				"Tell the program to check the most frequent words and print to the specified file."));
		options.addOption(new Option("o", "output", true, 
				"Store the resulting semantic space and bigram counts to the specified file."));
		options.addOption(new Option("i", "input", true, 
				"Load semantic space and bigram counts from the specified file."));
		options.addOption(new Option("w", "wacky", true, 
				"Compute data from WaCkY corpus in the specified file or directory."));
		try {
			CommandLine cmd = new PosixParser().parse(options, args);
			return cmd;
		} catch (ParseException e) {
			new HelpFormatter().printHelp("s-collocation <options>", options);
			System.exit(1);
			return null;
		}
	}
	
}
