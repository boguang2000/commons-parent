/*
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
package cn.aotcloud.oauth2.altu.oauth2.rs;

import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthSystemException;
import cn.aotcloud.oauth2.altu.oauth2.common.message.types.ParameterStyle;
import cn.aotcloud.oauth2.altu.oauth2.common.validators.OAuthValidator;
import cn.aotcloud.oauth2.altu.oauth2.rs.extractor.TokenExtractor;
import cn.aotcloud.utils.HttpServletUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ResourceServer {

    protected Map<ParameterStyle, TokenExtractor> extractors = new HashMap<ParameterStyle, TokenExtractor>();
    protected Map<ParameterStyle, OAuthValidator<HttpServletRequest>> validators =
    		new HashMap<ParameterStyle, OAuthValidator<HttpServletRequest>>();

    public OAuthValidator<HttpServletRequest> instantiateValidator(ParameterStyle ps) throws OAuthSystemException {
    	OAuthValidator<HttpServletRequest> oauthValidator = validators.get(ps);
        if (oauthValidator == null) {
            throw new OAuthSystemException("Cannot instantiate a message validator.");
        }
        return oauthValidator;
    }

    public TokenExtractor instantiateExtractor(ParameterStyle ps) throws OAuthSystemException {
    	TokenExtractor tokenExtractor = extractors.get(ps);
        if (tokenExtractor == null) {
            throw new OAuthSystemException("Cannot instantiate a token extractor.");
        }
        return tokenExtractor; //(TokenExtractor)OAuthUtils.instantiateClass(clazz);
    }

    /**
     * A replacement for HttpServletRequest.getParameter() as it will mess up with HTTP POST body
     * @param request
     * @param name
     * @return
     */
    public static String[] getQueryParameterValues(HttpServletRequest request, String name) {
        String query = HttpServletUtil.getQueryString(request);
        if (query == null) {
            return null;
        }
        List<String> values = new ArrayList<String>();
        String[] params = query.split("&");
        for (String param : params) {
            try {
                param = URLDecoder.decode(param, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // Ignore
            }
            int index = param.indexOf('=');
            String key = param;
            String value = null;
            if (index != -1) {
                key = param.substring(0, index);
                value = param.substring(index + 1);
            }
            if (key.equals(name)) {
                values.add(value);
            }
        }
        return values.toArray(new String[values.size()]);
    }

    public static String getQueryParameterValue(HttpServletRequest request, String name) {
        String[] values = getQueryParameterValues(request, name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }
}
