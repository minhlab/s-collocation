package ngocminh.collocation;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

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
				.fromUkwac(new File("src/test/resources/ukwac.1000.xml"));
		collocationDetector.getBigramCounts().write(System.out);
	}

}
