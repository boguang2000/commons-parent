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

import cn.aotcloud.oauth2.altu.oauth2.common.message.types.ParameterStyle;
import cn.aotcloud.oauth2.altu.oauth2.rs.extractor.BearerBodyTokenExtractor;
import cn.aotcloud.oauth2.altu.oauth2.rs.extractor.BearerCookieTokenExtractor;
import cn.aotcloud.oauth2.altu.oauth2.rs.extractor.BearerHeaderTokenExtractor;
import cn.aotcloud.oauth2.altu.oauth2.rs.extractor.BearerQueryTokenExtractor;
import cn.aotcloud.oauth2.altu.oauth2.rs.validator.BearerBodyOAuthValidator;
import cn.aotcloud.oauth2.altu.oauth2.rs.validator.BearerCookieOAuthValidator;
import cn.aotcloud.oauth2.altu.oauth2.rs.validator.BearerHeaderOAuthValidator;
import cn.aotcloud.oauth2.altu.oauth2.rs.validator.BearerQueryOAuthValidator;

public class BearerResourceServer extends ResourceServer {

    public BearerResourceServer() {
        extractors.put(ParameterStyle.HEADER, new BearerHeaderTokenExtractor());
        extractors.put(ParameterStyle.BODY, new BearerBodyTokenExtractor());
        extractors.put(ParameterStyle.QUERY, new BearerQueryTokenExtractor());
        
        // modify by xkxu
        extractors.put(ParameterStyle.COOKIE, new BearerCookieTokenExtractor());

        validators.put(ParameterStyle.HEADER, new BearerHeaderOAuthValidator());
        validators.put(ParameterStyle.BODY, new BearerBodyOAuthValidator());
        validators.put(ParameterStyle.QUERY, new BearerQueryOAuthValidator());
        
        // modify by xkxu
        validators.put(ParameterStyle.COOKIE, new BearerCookieOAuthValidator());
    }

}
