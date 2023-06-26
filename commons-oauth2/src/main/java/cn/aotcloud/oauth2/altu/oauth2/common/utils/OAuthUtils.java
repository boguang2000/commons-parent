/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.aotcloud.oauth2.altu.oauth2.common.utils;

import cn.aotcloud.oauth2.altu.oauth2.common.OAuth;
import cn.aotcloud.oauth2.altu.oauth2.common.error.OAuthError;
import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthProblemException;
import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthSystemException;
import cn.aotcloud.utils.OAuthSafeUtil;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common OAuth Utils class.
 * <p/>
 * Some methods based on the Utils class from OAuth V1.0a library available at:
 * http://oauth.googlecode.com/svn/code/java/core/
 */
public final class OAuthUtils {

    public static final String ENCODING = "UTF-8";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String NAME_VALUE_SEPARATOR = "=";

    public static final String AUTH_SCHEME = OAuth.OAUTH_HEADER_NAME;

    private static final Pattern OAUTH_HEADER = Pattern.compile("\\s*(\\w*)\\s+(.*)");
    private static final Pattern NVP = Pattern.compile("(\\S*)\\s*\\=\\s*\"([^\"]*)\"");

    public static final String MULTIPART = "multipart/";

    /**
     * Translates parameters into <code>application/x-www-form-urlencoded</code> String
     *
     * @param parameters parameters to encode
     * @param encoding   The name of a supported
     *                   <a href="../lang/package-summary.html#charenc">character
     *                   encoding</a>.
     * @return Translated string
     */
    public static String format(
        final Collection<? extends Map.Entry<String, Object>> parameters,
        final String encoding) {
        final StringBuilder result = new StringBuilder();
        for (final Map.Entry<String, Object> parameter : parameters) {
            String value = parameter.getValue() == null ? null : String.valueOf(parameter.getValue());
            
            if (!OAuthUtils.isEmpty(parameter.getKey()) && !OAuthUtils.isEmpty(value)) {
            	
                final String encodedName = encode(parameter.getKey(), encoding);
                final String encodedValue = value != null ? encode(value, encoding) : "";
                if (result.length() > 0) {
                    result.append(PARAMETER_SEPARATOR);
                }
                result.append(encodedName);
                result.append(NAME_VALUE_SEPARATOR);
                result.append(encodedValue);
            }
        }
        return result.toString();
    }

    private static String encode(final String content, final String encoding) {
        try {
            return URLEncoder.encode(content, encoding != null ? encoding : ENCODING);
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    /**
     * Read data from Input Stream and save it as a String.
     *
     * @param is InputStream to be read
     * @return String that was read from the stream
     */
    public static String saveStreamAsString(InputStream is) throws IOException {
        return toString(is, ENCODING);
    }

    /**
     * Get the entity content as a String, using the provided default character set
     * if none is found in the entity.
     * If defaultCharset is null, the default "UTF-8" is used.
     *
     * @param is             input stream to be saved as string
     * @param defaultCharset character set to be applied if none found in the entity
     * @return the entity content as a String
     * @throws IllegalArgumentException if entity is null or if content length > Integer.MAX_VALUE
     * @throws IOException              if an error occurs reading the input stream
     */
    public static String toString(final InputStream is, final String defaultCharset) throws IOException {
        return OAuthSafeUtil.toString(is, defaultCharset);
    }

    /**
     * Creates invalid_request exception with given message
     *
     * @param message error message
     * @return OAuthException
     */
    public static OAuthProblemException handleOAuthProblemException(String message) {
        return OAuthProblemException.error(OAuthError.TokenResponse.INVALID_REQUEST).description(message).responseStatus(400);
    }

    /**
     * Creates OAuthProblemException that contains set of missing oauth parameters
     *
     * @param missingParams missing oauth parameters
     * @return OAuthProblemException with user friendly message about missing oauth parameters
     */

    public static OAuthProblemException handleMissingParameters(Set<String> missingParams) {
    	StringBuilder sb = new StringBuilder("Missing parameters: ");
        if (!OAuthUtils.isEmpty(missingParams)) {
            for (String missingParam : missingParams) {
                sb.append(missingParam).append(" ");
            }
        }
        return handleOAuthProblemException(sb.toString().trim());
    }

    public static OAuthProblemException handleBadContentTypeException(String expectedContentType) {
    	StringBuilder errorMsg = new StringBuilder("Bad request content type. Expecting: ").append(
            expectedContentType);
        return handleOAuthProblemException(errorMsg.toString());
    }

    public static OAuthProblemException handleNotAllowedParametersOAuthException(
        List<String> notAllowedParams) {
        StringBuilder sb = new StringBuilder("Not allowed parameters: ");
        if (notAllowedParams != null) {
            for (String notAllowed : notAllowedParams) {
                sb.append(notAllowed).append(" ");
            }
        }
        return handleOAuthProblemException(sb.toString().trim());
    }

    /**
     * Parse a form-urlencoded document.
     */
    public static Map<String, Object> decodeForm(String form) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (!OAuthUtils.isEmpty(form)) {
            for (String nvp : form.split("\\&")) {
                int equals = nvp.indexOf('=');
                String name;
                String value;
                if (equals < 0) {
                    name = decodePercent(nvp);
                    value = null;
                } else {
                    name = decodePercent(nvp.substring(0, equals));
                    value = decodePercent(nvp.substring(equals + 1));
                }
                params.put(name, value);
            }
        }
        return params;
    }

    /**
     * Return true if the given Content-Type header means FORM_ENCODED.
     */
    public static boolean isFormEncoded(String contentType) {
        if (contentType == null) {
            return false;
        }
        int semi = contentType.indexOf(";");
        if (semi >= 0) {
            contentType = contentType.substring(0, semi);
        }
        return OAuth.ContentType.URL_ENCODED.equalsIgnoreCase(contentType.trim());
    }

    public static String decodePercent(String s) {
        try {
            return URLDecoder.decode(s, ENCODING);
            // This implements http://oauth.pbwiki.com/FlexibleDecoding
        } catch (UnsupportedEncodingException wow) {
            //throw new RuntimeException(wow.getMessage(), wow);
            throw new RuntimeException(wow);
        }
    }

    public static String percentEncode(String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLEncoder.encode(s, ENCODING)
                // OAuth encodes some characters differently:
                .replace("+", "%20").replace("*", "%2A")
                .replace("%7E", "~");
            // This could be done faster with more hand-crafted code.
        } catch (UnsupportedEncodingException wow) {
            //throw new RuntimeException(wow.getMessage(), wow);
            throw new RuntimeException(wow);
        }
    }

    private static boolean isEmpty(Set<String> missingParams) {
        if (missingParams == null || missingParams.size() == 0) {
            return true;
        }
        return false;
    }

    public static <T> T instantiateClass(Class<T> clazz) throws OAuthSystemException {
        return instantiateClassWithParameters(clazz, null, null);
    }

    public static <T> T instantiateClassWithParameters(Class<T> clazz, Class<?>[] paramsTypes,
                                                       Object[] paramValues) throws OAuthSystemException {

        try {
            if (paramsTypes != null && paramValues != null) {
                if (!(paramsTypes.length == paramValues.length)) {
                    throw new IllegalArgumentException("Number of types and values must be equal");
                }

                if (paramsTypes.length == 0 && paramValues.length == 0) {
                    return clazz.newInstance();
                }
                Constructor<T> clazzConstructor = clazz.getConstructor(paramsTypes);
                return clazzConstructor.newInstance(paramValues);
            }
            return clazz.newInstance();

        } catch (NoSuchMethodException e) {
            throw new OAuthSystemException(e);
        } catch (InstantiationException e) {
            throw new OAuthSystemException(e);
        } catch (IllegalAccessException e) {
            throw new OAuthSystemException(e);
        } catch (InvocationTargetException e) {
            throw new OAuthSystemException(e);
        }

    }


    public static String getAuthHeaderField(String authHeader) {

        if (authHeader != null) {
            Matcher m = OAUTH_HEADER.matcher(authHeader);
            if (m.matches()) {
                if (AUTH_SCHEME.equalsIgnoreCase(m.group(1))) {
                    return m.group(2);
                }
            }
        }
        return null;
    }

    public static Map<String, String> decodeOAuthHeader(String header) {
        Map<String, String> headerValues = new HashMap<String, String>();
        if (header != null) {
            Matcher m = OAUTH_HEADER.matcher(header);
            if (m.matches()) {
                if (AUTH_SCHEME.equalsIgnoreCase(m.group(1))) {
                    for (String nvp : m.group(2).split("\\s*,\\s*")) {
                        m = NVP.matcher(nvp);
                        if (m.matches()) {
                            String name = decodePercent(m.group(1));
                            String value = decodePercent(m.group(2));
                            headerValues.put(name, value);
                        }
                    }
                }
            }
        }
        return headerValues;
    }

    // todo: implement method to decode header form (with no challenge)

    /**
     * Decodes the Basic Authentication header into a username and password
     *
     * @param authenticationHeader {@link String} containing the encoded header value.
     *                             e.g. "Basic dXNlcm5hbWU6cGFzc3dvcmQ="
     * @return a {@link String[]} if the header could be decoded into a non null username and password or null.
     */
    public static String[] decodeClientAuthenticationHeader(String authenticationHeader) {
        if (isEmpty(authenticationHeader)) {
            return null;
        }
        String[] tokens = authenticationHeader.split(" ");
        if (tokens.length != 2) {
            return null;
        }
        String authType = tokens[0];
        if (!"basic".equalsIgnoreCase(authType)) {
            return null;
        }
        String encodedCreds = tokens[1];
        return decodeBase64EncodedCredentials(encodedCreds);
    }

    private static String[] decodeBase64EncodedCredentials(String encodedCreds) {
        String decodedCreds = null;
		try {
			decodedCreds = new String(Base64.decodeBase64(encodedCreds), ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
        String[] creds = decodedCreds.split(":", 2);
        if (creds.length != 2) {
          return null;
        }
        if (!OAuthUtils.isEmpty(creds[0]) && !OAuthUtils.isEmpty(creds[1])) {
          return creds;
        }
        return null;
    }

    /**
     * Construct a WWW-Authenticate header
     */
    public static String encodeOAuthHeader(Map<String, Object> entries) {
        StringBuffer sb = new StringBuffer();
        sb.append(OAuth.OAUTH_HEADER_NAME).append(" ");
        /*
         * Android 4.1 requires realm as first parameter!
         * If not set, it will throw an IOException
         * see parseChallenges in
         * https://android.googlesource.com/platform/libcore/+/android-4.1.2_r2/luni/src/main/java/libcore/net/http/HeaderParser.java 
         * more information:
         * http://stackoverflow.com/questions/11810447/httpurlconnection-worked-fine-in-android-2-x-but-not-in-4-1-no-authentication-c
         */
        if (entries.get("realm") != null) {
            String value = String.valueOf(entries.get("realm"));
            if (!OAuthUtils.isEmpty(value)) {
                sb.append("realm=\"");
                sb.append(value);
                sb.append("\",");
            }
            entries.remove("realm");
        }
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String value = entry.getValue() == null? null: String.valueOf(entry.getValue());
            if (!OAuthUtils.isEmpty(entry.getKey()) && !OAuthUtils.isEmpty(value)) {
                sb.append(entry.getKey());
                sb.append("=\"");
                sb.append(value);
                sb.append("\",");
            }
        }

        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Construct an Authorization Bearer header
     */
    public static String encodeAuthorizationBearerHeader(Map<String, Object> entries) {
    	// modify by xkxu StringBuffer 改成了 StringBuilder
    	StringBuilder sb = new StringBuilder();
        sb.append(OAuth.OAUTH_HEADER_NAME).append(" ");
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String value = entry.getValue() == null? null: String.valueOf(entry.getValue());
            if (!OAuthUtils.isEmpty(entry.getKey()) && !OAuthUtils.isEmpty(value)) {
                sb.append(value);
            }
        }

        return sb.toString();
    }

    public static boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }

    public static boolean hasEmptyValues(String[] array) {
        if (array == null || array.length == 0) {
            return true;
        }
        for (String s : array) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    public static String getAuthzMethod(String header) {
        if (header != null) {
            Matcher m = OAUTH_HEADER.matcher(header);
            if (m.matches()) {
                return m.group(1);

            }
        }
        return null;
    }

    public static Set<String> decodeScopes(String s) {
        return OAuthSafeUtil.decodeScopes(s);
    }

    public static String encodeScopes(Set<String> s) {
        return OAuthSafeUtil.encodeScopes(s);
    }

    public static boolean isMultipart(HttpServletRequest request) {

        if (!"post".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        if (contentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART)) {
            return true;
        }
        return false;
    }


    public static boolean hasContentType(String requestContentType, String requiredContentType) {
        return OAuthSafeUtil.hasContentType(requestContentType, requiredContentType);
    }

    
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
    	return OAuthSafeUtil.getCookieValue(request, cookieName);
    }
}


