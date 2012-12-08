package ngocminh.collocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import ngocminh.collocation.util.LineProcessorAdapter;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.SemanticSpaceIO;
import edu.ucla.sspace.util.Pair;

/**
 * Collocation detector I/O. Data is saved into a directory which contains three files 
 * for word space, word list and bigrams.  
 * 
 * @author Lê Ngọc Minh
 *
 */
public final class CollocationDetectorIO {

	private static final String BIGRAMS_FILE_NAME = "detector.bigrams";
	private static final String WORDS_FILE_NAME = "detector.words";
	private static final String SPACE_FILE_NAME = "detector.sspace";

	private CollocationDetectorIO() {
	}

	public static CollocationDetector load(File dir) throws IOException {
		BigramCounts bigramCounts = loadBigramCounts(dir);
		SemanticSpace semanticSpace = SemanticSpaceIO.load(new File(dir, SPACE_FILE_NAME));
		return new CollocationDetector(semanticSpace, bigramCounts);
	}

	public static void save(CollocationDetector detector, File dir)
			throws IOException {
		dir.mkdirs();
		saveBigramCounts(detector.getBigramCounts(), dir);
		SemanticSpaceIO.save(detector.getSemanticSpace(), new File(dir, SPACE_FILE_NAME));
	}

	private static BigramCounts loadBigramCounts(File dir) throws IOException {
		final BigramCounts bigramCounts = new BigramCounts();
		File wordFile = new File(dir, WORDS_FILE_NAME);
		File bigramFile = new File(dir, BIGRAMS_FILE_NAME);
		final ArrayList<String> words = new ArrayList<>();
		Files.readLines(wordFile, Charsets.UTF_8, new LineProcessorAdapter(false) {
			@Override
			public boolean processLine(String line) throws IOException {
				String[] chunks = line.split("\t");
				int id = Integer.parseInt(chunks[0]);
				while (words.size() <= id) words.add("");
				words.set(id, chunks[1]);
				return true;
			}
		});
		Files.readLines(bigramFile, Charsets.UTF_8, new LineProcessorAdapter(false) {
			@Override
			public boolean processLine(String line) throws IOException {
				String[] chunks = line.split("\t");
				String first = words.get(Integer.parseInt(chunks[0]));
				String second = words.get(Integer.parseInt(chunks[1]));
				int count = Integer.parseInt(chunks[2]);
				bigramCounts.add(first, second, count);
				return true;
			}
		});
		return bigramCounts;
	}
	
	private static void saveBigramCounts(BigramCounts bigramCounts, File dir) throws IOException {
		File wordFile = new File(dir, WORDS_FILE_NAME);
		File bigramFile = new File(dir, BIGRAMS_FILE_NAME);
		try (BufferedWriter wordOut = Files.newWriter(wordFile, Charsets.UTF_8);
				BufferedWriter bigramOut = Files.newWriter(bigramFile, Charsets.UTF_8)) {
			LinkedHashMap<String, Integer> idMap = new LinkedHashMap<>();
			for (Pair<String> bigram : bigramCounts.getBigrams()) {
				int first = getId(idMap, bigram.x);
				int second = getId(idMap, bigram.y);
				int count = bigramCounts.getBigramCount(bigram.x, bigram.y);
				bigramOut.append(String.valueOf(first)).append('\t')
					.append(String.valueOf(second)).append('\t')
					.append(String.valueOf(count)).append('\n');
			}
			for (Entry<String, Integer> idEntry : idMap.entrySet()) {
				wordOut.append(String.valueOf(idEntry.getValue())).append('\t')
						.append(idEntry.getKey()).append('\n');
			}
		}
	}

	private static int getId(Map<String, Integer> idMap, String s) {
		if (!idMap.containsKey(s)) {
			int id = idMap.size();
			idMap.put(s, id);
			return id;
		}
		return idMap.get(s);
	}
}
