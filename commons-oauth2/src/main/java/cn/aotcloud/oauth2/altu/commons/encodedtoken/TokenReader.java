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
package cn.aotcloud.oauth2.altu.commons.encodedtoken;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;

public abstract class TokenReader<T> extends TokenDecoder {

    /**
     * The Base64 JSON string default separator.
     */
    private final Pattern base64urlTokenPattern = Pattern.compile("([a-zA-Z0-9/+=]+)\\.([a-zA-Z0-9/+=]+)\\.(.+)");

    /**
     * Read the base64url token string
     * @param base64String
     * @return
     */
    public T read(String b64String) {
        if (b64String == null || b64String.isEmpty()) {
            throw new IllegalArgumentException("Impossible to obtain a Token from a null or empty string");
        }
        b64String = RegExUtils.replaceAll(b64String, "\n", "");
        b64String = RegExUtils.replaceAll(b64String, "\r", "");
        // TODO improve multi-line tokens
//        StringBuilder buffer = new StringBuilder();
//        BufferedReader reader = new BufferedReader(new StringReader(base64String));
//        String line = null;
//        try {
//            while ((line = reader.readLine()) != null) {
//                buffer.append(line.trim());
//            }
//        } catch (IOException e) {
//            // it cannot happen
//        } finally {
//            try {
//                reader.close();
//            } catch (IOException e) {
//                // swallow it
//            }
//        }
//
//        Matcher matcher = base64urlTokenPattern.matcher(buffer.toString());
        Matcher matcher = base64urlTokenPattern.matcher(b64String);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(b64String
                                               + "is not a valid Token, it does not match with the pattern: "
                                               + base64urlTokenPattern.pattern());
        }

        // HEADER
        String header = matcher.group(1);
        String decodedHeader = base64Decode(header);

        // BODY
        String body = matcher.group(2);
        String decodedBody = base64Decode(body);

        // SIGNATURE
        // Keep signature encoded in base64url
        String signature = matcher.group(3);

        return build(b64String, decodedHeader, decodedBody, signature);
    }

    /**
     * Build the token reader
     *
     * @param rawString
     * @param decodedHeader
     * @param decodedBody
     * @param encodedSignature
     * @return
     */
    protected abstract T build(String rawString, String decodedHeader, String decodedBody, String encodedSignature);
}
