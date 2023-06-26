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

package cn.aotcloud.oauth2.altu.oauth2.client.response;

import org.springframework.util.FileCopyUtils;

import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthProblemException;
import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthSystemException;
import cn.aotcloud.oauth2.altu.oauth2.common.utils.OAuthUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 *
 *
 *
 */
public class OAuthClientResponseFactory {

    public static OAuthClientResponse createGitHubTokenResponse(String body, String contentType,
                                                                int responseCode)
            throws OAuthProblemException {
        GitHubTokenResponse resp = new GitHubTokenResponse();
        resp.init(body, contentType, responseCode);
        return resp;
    }

    public static OAuthClientResponse createJSONTokenResponse(String body, String contentType,
                                                              int responseCode)
            throws OAuthProblemException {
        OAuthJSONAccessTokenResponse resp = new OAuthJSONAccessTokenResponse();
        resp.init(body, contentType, responseCode);
        return resp;
    }

    @SuppressWarnings("unchecked")
	public static <T extends OAuthClientResponse> T createCustomResponse(String body, String contentType,
                                                                         int responseCode,
                                                                         Map<String, List<String>> headers,
                                                                         Class<T> clazz)
            throws OAuthSystemException, OAuthProblemException {

        OAuthClientResponse resp = OAuthUtils
                .instantiateClassWithParameters(clazz, null, null);

        resp.init(body, contentType, responseCode, headers);

        return (T) resp;
    }

    public static <T extends OAuthClientResponse> T createCustomResponse(InputStream body, String contentType,
                                                                         int responseCode,
                                                                         Map<String, List<String>> headers,
                                                                         Class<T> clazz)
            throws OAuthSystemException, OAuthProblemException {

        T resp = OAuthUtils.instantiateClassWithParameters(clazz, null, null);

        if (body == null) {
            body = new ByteArrayInputStream(new byte[0]);
        } else {
        	try {
				body = new ByteArrayInputStream(FileCopyUtils.copyToByteArray(body));
			} catch (IOException e) {
				throw new OAuthSystemException(e);
			}
        }
        resp.init(body, contentType, responseCode, headers);

        return resp;
    }
}
