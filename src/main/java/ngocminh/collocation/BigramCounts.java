package ngocminh.collocation;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class BigramCounts {
	
	private Map<String, FirstWordData> map = new HashMap<>();
	private Multiset<String> seconds = HashMultiset.create();
	
	public void add(String first, String second) {
		if (!map.containsKey(first)) {
			map.put(first, new FirstWordData());
		}
		FirstWordData firstWord = map.get(first);
		firstWord.count++;
		firstWord.secondWords.add(second);
		seconds.add(second);
	}

	public int getBigramCount(String first, String second) {
		if (map.containsKey(first)) {
			return map.get(first).secondWords.count(second);
		}
		return 0;
	}

	public int getFirstCount(String first) {
		if (map.containsKey(first)) {
			return map.get(first).count;
		}
		return 0;
	}
	
	public int getSecondCount(String second) {
		return seconds.count(second);
	}
	
	public void write(OutputStream outputStream) {
		PrintStream out = new PrintStream(outputStream);
		for (Entry<String, FirstWordData> entry : map.entrySet()) {
			String first = entry.getKey();
			FirstWordData data = entry.getValue();
			
			out.format("#(%s) = %d\n", first, data.count);
			for (com.google.common.collect.Multiset.Entry<String> secondEntry : 
					data.secondWords.entrySet()) {
				out.format("#(%s, %s) = %d\n", first, 
						secondEntry.getElement(), secondEntry.getCount());
			}
		}
	}
	
	private static class FirstWordData {
		
		int count;
		Multiset<String> secondWords = HashMultiset.create();
		
	}

}
