package cn.aotcloud.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.aotcloud.utils.IOUtils;
import cn.aotcloud.utils.ServletUtils;

public class BodyHttpRequestWrapper extends HttpServletRequestWrapper {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, String[]> paramValuesMap = new HashMap<String, String[]>();
	
	private byte[] buffer = null;

	public BodyHttpRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		this.paramValuesMap.putAll(request.getParameterMap());
		this.copyInputStream();
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	
	@Override
	public Enumeration<String> getParameterNames() {
		return super.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		return this.paramValuesMap.get(name);
	}

	public void setParameterValues(String name, String[] values) {
		this.paramValuesMap.put(name, values);
	}
	
	public void removeParameter(String name) {
		this.paramValuesMap.remove(name);
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		return this.paramValuesMap;
	}

	@Override
	public String getParameter(String name) {
		if (this.paramValuesMap.get(name) == null) {
			return null;
		} else {
			return this.paramValuesMap.get(name)[0];
		}
	}

	@Override
    public ServletInputStream getInputStream() throws IOException {
		if(this.buffer != null) {
			return new ServletBufferInputStream(this.buffer);
		} else {
			return null;
		}
    }
	
	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream(), ServletUtils.readCharacterEncoding(this)));
	}
	
	public String getBodyString() {
		String bodyString = null;
		try {
			bodyString = new String(this.buffer, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("buffer转字符串发生UnsupportedEncodingException");
		}
		return bodyString;
	}
	
	/**
	 * 备份流
	 * @throws IOException
	 */
	public void copyInputStream() throws IOException {
		InputStream is = null;
		try {
			is = super.getInputStream();
			if(is != null) {
				this.buffer = IOUtils.toByteArray(is);
			}
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
