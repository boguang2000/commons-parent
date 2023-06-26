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

package cn.aotcloud.oauth2.altu.oauth2.common.message.types;

/**
 * 参数传递方式
 */
public enum ParameterStyle {
	
    BODY("body"),
    QUERY("query"),
    HEADER("header"),
    
    // modify by xkxu
    // 在上面三种都无法或者不好处理时，才能采用这种方式，采用cookie不是一种好的方式。这个基本上是最坏的方式了。
    COOKIE("cookie");

    private String parameterStyle;

    ParameterStyle(String parameterStyle) {
        this.parameterStyle = parameterStyle;
    }

    @Override
    public String toString() {
        return parameterStyle;
    }
}
