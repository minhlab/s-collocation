package ngocminh.collocation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.ucla.sspace.util.MultiMap;

public final class CollectionUtils {

	static final MultiMap<Double, String> EMPTY_MULTI_MAP = new MultiMap<Double, String>() {
	
		@Override
		public Map<Double, Set<String>> asMap() {
			return Collections.emptyMap();
		}
	
		@Override
		public void clear() { }
	
		@Override
		public boolean containsKey(Object key) {
			return false;
		}
	
		@Override
		public boolean containsMapping(Object key, Object value) {
			return false;
		}
	
		@Override
		public boolean containsValue(Object key) {
			return false;
		}
	
		@Override
		public Set<Entry<Double, String>> entrySet() {
			return Collections.emptySet();
		}
	
		@Override
		public Set<String> get(Object key) {
			return Collections.emptySet();
		}
	
		@Override
		public boolean isEmpty() {
			return true;
		}
	
		@Override
		public Set<Double> keySet() {
			return Collections.emptySet();
		}
	
		@Override
		public boolean put(Double key, String value) {
			throwExceptionOnChange();
			return false;
		}
	
		private void throwExceptionOnChange() {
			throw new UnsupportedOperationException("Trying to change an immutable collection.");
		}
	
		@Override
		public void putAll(Map<? extends Double, ? extends String> m) {
			throwExceptionOnChange();
		}
	
		@Override
		public void putAll(MultiMap<? extends Double, ? extends String> m) {
			throwExceptionOnChange();
		}
	
		@Override
		public boolean putMany(Double key, Collection<String> values) {
			throwExceptionOnChange();
			return false;
		}
	
		@Override
		public Set<String> remove(Double key) {
			throwExceptionOnChange();
			return Collections.emptySet();
		}
	
		@Override
		public boolean remove(Object key, Object value) {
			throwExceptionOnChange();
			return false;
		}
	
		@Override
		public int range() {
			return 0;
		}
	
		@Override
		public int size() {
			return 0;
		}
	
		@Override
		public Collection<String> values() {
			return Collections.emptySet();
		}
	
		@Override
		public Collection<Set<String>> valueSets() {
			return Collections.emptySet();
		}
	};

	private CollectionUtils() {
	}

	static MultiMap<Double, String> emptyMultiMap() {
		return EMPTY_MULTI_MAP;
	}
	
}
