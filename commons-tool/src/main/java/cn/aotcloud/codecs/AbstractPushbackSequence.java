package cn.aotcloud.codecs;

public abstract class AbstractPushbackSequence<T> implements PushbackSequence<T> {
	protected String input;
	protected T pushback;
	protected T temp;
	protected int index = 0;
	protected int mark = 0;

	public AbstractPushbackSequence(String input) {
		this.input = input;
	}

	/**
	 * {@inheritDoc}
	 */
	public void pushback(T c) {
		pushback = c;
	}

	/**
	 * {@inheritDoc}
	 */
	public int index() {
		return index;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		if (pushback != null)
			return true;
		if (input == null)
			return false;
		if (input.length() == 0)
			return false;
		if (index >= input.length())
			return false;
		return true;
	}
}
