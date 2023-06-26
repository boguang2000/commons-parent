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

package cn.aotcloud.oauth2.altu.oauth2.client;

import cn.aotcloud.oauth2.altu.oauth2.client.request.OAuthClientRequest;
import cn.aotcloud.oauth2.altu.oauth2.client.response.OAuthClientResponse;
import cn.aotcloud.oauth2.altu.oauth2.client.response.OAuthClientResponseFactory;
import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthProblemException;
import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthSystemException;
import cn.aotcloud.utils.HttpTrustUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation of the Oltu OAuth HttpClient using URL Connection
 */
public class URLConnectionClient extends cn.aotcloud.utils.URLConnectionClient implements HttpClient {
	
	protected static Logger logger = LoggerFactory.getLogger(URLConnectionClient.class);

	//调用该类直接跳过ssl证书
    static {
        try {
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier((urlHostName, session) -> true);
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
    }
    
    /**
     * 跳过ssl证书
     *
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static void trustAllHttpsCertificates() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[1];
        //trustAllCerts[0] = (TrustManager) new TrustAllManager();
        trustAllCerts[0] = HttpTrustUtil.createX509TrustManager();
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    
    @SuppressWarnings("unchecked")
	public <T extends OAuthClientResponse> T execute(OAuthClientRequest request, Map<String, String> headers,
                                                     String requestMethod, Class<T> responseClass)
            throws OAuthSystemException, OAuthProblemException {
    	
    	InputStream responseBody = null;
    	try {
    		Map<String, Object> map = executeBase(request.getLocationUri(), request.getHeaders(), request.getBody(), headers, requestMethod);
        	
        	byte[] responseBodyBytes = (byte[]) map.get("responseBodyBytes");
        	responseBody = new ByteArrayInputStream(responseBodyBytes);
        	String contentType = (String) map.get("contentType");
        	int responseCode = (int) map.get("responseCode");
        	Map<String, List<String>> responseHeaders = (Map<String, List<String>>) map.get("responseHeaders");
        	
            return OAuthClientResponseFactory.createCustomResponse(responseBody, contentType, responseCode, responseHeaders, responseClass);
    	} catch(RuntimeException e) {
    		throw new OAuthSystemException(e);
    	} finally {
    		if (responseBody != null) {
        		try {
					responseBody.close();
				} catch (IOException e) {
				}
        	}
    	}
    }

    @Override
    public void shutdown() {
        // Nothing to do here
    }

}
