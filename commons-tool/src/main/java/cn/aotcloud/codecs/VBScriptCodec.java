package cn.aotcloud.codecs;

public class VBScriptCodec extends AbstractCharacterCodec {

	public final static char[] CHAR_ALPHANUMERICS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
			'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	/**
	 * Encode a String so that it can be safely used in a specific context.
	 * 
	 * @param immune
	 * @param input  the String to encode
	 * @return the encoded String
	 */
	public String encode(char[] immune, String input) {
		StringBuilder sb = new StringBuilder();
		boolean encoding = false;
		boolean inquotes = false;
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);

			// handle normal characters and surround them with quotes
			if (containsCharacter(c, CHAR_ALPHANUMERICS) || containsCharacter(c, immune)) {
				if (encoding && i > 0)
					sb.append("&");
				if (!inquotes && i > 0)
					sb.append("\"");
				sb.append(c);
				inquotes = true;
				encoding = false;

				// handle characters that need encoding
			} else {
				if (inquotes && i < input.length())
					sb.append("\"");
				if (i > 0)
					sb.append("&");
				sb.append(encodeCharacter(immune, Character.valueOf(c)));
				inquotes = false;
				encoding = true;
			}
		}
		return sb.toString();
	}

	/**
	 * Returns quote-encoded character
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

		return "chrw(" + (int) c.charValue() + ")";
	}

	/**
	 * Returns the decoded version of the character starting at index, or null if no
	 * decoding is possible.
	 * 
	 * Formats all are legal both upper/lower case: "x - all special characters " +
	 * chr(x) + " - not supported yet
	 */
	public Character decodeCharacter(PushbackSequence<Character> input) {
		input.mark();
		Character first = input.next();
		if (first == null) {
			input.reset();
			return null;
		}

		// if this is not an encoded character, return null
		if (first.charValue() != '\"') {
			input.reset();
			return null;
		}

		Character second = input.next();
		return second;
	}

}