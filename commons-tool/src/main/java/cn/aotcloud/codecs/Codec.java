package cn.aotcloud.codecs;

public interface Codec<T> {
	/**
	 * Encode a String so that it can be safely used in a specific context.
	 * 
	 * @param immune
	 * @param input  the String to encode
	 * @return the encoded String
	 */
	public String encode(char[] immune, String input);

	/**
	 * Default implementation that should be overridden in specific codecs.
	 * 
	 * @param immune array of chars to NOT encode. Use with caution.
	 * @param c      the Character to encode
	 * @return the encoded Character
	 */
	public String encodeCharacter(char[] immune, Character c);

	/**
	 * Default codepoint implementation that should be overridden in specific
	 * codecs.
	 * 
	 * @param immune
	 * @param codePoint the integer to encode
	 * @return the encoded Character
	 */
	public String encodeCharacter(char[] immune, int codePoint);

	/**
	 * Decode a String that was encoded using the encode method in this Class
	 * 
	 * @param input the String to decode
	 * @return the decoded String
	 */
	public String decode(String input);

	/**
	 * Returns the decoded version of the next character from the input string and
	 * advances the current character in the PushbackSequence. If the current
	 * character is not encoded, this method MUST reset the PushbackString.
	 * 
	 * @param input the Character to decode
	 * 
	 * @return the decoded Character
	 */
	public T decodeCharacter(PushbackSequence<T> input);

	/**
	 * Lookup the hex value of any character that is not alphanumeric.
	 * 
	 * @param c The character to lookup.
	 * @return return null if alphanumeric or the character code in hex.
	 */
	public String getHexForNonAlphanumeric(char c);

	/**
	 * Lookup the hex value of any character that is not alphanumeric.
	 * 
	 * @param c The character to lookup.
	 * @return return null if alphanumeric or the character code in hex.
	 */
	public String getHexForNonAlphanumeric(int c);

	public String toOctal(char c);

	public String toHex(char c);

	public String toHex(int c);

	/**
	 * Utility to search a char[] for a specific char.
	 * 
	 * @param c
	 * @param array
	 * @return True if the supplied array contains the specified character. False
	 *         otherwise.
	 */
	public boolean containsCharacter(char c, char[] array);

}
