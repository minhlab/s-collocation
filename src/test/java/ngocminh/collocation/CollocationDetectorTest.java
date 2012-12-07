package ngocminh.collocation;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class CollocationDetectorTest {

	@Test
	public void test() throws IOException {
		CollocationDetector detector = 
				CollocationDetector.fromUkwac(new File(
				"src/test/resources/ukwac.1000.xml"));
		Assert.assertTrue(detector.getCollocation("be", "that") > 0);
	}
	
}
