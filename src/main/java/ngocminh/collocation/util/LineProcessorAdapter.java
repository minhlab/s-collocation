package ngocminh.collocation.util;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class LineProcessorAdapter implements LineProcessor<Void> {

	private int counter;
	private int reportPeriod = 1_000_000;
	private boolean counterReported;

	public LineProcessorAdapter() {
		this(false);
	}
	
	public LineProcessorAdapter(boolean counterReported) {
		this.counterReported = counterReported;
	}
	
	@Override
	public boolean processLine(String line) throws IOException {
		counter++;
		if (counterReported && (counter % reportPeriod == 0)) {
			printCount(counter);
		}
		doProcess(line);
		return true;
	}
	
	protected void doProcess(String line) throws IOException { }

	public int getCounter() {
		return counter;
	}
	
	@Override
	public Void getResult() {
		return null;
	}

	private static NumberFormatter numberFormatter = new NumberFormatter(
			NumberFormat.getNumberInstance());
	
	public static void printCount(int count) {
		try {
			System.out.println(numberFormatter.valueToString(count));
		} catch (ParseException e) { /* never */ }
	}
	
	public static int countLines(File file) throws IOException {
		LineProcessorAdapter processor = new LineProcessorAdapter(false);
		Files.readLines(file, Charsets.UTF_8, processor);
		return processor.getCounter();
	}
	
}