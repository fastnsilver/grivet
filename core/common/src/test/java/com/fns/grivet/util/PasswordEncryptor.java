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
package com.fns.grivet.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Encrypts a raw password for use with {@code BCryptPasswordEncoder}
 *
 * @author Chris Phillipson
 *
 */
public class PasswordEncryptor {

	public static void main(String[] args) {
		if (args != null && args.length == 1) {
			var passwordEncoder = new BCryptPasswordEncoder();
			var hashedPassword = passwordEncoder.encode(args[0]);
			System.out.println("Running PasswordEncryptor...\n-- Raw password: %s\n-- Encoded: %s\n".formatted(args[0],
					hashedPassword));
		}
		else {
			System.err.println("Please provide a password to be encoded!");
		}
	}

}
