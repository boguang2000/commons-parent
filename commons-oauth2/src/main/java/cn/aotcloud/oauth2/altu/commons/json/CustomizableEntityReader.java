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
package cn.aotcloud.oauth2.altu.commons.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map.Entry;

/**
 */
public abstract class CustomizableEntityReader<E, B extends CustomizableBuilder<E>> {

    private final B builder;

    public CustomizableEntityReader(B builder) {
        this.builder = builder;
    }

    protected final B getBuilder() {
        return builder;
    }

    /**
     * Method extracted from {@code org.json.JSONObject#JSONObject(JSONTokener)}
     *
     * @param jsonString
     */
    public void read(String jsonString) {
    	
    	JSONObject jsonObject = JSON.parseObject(jsonString);
    	
    	for (Entry<String, Object> entry : jsonObject.entrySet()) {
    		String key = entry.getKey();
    		Object value = entry.getValue();
    		
    		// guard from null values
            if (value != null) {
                if (value instanceof JSONArray) { // only plain simple arrays in this version
                    JSONArray array = (JSONArray) value;
                    Object[] values = new Object[array.size()];
                    for (int i = 0; i < array.size(); i++) {
                        values[i] = array.get(i);
                    }
                    value = values;
                }

                // if the concrete implementation is not able to handle the property, set the custom field
                if (!handleProperty(key, value)) {
                    builder.setCustomField(key, value);
                }
            }
    	}
    }

    protected abstract <T> boolean handleProperty(String key, T value);

}
