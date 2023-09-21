/**
 * OWASP Enterprise Security API (ESAPI)
 * 
 * This file is part of the Open Web Application Security Project (OWASP)
 * Enterprise Security API (ESAPI) project. For details, please see
 * <a href="http://www.owasp.org/index.php/ESAPI">http://www.owasp.org/index.php/ESAPI</a>.
 *
 * Copyright (c) 2007-2019 - The OWASP Foundation
 * 
 * The ESAPI is published by OWASP under the BSD license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 * 
 * @author Jeff Williams <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @created 2007
 */
package cn.aotcloud.codecs;

import java.io.IOException;
import java.net.URI;

import cn.aotcloud.exception.SafeException;

public interface Encoder {

	String canonicalize(String input);

	String canonicalize(String input, boolean strict);

	String canonicalize(String input, boolean throwEnable, boolean logEnable);

	String encodeForCSS(String untrustedData);

	String encodeForHTML(String untrustedData);

	String decodeForHTML(String input);

	String encodeForHTMLAttribute(String untrustedData);

	String encodeForJavaScript(String untrustedData);

	String encodeForVBScript(String untrustedData);

	@SuppressWarnings("rawtypes")
	String encodeForSQL(Codec codec, String input);

	@SuppressWarnings("rawtypes")
	String encodeForOS(Codec codec, String input);

	String encodeForLDAP(String input);

	String encodeForLDAP(String input, boolean encodeWildcards);

	String encodeForDN(String input);

	String encodeForXPath(String input);

	String encodeForXML(String input);

	String encodeForXMLAttribute(String input);

	String encodeForURL(String input) throws SafeException;

	String decodeFromURL(String input) throws SafeException;

	String encodeForBase64(byte[] input, boolean wrap);

	byte[] decodeFromBase64(String input) throws IOException;

	String getCanonicalizedURI(URI dirtyUri);

}
