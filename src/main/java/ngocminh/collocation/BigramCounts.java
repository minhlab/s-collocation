package ngocminh.collocation;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class BigramCounts {
	
	private Map<String, FirstWord> map = new HashMap<>();
	
	public void add(String first, String second) {
		if (!map.containsKey(first)) {
			map.put(first, new FirstWord());
		}
		FirstWord firstWord = map.get(first);
		firstWord.count++;
		firstWord.secondWords.add(second);
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
	
	public void write(OutputStream outputStream) {
		PrintStream out = new PrintStream(outputStream);
		for (Entry<String, FirstWord> entry : map.entrySet()) {
			String first = entry.getKey();
			out.format("#(%s) = %d\n", first, entry.getValue().count);
			for (com.google.common.collect.Multiset.Entry<String> secondEntry : 
					entry.getValue().secondWords.entrySet()) {
				out.format("#(%s, %s) = %d\n", first, 
						secondEntry.getElement(), secondEntry.getCount());
			}
		}
	}
	
	private static class FirstWord {
		
		int count;
		Multiset<String> secondWords = HashMultiset.create();
		
	}

}
