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

package cn.aotcloud.oauth2.altu.oauth2.rsfilter;

import javax.servlet.http.HttpServletRequest;

import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthProblemException;

/**
 * @author xkxu
 */
public interface OAuthRSProvider {

    /**
     * 验证令牌的合法性
     *
     * @param rsId  访问的资源ID，可以为空
     * @param token 访问令牌
     * @param req   HTTP请求对象
     * @return  令牌检验结果
     * @throws OAuthProblemException
     */
    OAuthDecision validateRequest(String rsId, String token, HttpServletRequest req) throws
            OAuthProblemException;

}