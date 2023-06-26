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

package cn.aotcloud.oauth2.altu.oauth2.rs.request;


import org.apache.commons.lang3.StringUtils;

import cn.aotcloud.oauth2.altu.oauth2.common.OAuth;
import cn.aotcloud.oauth2.altu.oauth2.common.error.OAuthError;
import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthProblemException;
import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthSystemException;
import cn.aotcloud.oauth2.altu.oauth2.common.message.types.ParameterStyle;
import cn.aotcloud.oauth2.altu.oauth2.common.message.types.TokenType;
import cn.aotcloud.oauth2.altu.oauth2.common.utils.OAuthUtils;
import cn.aotcloud.oauth2.altu.oauth2.common.validators.OAuthValidator;
import cn.aotcloud.oauth2.altu.oauth2.rs.BearerResourceServer;
import cn.aotcloud.oauth2.altu.oauth2.rs.ResourceServer;
import cn.aotcloud.oauth2.altu.oauth2.rs.extractor.TokenExtractor;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author xkxu
 */
public class OAuthAccessResourceRequest {
	
	private static final Map<TokenType, ResourceServer> tokens = new HashMap<TokenType, ResourceServer>();

    private HttpServletRequest request;
    private ParameterStyle[] parameterStyles = new ParameterStyle[] {OAuth.DEFAULT_PARAMETER_STYLE};
    private TokenType[] tokenTypes = new TokenType []{OAuth.DEFAULT_TOKEN_TYPE};
    private ParameterStyle usedParameterStyle;
    private ResourceServer usedResourceServer;
    private TokenExtractor extractor;
    
    {
        tokens.put(TokenType.BEARER, new BearerResourceServer());
        //TODO add MACResourceServer - see AMBER-41
    }
  
    public OAuthAccessResourceRequest(HttpServletRequest request)
        throws OAuthSystemException, OAuthProblemException {
        this(false, request,new TokenType[]{OAuth.DEFAULT_TOKEN_TYPE},
        		new ParameterStyle[] {OAuth.DEFAULT_PARAMETER_STYLE});
    }

    public OAuthAccessResourceRequest(HttpServletRequest request, ParameterStyle... parameterStyles)
            throws OAuthSystemException, OAuthProblemException {
        this(false, request, new TokenType[]{OAuth.DEFAULT_TOKEN_TYPE}, parameterStyles);
    }
    
    public OAuthAccessResourceRequest(boolean checkMutilToken, HttpServletRequest request, ParameterStyle... parameterStyles)
    		throws OAuthSystemException, OAuthProblemException {
    	this(checkMutilToken, request, new TokenType[]{OAuth.DEFAULT_TOKEN_TYPE}, parameterStyles);
    }

    public OAuthAccessResourceRequest(HttpServletRequest request, TokenType... tokenTypes)
            throws OAuthSystemException, OAuthProblemException {
        this(false, request, tokenTypes, new ParameterStyle[]{OAuth.DEFAULT_PARAMETER_STYLE});
    }
    
    public OAuthAccessResourceRequest(boolean checkMutilToken, HttpServletRequest request, 
    		TokenType[] tokenTypes ,ParameterStyle[] parameterStyles)
    				throws OAuthSystemException, OAuthProblemException {
        
    	this.request = request;
        this.tokenTypes = (tokenTypes != null && tokenTypes.length > 0) 
        		? Arrays.copyOf(tokenTypes, tokenTypes.length) : tokenTypes;
        		
        this.parameterStyles = (parameterStyles != null && parameterStyles.length > 0) 
        		? Arrays.copyOf(parameterStyles, parameterStyles.length) : parameterStyles;
        this.validate(checkMutilToken);
    }

    public String getAccessToken() throws OAuthSystemException {
        return extractor.getAccessToken(request);
    }

    
    /**
     * 验证是否有合法的访问令牌，支持从 Header、Query、Body、Cookie中获得访问令牌，
     * 
     * @throws OAuthSystemException
     * @throws OAuthProblemException
     */
	private void validate(boolean checkMutilToken) throws OAuthSystemException, OAuthProblemException {

        boolean lackAuthInfo = false;
        OAuthProblemException ex = null;
        String lackAuthReason = "OAuth parameters were not found";
        
        LinkedHashMap<ParameterStyle, ResourceServer> map = new LinkedHashMap<>();
        
        for (TokenType tokenType : tokenTypes) {
            ResourceServer resourceServer = instantiateResourceServer(tokenType);
            for (ParameterStyle style : parameterStyles) {
                try {

                    OAuthValidator<HttpServletRequest> validator = resourceServer.instantiateValidator(style);
                    validator.validateContentType(request);
                    validator.validateMethod(request);
                    validator.validateRequiredParameters(request);
                   
                    map.put(style, resourceServer);
                    
                } catch (OAuthProblemException e) {
                    //request lacks any authentication information?
                    if (OAuthUtils.isEmpty(e.getError())) {
                        lackAuthInfo = true;
                        lackAuthReason = e.getDescription();
                    } else {
                        ex = OAuthProblemException.error(e.getError(), e.getDescription());
                    }
                }
            }
        }
        
        String accessToken = null;
        if (map.size() > 1) {
        	for (Entry<ParameterStyle, ResourceServer> entry : map.entrySet()) {
        		TokenExtractor tokenExtractor = entry.getValue().instantiateExtractor(entry.getKey());
        		if (accessToken == null) {
        			accessToken = tokenExtractor.getAccessToken(request);
        		} else {
        			if (checkMutilToken && !StringUtils.equals(accessToken, tokenExtractor.getAccessToken(request))) {
        				throw OAuthProblemException.error(OAuthError.TokenResponse.INVALID_REQUEST,
        	                    "发现多个不同的访问令牌。");
        			}
        		}
        	}
        }
        
        if (map.isEmpty() && lackAuthInfo) {
            throw OAuthProblemException.error(OAuthError.TokenResponse.INVALID_REQUEST, lackAuthReason);
        }
        
        // 没有找到令牌
        if (map.isEmpty()) {
        	if (ex != null) {
                throw ex;
            }
            throw OAuthProblemException.error(OAuthError.TokenResponse.INVALID_REQUEST,
                    "OAuth parameters were not found");
        }
        
        usedParameterStyle = map.entrySet().iterator().next().getKey();
        usedResourceServer = map.entrySet().iterator().next().getValue();

        extractor = usedResourceServer.instantiateExtractor(usedParameterStyle);
    }

	public static ResourceServer instantiateResourceServer(TokenType tokenType) throws OAuthSystemException {
    	ResourceServer resourceServer = tokens.get(tokenType);
        if (resourceServer == null) {
            throw new OAuthSystemException("Cannot instantiate a resource server.");
        }
        return resourceServer; //(ResourceServer)OAuthUtils.instantiateClass(clazz);
    }
    
}
