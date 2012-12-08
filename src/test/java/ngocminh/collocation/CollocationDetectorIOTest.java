package ngocminh.collocation;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;

public class CollocationDetectorIOTest {

	@Test
	public void load() throws IOException {
		File dir = new File("src/test/resources/cdstore");
		CollocationDetector detector = CollocationDetectorIO.load(dir);
		Assert.assertNotNull(detector);
		Assert.assertEquals(8, detector.getSemanticSpace().getWords().size());
		Assert.assertEquals(6, Iterables.size(detector.getBigramCounts().getBigrams()));
	}

	@Test
	public void store() throws IOException {
		CollocationDetector detector = CollocationDetector.fromUkwacCorpus(
				new File("src/test/resources/ukwac.1.xml")); 
		File dir = Files.createTempDir();
		CollocationDetectorIO.save(detector, dir);
		Assert.assertTrue(new File(dir, "detector.bigrams").exists());
		Assert.assertTrue(new File(dir, "detector.sspace").exists());
		Assert.assertTrue(new File(dir, "detector.words").exists());
	}

}
