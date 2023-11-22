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
package com.fns.grivet.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.json.JSONArray;
import org.json.JSONObject;

public class ValueHelper {

	public static Object getValue(AttributeType attributeType, Object source) {
		Object value = null;
		String stringValue = String.valueOf(source);
		// conversion takes place here...
		switch (attributeType) {
			case BIG_INTEGER:
				value = Long.valueOf(stringValue);
				break;
			case ISO_DATE:
				value = DateTimeFormatter.ISO_DATE.format(LocalDate.parse(stringValue));
				break;
			case ISO_DATETIME:
				value = DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.parse(stringValue));
				break;
			case ISO_INSTANT:
				value = "%s%s".formatted(stringValue, "Z");
				break;
			case DECIMAL:
				value = Double.valueOf(stringValue);
				break;
			case INTEGER:
				value = Integer.valueOf(stringValue);
				break;
			case VARCHAR:
				value = stringValue;
				break;
			case TEXT:
				value = stringValue;
				break;
			case JSON_BLOB:
				if (stringValue.startsWith("{")) {
					value = new JSONObject(stringValue);
				}
				if (stringValue.startsWith("[")) {
					value = new JSONArray(stringValue);
				}
				break;
			case BOOLEAN:
				int i = Integer.valueOf(stringValue);
				if (i == 1) {
					value = true;
				}
				else {
					value = false;
				}
				break;
			default:
				value = stringValue;
				break;
		}
		return value;
	}

	public static Object toValue(AttributeType attributeType, Object source) {
		Object value = null;
		String stringValue = String.valueOf(source);
		// conversion takes place here...
		TemporalAccessor ta;
		switch (attributeType) {
			case BIG_INTEGER:
				value = Long.valueOf(stringValue);
				break;
			case ISO_DATE:
				ta = DateTimeFormatter.ISO_DATE.parse(stringValue);
				LocalDate ld = LocalDate.from(ta);
				value = java.sql.Date.valueOf(ld);
				break;
			case ISO_DATETIME:
				ta = DateTimeFormatter.ISO_DATE_TIME.parse(stringValue);
				LocalDateTime ldt = LocalDateTime.from(ta);
				value = Timestamp.valueOf(ldt);
				break;
			case ISO_INSTANT:
				ta = DateTimeFormatter.ISO_INSTANT.parse(stringValue);
				Instant in = Instant.from(ta);
				value = Timestamp.valueOf(LocalDateTime.ofInstant(in, ZoneId.of("UTC")));
				break;
			case DECIMAL:
				value = Double.valueOf(stringValue);
				break;
			case INTEGER:
				value = Integer.valueOf(stringValue);
				break;
			case VARCHAR:
				value = stringValue;
				break;
			case TEXT:
				value = stringValue;
				break;
			case JSON_BLOB:
				value = stringValue;
				break;
			case BOOLEAN:
				boolean b = Boolean.parseBoolean(stringValue);
				if (b) {
					value = 1;
				}
				else {
					value = 0;
				}
				break;
			default:
				value = stringValue;
				break;
		}
		return value;
	}

}
