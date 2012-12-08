package ngocminh.collocation;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class CollocationDetectorTest {

	@Test
	public void test() throws IOException {
		CollocationDetector detector = 
				CollocationDetector.fromUkwacCorpus(new File(
				"src/test/resources/ukwac.1000.xml"));
		Assert.assertTrue(detector.getForwardCollocationScore("be", "that") > 0);
	}
	
}
