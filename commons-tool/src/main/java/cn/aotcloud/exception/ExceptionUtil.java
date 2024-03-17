package cn.aotcloud.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionUtil {
    
	public static String getMessage(Throwable t) {
		if(t != null) {
			return t.getMessage();
		} else {
			return null;
		}
	}
	
	public static void printStackTrace(Throwable t) {
		if(t != null) {
			t.printStackTrace();
		}
	}
	
	public static String getExceptionInfo(Exception ex) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String result = sw.toString();
            if(StringUtils.isNotBlank(result) && result.length() > 30000) {
                result = result.substring(0,30000);
            }
            return result;
        } finally {
            if (null != sw) {
                try {
                    sw.close();
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
            if (null != pw) {
                pw.close();
            }
        }
    }
	
	public static void printLog(Exception exception, boolean showMethodName) {
		StackTraceElement[] stackTraceElements = exception.getStackTrace();
		if(stackTraceElements != null && stackTraceElements.length > 0) {
			String className = stackTraceElements[0].getClassName();
			Logger logger = LoggerFactory.getLogger(className);
			if(showMethodName) {
				String methodName = stackTraceElements[0].getMethodName();
				String fileName = stackTraceElements[0].getFileName();
				int lineNumber = stackTraceElements[0].getLineNumber();
				logger.error("{}({}:{}):{}", methodName, fileName, lineNumber, getMessage(exception));
			} else {
				logger.error("{}:{}", getMessage(exception));
			}
		}
	}
}

