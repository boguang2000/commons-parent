package cn.aotcloud.codecs;

public class WindowsCodec extends AbstractCharacterCodec {

	/**
	 * {@inheritDoc}
	 * 
	 * Returns Windows shell encoded character (which is ^)
	 *
	 * @param immune
	 */
	public String encodeCharacter(char[] immune, Character c) {
		char ch = c.charValue();

		// check for immune characters
		if (containsCharacter(ch, immune)) {
			return "" + ch;
		}

		// check for alphanumeric characters
		String hex = super.getHexForNonAlphanumeric(ch);
		if (hex == null) {
			return "" + ch;
		}

		return "^" + c;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Returns the decoded version of the character starting at index, or null if no
	 * decoding is possible.
	 * <p>
	 * Formats all are legal both upper/lower case: ^x - all special characters
	 */
	public Character decodeCharacter(PushbackSequence<Character> input) {
		input.mark();
		Character first = input.next();
		if (first == null) {
			input.reset();
			return null;
		}

		// if this is not an encoded character, return null
		if (first.charValue() != '^') {
			input.reset();
			return null;
		}

		Character second = input.next();
		return second;
	}

}