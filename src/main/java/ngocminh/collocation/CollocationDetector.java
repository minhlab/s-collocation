package ngocminh.collocation;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.Similarity.SimType;
import edu.ucla.sspace.common.WordComparator;
import edu.ucla.sspace.dependency.DependencyExtractorManager;
import edu.ucla.sspace.dependency.DependencyRelation;
import edu.ucla.sspace.dependency.DependencyTreeNode;
import edu.ucla.sspace.dependency.WaCKyDependencyExtractor;
import edu.ucla.sspace.dv.DependencyVectorSpace;
import edu.ucla.sspace.text.Document;
import edu.ucla.sspace.text.IteratorFactory;
import edu.ucla.sspace.text.UkWacDependencyFileIterator;
import edu.ucla.sspace.util.SortedMultiMap;

public class CollocationDetector {

	private SemanticSpace semanticSpace;
	private BigramCounts bigramCounts;
	private int neighborHoodSize = 100;

	public CollocationDetector(SemanticSpace semanticSpace, BigramCounts bigramCounts) {
		this.semanticSpace = semanticSpace;
		this.bigramCounts = bigramCounts;
	}

	/**
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public double getCollocation(String first, String second) {
		WordComparator wordComparator = new WordComparator(1);
		double observedFreq = getForwardPredictingFrequency(first, second);
		double expectedFreq = 0;
		neighbors = wordComparator.getMostSimilar(
				second, semanticSpace, neighborHoodSize, SimType.COSINE);
		if (neighbors == null) {
			return Double.NaN;
		}
		for (Entry<Double, String> entry : neighbors.entrySet()) {
			expectedFreq += getForwardPredictingFrequency(first, entry.getValue());
		}
		expectedFreq /= neighbors.entrySet().size();
		return observedFreq - expectedFreq;
	}
	
	private double getForwardPredictingFrequency(String first, String second) {
		return bigramCounts.getBigramCount(first, second) /
				(double)bigramCounts.getFirstCount(first);
	}

	private static WaCKyDependencyExtractor extractor = new WaCKyDependencyExtractor();
	private SortedMultiMap<Double, String> neighbors;
	
	static {
		DependencyExtractorManager.addExtractor("wacky", extractor);
	}
	
	public static CollocationDetector fromUkwac(File file) throws IOException {
		CollocationDetector collocationDetector = new CollocationDetector(
				new DependencyVectorSpace(), new BigramCounts());
		collocationDetector.loadUkwacFile(file);
		return collocationDetector;
	}

	public void loadUkwacFile(File file) throws IOException {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				loadUkwacFile(child);
			}
			return;
		}
		for (UkWacDependencyFileIterator iterator = 
				new UkWacDependencyFileIterator(file.getAbsolutePath()); 
				iterator.hasNext();) {
			Document document = iterator.next();
			semanticSpace.processDocument(document.reader());
			addBigramCounts(bigramCounts, document);
		}
	}

	private static void addBigramCounts(BigramCounts bigramCounts,
			Document document) throws IOException {
		DependencyTreeNode[] tree = extractor.readNextTree(document.reader());
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
				bigramCounts.add(node.lemma(), depend.lemma());
			}
		}
	}

	public BigramCounts getBigramCounts() {
		return bigramCounts;
	}
	
	public SemanticSpace getSemanticSpace() {
		return semanticSpace;
	}
	
	SortedMultiMap<Double, String> getNeighbors() {
		return neighbors;
	}
	
	public int getNeighborHoodSize() {
		return neighborHoodSize;
	}
	
	public void setNeighborHoodSize(int neighborHoodSize) {
		this.neighborHoodSize = neighborHoodSize;
	}
	
}
