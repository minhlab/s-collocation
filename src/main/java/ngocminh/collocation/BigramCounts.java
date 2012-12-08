package ngocminh.collocation;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.ucla.sspace.util.BoundedSortedMultiMap;
import edu.ucla.sspace.util.Pair;
import edu.ucla.sspace.util.SortedMultiMap;

public class BigramCounts {
	
	private Map<String, FirstWordData> map = new HashMap<>();
	private Multiset<String> seconds = HashMultiset.create();

	public void add(String first, String second) {
		add(first, second, 1);
	}

	public void add(String first, String second, int count) {
		if (!map.containsKey(first)) {
			map.put(first, new FirstWordData());
		}
		FirstWordData firstWord = map.get(first);
		firstWord.count += count;
		firstWord.secondWords.add(second, count);
		seconds.add(second, count);
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
	
	/**
	 * Return the most frequent bigrams in <em>ascending<em> order.
	 * @param count -- number of returned bigrams 
	 * @return
	 */
	public SortedMultiMap<Integer, Pair<String>> getMostFrequentBigrams(int count) {
		BoundedSortedMultiMap<Integer, Pair<String>> pairMap = 
				new BoundedSortedMultiMap<>(count, false);
		for (Entry<String, FirstWordData> entry : map.entrySet()) {
			String first = entry.getKey();
			if (pairMap.size() == count
					&& entry.getValue().count < pairMap.firstKey()) {
				continue;
			}
			for (com.google.common.collect.Multiset.Entry<String> secondEntry : 
					entry.getValue().secondWords.entrySet()) {
				String second = secondEntry.getElement();
				int pairCount = secondEntry.getCount();
				// Check first to avoid creating unnecessary Pair objects
				if (pairMap.size() == count
						&& pairCount < pairMap.firstKey()) { 
					continue;
				}
				pairMap.put(pairCount, new Pair<>(first, second));
			}
		}
		return pairMap;
	}

	/**
	 * <p>
	 * Return an object that can iterate through the whole collection of bigrams
	 * in a for-each loop.
	 * </p>
	 * 
	 * <p>
	 * I considered return an Iterable of
	 * {@link com.google.common.collect.Multiset.Entry}<Pair<String>> but they
	 * didn't provide a default implementation and I'm too lazy to create yet
	 * another class :P
	 * </p>
	 * 
	 * @return
	 */
	public Iterable<Pair<String>> getBigrams() {
		return new Iterable<Pair<String>>() {
			
			@Override
			public Iterator<Pair<String>> iterator() {
				return new Iterator<Pair<String>>() {
					
					private Entry<String, FirstWordData> firstEntry; 
					private Iterator<Entry<String, FirstWordData>> firstIterator;
					private Iterator<com.google.common.collect.Multiset.Entry<String>> secondIterator;
					
					{
						firstIterator = map.entrySet().iterator();
					}
					
					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
					
					@Override
					public Pair<String> next() {
						while (secondIterator == null || !secondIterator.hasNext()) {
							firstEntry = firstIterator.next();
							secondIterator = firstEntry.getValue().secondWords.entrySet().iterator();
						}
						return new Pair<String>(firstEntry.getKey(), secondIterator.next().getElement());
					}
					
					@Override
					public boolean hasNext() {
						while (secondIterator == null || !secondIterator.hasNext()) {
							if (!firstIterator.hasNext()) {
								return false;
							}
							firstEntry = firstIterator.next();
							secondIterator = firstEntry.getValue().secondWords.entrySet().iterator();
						}
						return true;
					}
				};
			}
		};
	}
	
	private static class FirstWordData {
		
		int count;
		Multiset<String> secondWords = HashMultiset.create();
		
	}

}
