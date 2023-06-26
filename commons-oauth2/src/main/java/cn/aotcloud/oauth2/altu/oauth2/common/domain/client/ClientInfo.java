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

package cn.aotcloud.oauth2.altu.oauth2.common.domain.client;

/**
 * 代表一个注册到授权服务中的客户端
 */
public interface ClientInfo {

	/**
	 * @return	客户端ID
	 */
    String getClientId();

    /**
     * @return	客户端密钥
     */
    String getClientSecret();

    /**
     * @return	客户端颁发的时间戳
     */
    Long getIssuedAt();

    /**
     * @return	客户端过期时间
     */
    Long getExpiresIn();

    /**
     * @return	重定向地址
     */
    String getRedirectUri();

    String getClientUri();

    String getDescription();

    /**
     * @return	名称
     */
    String getName();

    /**
     * @return	图标URL地址
     */
    String getIconUri();
    
    /**
     * @return	客户端支持的授权模式，支持多种，默认是授权码模式
     */
    public String[] getGrantTypes();
}
