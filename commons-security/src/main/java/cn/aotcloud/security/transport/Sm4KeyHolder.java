package cn.aotcloud.security.transport;

import org.springframework.core.NamedThreadLocal;

/**
 * @author xkxu
 */
public class Sm4KeyHolder {

	private static NamedThreadLocal<String> sm4KeyThreadLocal = new NamedThreadLocal<String>("Sm4Key ThreadLocal");
	
	private static NamedThreadLocal<String> sm4IvThreadLocal = new NamedThreadLocal<String>("Sm4Iv ThreadLocal");

	public static String getSm4Key() {
		return sm4KeyThreadLocal.get();
	}

	public static String getSm4Iv() {
		return sm4IvThreadLocal.get();
	}

	public static void setSm4Key(String sm4Key) {
		sm4KeyThreadLocal.set(sm4Key);
	}
	
	public static void setSm4Iv(String sm4Iv) {
		sm4IvThreadLocal.set(sm4Iv);
	}

	public static void clear() {
		sm4KeyThreadLocal.remove();
		sm4IvThreadLocal.remove();
	}
}
