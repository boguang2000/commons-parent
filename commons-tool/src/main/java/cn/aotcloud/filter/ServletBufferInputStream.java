package cn.aotcloud.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class ServletBufferInputStream extends ServletInputStream {

	private ByteArrayInputStream inputStream;

	public ServletBufferInputStream(byte[] buffer) {
		this.inputStream = (buffer == null ? null : new ByteArrayInputStream(buffer));
	}

	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	@Override
	public int read() throws IOException {
		return inputStream.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return inputStream.read(b, off, len);
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public void setReadListener(ReadListener listener) {
		
	}
}
