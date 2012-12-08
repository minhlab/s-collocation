package ngocminh.collocation;

import java.io.BufferedReader;
import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import edu.ucla.sspace.dependency.DependencyTreeNode;
import edu.ucla.sspace.dependency.WaCKyDependencyExtractor;

public class DependencyExtractorTest {

	@Test
	public void small() throws Exception {
		File file = new File("src/test/resources/sentences.txt");
		WaCKyDependencyExtractor extractor = new WaCKyDependencyExtractor();
		try (BufferedReader reader = Files.newReader(file, Charsets.UTF_8)) {
			DependencyTreeNode[] tree = extractor.readNextTree(reader);
			Assert.assertNotNull(tree);
		}
	}
	
}
