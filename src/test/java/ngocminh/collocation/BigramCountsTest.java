package ngocminh.collocation;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Iterables;

import edu.ucla.sspace.util.Pair;
import edu.ucla.sspace.util.SortedMultiMap;

public class BigramCountsTest {

	@Test
	public void empty() {
		BigramCounts bigramCounts = new BigramCounts();
		Assert.assertEquals(0, bigramCounts.getFirstCount("abc"));
	}

	@Test
	public void small() {
		BigramCounts bigramCounts = new BigramCounts();
		bigramCounts.add("the", "Illiad");
		bigramCounts.add("the", "apple");
		bigramCounts.add("the", "man");
		Assert.assertEquals(1, bigramCounts.getBigramCount("the", "Illiad"));
		Assert.assertEquals(1, bigramCounts.getBigramCount("the", "apple"));
		Assert.assertEquals(1, bigramCounts.getBigramCount("the", "man"));
		Assert.assertEquals(3, bigramCounts.getFirstCount("the"));
		Assert.assertEquals(0, bigramCounts.getFirstCount("Illiad"));
	}

	@Test
	public void large() throws IOException {
		CollocationDetector collocationDetector = CollocationDetector
				.fromUkwacCorpus(new File("src/test/resources/ukwac.1000.xml"));
		collocationDetector.getBigramCounts().write(System.out);
	}

	@Test
	public void mostFrequent() {
		BigramCounts bigramCounts = new BigramCounts();
		bigramCounts.add("the", "Illiad");
		bigramCounts.add("the", "apple");
		bigramCounts.add("the", "apple");
		bigramCounts.add("the", "apple");
		bigramCounts.add("the", "man");
		bigramCounts.add("the", "man");
		SortedMultiMap<Integer, Pair<String>> bigrams = bigramCounts.getMostFrequentBigrams(2);
		Iterator<Pair<String>> iterator = bigrams.values().iterator();
		Assert.assertEquals(new Pair<String>("the", "man"), iterator.next());
		Assert.assertEquals(new Pair<String>("the", "apple"), iterator.next());
	}
	
	@Test
	public void getBigrams() {
		BigramCounts bigramCounts = new BigramCounts();
		bigramCounts.add("the", "Illiad");
		bigramCounts.add("the", "apple");
		bigramCounts.add("the", "apple");
		bigramCounts.add("the", "man");
		Iterable<Pair<String>> bigrams = bigramCounts.getBigrams();
		Assert.assertEquals(3, Iterables.size(bigrams));
		Assert.assertTrue(Iterables.contains(bigrams, new Pair<>("the", "Illiad")));
		Assert.assertTrue(Iterables.contains(bigrams, new Pair<>("the", "apple")));
		Assert.assertTrue(Iterables.contains(bigrams, new Pair<>("the", "man")));
	}
	
}
