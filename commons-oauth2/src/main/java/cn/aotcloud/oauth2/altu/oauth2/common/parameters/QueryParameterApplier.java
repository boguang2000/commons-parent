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

package cn.aotcloud.oauth2.altu.oauth2.common.parameters;

import java.util.Map;

import cn.aotcloud.oauth2.altu.oauth2.common.message.OAuthMessage;
import cn.aotcloud.oauth2.altu.oauth2.common.utils.OAuthUtils;

/**
 *
 */
public class QueryParameterApplier implements OAuthParametersApplier {

    public OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params) {

        String messageUrl = message.getLocationUri();
        if (messageUrl != null) {
            StringBuilder url = new StringBuilder(messageUrl);	// modify by xkxu  StringBuffer 改成了 StringBuilder
            String query = OAuthUtils.format(params.entrySet(), OAuthUtils.ENCODING);
  
            if (!OAuthUtils.isEmpty(query)) {
                if (messageUrl.contains("?")) {
                    url.append("&").append(query);
                } else {
                    url.append("?").append(query);
                }
            }   
            message.setLocationUri(url.toString());
        }
        return message;
    }
}
