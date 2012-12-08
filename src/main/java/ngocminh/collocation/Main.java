package ngocminh.collocation;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		CollocationDetector detector = CollocationDetector.fromUkwacCorpus(new File(args[0]));
		tryBackwardCollocation(detector, "blonde", "hair");
		tryBackwardCollocation(detector, "yellow", "flower");
		tryBackwardCollocation(detector, "pay", "attention");
		tryBackwardCollocation(detector, "commit", "crime");
		tryBackwardCollocation(detector, "heavy", "rain");
	}

	private static void tryBackwardCollocation(CollocationDetector detector,
			String first, String second) {
		System.out.println(detector.getBackwardCollocationScore(first, second));
		System.out.println(detector.getNeighbors());
	}
	
}
