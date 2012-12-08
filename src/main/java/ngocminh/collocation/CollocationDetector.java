package ngocminh.collocation;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import ngocminh.collocation.util.NormalWordFilter;

import com.google.common.base.Objects;
import com.google.common.io.CharStreams;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.Similarity.SimType;
import edu.ucla.sspace.common.WordComparator;
import edu.ucla.sspace.dependency.DependencyExtractorManager;
import edu.ucla.sspace.dependency.DependencyRelation;
import edu.ucla.sspace.dependency.DependencyTreeNode;
import edu.ucla.sspace.dependency.WaCKyDependencyExtractor;
import edu.ucla.sspace.dri.DependencyRandomIndexing;
import edu.ucla.sspace.text.Document;
import edu.ucla.sspace.text.IteratorFactory;
import edu.ucla.sspace.text.UkWacDependencyFileIterator;
import edu.ucla.sspace.util.MultiMap;

public class CollocationDetector {

	private SemanticSpace semanticSpace;
	private BigramCounts bigramCounts;
	private WordComparator wordComparator = new WordComparator();
	
	private int neighborhoodSize = 100;
	private int processedDocumentCount = 0;

	/**
	 * Saved for testing purpose only
	 */
	private MultiMap<Double, String> neighbors;
	
	public CollocationDetector(SemanticSpace semanticSpace, BigramCounts bigramCounts) {
		this.semanticSpace = semanticSpace;
		this.bigramCounts = bigramCounts;
	}

	/**
	 * <p>Compute a score representing the grade that one word predicts its following
	 * counterpart.</p>
	 * 
	 * TODO fix terminology
	 * @param first
	 * @param second
	 * @return
	 */
	public double getForwardCollocationScore(String first, String second) {
		double observedFreq = getForwardFrequency(first, second);
		double expectedFreq = 0;
		neighbors = getNeighbors(second);
		for (Entry<Double, String> entry : neighbors.entrySet()) {
			expectedFreq += getForwardFrequency(first, entry.getValue());
		}
		expectedFreq /= neighbors.entrySet().size();
		return observedFreq - expectedFreq;
	}

	/**
	 * <p>Compute a score representing the grade that one word predicts its following
	 * counterpart.</p>
	 * 
	 * TODO fix terminology
	 * @param first
	 * @param second
	 * @return
	 */
	public double getBackwardCollocationScore(String first, String second) {
		double observedFreq = getBackwardFrequency(first, second);
		double expectedFreq = 0;
		neighbors = getNeighbors(first);
		for (Entry<Double, String> entry : neighbors.entrySet()) {
			expectedFreq += getBackwardFrequency(first, entry.getValue());
		}
		expectedFreq /= neighbors.entrySet().size();
		return observedFreq - expectedFreq;
	}

	private double getForwardFrequency(String first, String second) {
		return bigramCounts.getBigramCount(first, second) /
				(double)bigramCounts.getFirstCount(first);
	}

	private double getBackwardFrequency(String first, String second) {
		return bigramCounts.getBigramCount(first, second) /
				(double)bigramCounts.getSecondCount(first);
	}

	private MultiMap<Double, String> getNeighbors(String second) {
		return Objects.firstNonNull(wordComparator.getMostSimilar(
				second, semanticSpace, neighborhoodSize, SimType.COSINE),
				CollectionUtils.emptyMultiMap());
	}
	
	private static final WaCKyDependencyExtractor extractor; 
	
	static {
		extractor = new WaCKyDependencyExtractor(new NormalWordFilter(), null);
		DependencyExtractorManager.addExtractor("wacky", extractor);
	}
	
	/**
	 * Initiate and fill a collocation detector with data from an UkWaC corpus.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static CollocationDetector fromUkwacCorpus(File file) throws IOException {
		CollocationDetector collocationDetector = new CollocationDetector(
				new DependencyRandomIndexing(null), new BigramCounts());
		collocationDetector.loadUkwacCorpus(file);
		return collocationDetector;
	}

	/**
	 * Add data from the provided corpus to this detector. This method can be
	 * called several times to accumulate data. If the specified file happens to
	 * be a directory, its content will be loaded in a recursive manner.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void loadUkwacCorpus(File file) throws IOException {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				loadUkwacCorpus(child);
			}
			return;
		}
		for (Iterator<Document> iterator = 
				new UkWacDependencyFileIterator(file.getAbsolutePath()); 
				iterator.hasNext();) {
			Document document = iterator.next();
			try {
				processDocument(document);
			} catch (ArrayIndexOutOfBoundsException ex) {
				System.err.print("Error in document: ");
				System.err.println(CharStreams.toString(document.reader()));
			}
		}
	}

	private void processDocument(Document document) throws IOException {
		semanticSpace.processDocument(document.reader());
		addBigramCounts(bigramCounts, document);
		processedDocumentCount++;
		if (processedDocumentCount % 10 == 0) {
			System.out.println(processedDocumentCount);
		}
	}

	private static void addBigramCounts(BigramCounts bigramCounts,
			Document document) throws IOException {
		DependencyTreeNode[] tree = extractor.readNextTree(document.reader());
		if (tree == null) {
			return;
		}
		for (int i = 0; i < tree.length; i++) {
			DependencyTreeNode node = tree[i];
			if (node.word() == IteratorFactory.EMPTY_TOKEN) {
				continue;
			}
			for (DependencyRelation relation : node.neighbors()) {
				DependencyTreeNode depend = relation.dependentNode();
				if (depend == node || depend.word() == IteratorFactory.EMPTY_TOKEN) {
					continue;
				}
				bigramCounts.add(node.word(), depend.word());
			}
		}
	}

	public BigramCounts getBigramCounts() {
		return bigramCounts;
	}
	
	public SemanticSpace getSemanticSpace() {
		return semanticSpace;
	}
	
	MultiMap<Double, String> getNeighbors() {
		return neighbors;
	}

	/**
	 * Return neighborhood size -- the size of the examined neighborhood of a
	 * word to estimate its probability of accordance with the other word in
	 * pair.
	 * 
	 * @return
	 */
	public int getNeighborhoodSize() {
		return neighborhoodSize;
	}
	
	/**
	 * Set neighborhood size.
	 * 
	 * @param neighborHoodSize
	 */
	public void setNeighborhoodSize(int neighborHoodSize) {
		this.neighborhoodSize = neighborHoodSize;
	}
	
}
