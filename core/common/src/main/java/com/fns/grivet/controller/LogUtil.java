/*
 * Copyright 2015 - Chris Phillipson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fns.grivet.controller;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Creates a message from a {@code JSONObject}
 * and optionally supplied {@code String[]} arguments suitable for logging.
 *  
 * @author Chris Phillipson
 *
 */
class LogUtil {

    static String toLog(JSONObject jsonObject, String... args) {
        StringBuffer sb = new StringBuffer();
        if (args != null && args.length > 0) {
            Arrays.stream(args).forEach(a -> sb.append(a));
        }
        if (jsonObject != null) {
            sb.append("--\n");
            sb.append(jsonObject.toString());
            sb.append("--\n");
        }
        return sb.toString();
    }
    
} 
