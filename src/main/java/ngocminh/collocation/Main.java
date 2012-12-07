package ngocminh.collocation;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		CollocationDetector detector = CollocationDetector.fromUkwac(new File(args[0]));
		System.out.println(detector.getCollocation("blonde", "hair"));
		System.out.println(detector.getCollocation("yellow", "flower"));
		System.out.println(detector.getCollocation("pay", "attention"));
		System.out.println(detector.getCollocation("commit", "crime"));
		System.out.println(detector.getCollocation("heavy", "rain"));
	}
	
}
