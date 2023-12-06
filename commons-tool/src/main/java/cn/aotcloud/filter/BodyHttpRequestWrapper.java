package cn.aotcloud.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import cn.aotcloud.exception.ExceptionUtil;
import cn.aotcloud.utils.HttpServletUtil;
import cn.aotcloud.utils.IOUtils;
import cn.aotcloud.utils.ServletUtils;

public class BodyHttpRequestWrapper extends HttpServletRequestWrapper {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, String[]> paramValuesMap = new HashMap<String, String[]>();
	
	private ByteArrayInputStream byteArrayInputStream = null;
	
	public BodyHttpRequestWrapper(HttpServletRequest request) {
		super(request);
		this.paramValuesMap.putAll(request.getParameterMap());
		this.copyInputStream();
	}
	
	public BodyHttpRequestWrapper(HttpServletRequest request, HttpInputMessage httpInputMessage) {
		super(request);
	}

	public void setQueryString(String queryString) {
		MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString("http://localhost/").query(queryString).build().getQueryParams();
		this.setQueryParams(queryParams);
	}
	
	public void setQueryParams(MultiValueMap<String, String> queryParams) {
		this.paramValuesMap = HttpServletUtil.transferQueryParams(queryParams);
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
		return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }
	
	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream(), ServletUtils.readCharacterEncoding(this)));
	}
	
	public String getBodyString() {
		String bodyString = null;
		try {
			if(byteArrayInputStream != null) {
				bodyString = IOUtils.toString(byteArrayInputStream, HttpServletUtil.getCharacterEncoding(this));
			} else {
				logger.error("载荷转换为字符串时流不能为空");
			}
		} catch (IOException e) {
			logger.error("载荷转换为字符串异常：{}", ExceptionUtil.getMessage(e));
		}
		return bodyString;
	}
	
	/**
	 * 备份流
	 */
	public void copyInputStream() {
		try(InputStream in = super.getInputStream()) {
			this.byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(in));
		} catch(IOException e) {
			logger.error("复制请求流时发生异常：{}", ExceptionUtil.getMessage(e));
		}
	}
}
