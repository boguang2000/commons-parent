package cn.aotcloud.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import cn.aotcloud.codecs.CSSCodec;
import cn.aotcloud.codecs.DefaultEncoder;
import cn.aotcloud.codecs.Encoder;
import cn.aotcloud.codecs.HTMLEntityCodec;
import cn.aotcloud.codecs.JavaScriptCodec;
import cn.aotcloud.codecs.MySQLCodec;
import cn.aotcloud.codecs.OracleCodec;
import cn.aotcloud.codecs.PercentCodec;
import cn.aotcloud.codecs.UnixCodec;
import cn.aotcloud.codecs.VBScriptCodec;
import cn.aotcloud.codecs.WindowsCodec;
import cn.aotcloud.codecs.XMLEntityCodec;

public class CodecUtils {

	protected static final Logger logger = LoggerFactory.getLogger(CodecUtils.class);
	
	private static final Encoder encoder = new DefaultEncoder(Arrays.asList(new Object[] {
			new HTMLEntityCodec(),
			new PercentCodec(),
			new JavaScriptCodec(),
			new VBScriptCodec(),
			new CSSCodec(),
			new MySQLCodec(),
			new OracleCodec(),
			new XMLEntityCodec(),
			new UnixCodec(),
			new WindowsCodec()}));
	
	public static String getMd5HexString(final InputStream data)  {
		try {
			return DigestUtils.md5Hex(data);
		} catch (IOException e) {
			logger.error("获取Md5编码时IOException异常");
		}
		return null;
	}
	
	public static String getSha1HexString(final InputStream data)  {
		try {
			return DigestUtils.sha1Hex(data);
		} catch (IOException e) {
			logger.error("获取Sha1编码时IOException异常");
		}
		return null;
	}
	
	public static ByteArrayInputStream stringToInputStream(String data) {
		if(StringUtils.isNotBlank(data)) {
			return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
		} else {
			return null;
		}
	}
	
	public static HttpInputMessage createHttpInputMessage(HttpHeaders httpHeaders, String data) {
		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(bytes);
		return new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
            	httpHeaders.setContentLength(bytes.length);
                return httpHeaders;
            }
            @Override
            public InputStream getBody() {
                return inputStream;
            }
        };
	}
		
	public static String canonicalize(String input) {
		return canonicalize(input, true);
	}

	public static String canonicalize(String input, boolean strict) {
		return canonicalize(input, strict, strict);
	}

	public static String canonicalize(String input, boolean throwEnable, boolean logEnable) {
		return encoder.canonicalize(input, throwEnable, logEnable);
	}
	
	public static String[] canonicalize(String[] inputArray) {
		return canonicalize(inputArray, true);
	}

	public static String[] canonicalize(String[] inputArray, boolean strict) {
		return canonicalize(inputArray, strict, strict);
	}

	public static String[] canonicalize(String[] inputArray, boolean throwEnable, boolean logEnable) {
		String[] outputArray = null;
		if(inputArray != null) {
			outputArray = new String[inputArray.length];
			for(int i=0; i< inputArray.length; i ++) {
				if(StringUtils.isNotEmpty(inputArray[i])) {
					outputArray[i] = encoder.canonicalize(inputArray[i], throwEnable, logEnable);
				} else {
					outputArray[i] = inputArray[i];
				}
			}
		}
		return outputArray;
	}
}
