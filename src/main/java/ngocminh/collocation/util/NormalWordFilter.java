package ngocminh.collocation.util;

import edu.ucla.sspace.text.TokenFilterInterface;

/**
 * Accept non-empty, letter-only tokens.
 * 
 * @author Lê Ngọc Minh
 *
 */
public class NormalWordFilter implements TokenFilterInterface {

	@Override
	public boolean accept(String token) {
		if (token.isEmpty()) {
			return false;
		}
		for (int i = 0; i < token.length(); i++) {
			if (!Character.isLetter(token.charAt(i))) {
				return false;
			}
		}
		return true;
	}


}
