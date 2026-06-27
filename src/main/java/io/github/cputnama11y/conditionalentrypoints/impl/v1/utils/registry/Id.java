/*
* MIT License
*
* Copyright (c) 2025 CichlidMC
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*
*/
package io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.registry;

import fish.cichlidmc.fishflakes.api.value.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

/// A namespaced ID, used to uniquely identify several components of Sushi.
public final class Id implements Comparable<Id> {
	/// Codec that parses IDs from Strings. No fallback namespace.
	public static final Codec<Id> CODEC = fallbackNamespaceCodec(null);

	public final String namespace;
	public final String path;

	private final String asString;

	public Id(String namespace, String path) throws InvalidException {
		this.namespace = validate(namespace, "namespace", Id::isValidNamespace);
		this.path = validate(path, "path", Id::isValidPath);

		this.asString = namespace + ':' + path;
	}

	public Id suffixed(String suffix) {
		return new Id(this.namespace, this.path + suffix);
	}

	@Override
	public int compareTo(Id that) {
		int byNamespace = this.namespace.compareTo(that.namespace);
		if (byNamespace != 0) {
			return byNamespace;
		}

		return this.path.compareTo(that.path);
	}

	@Override
	public int hashCode() {
		return this.asString.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Id that && this.namespace.equals(that.namespace) && this.path.equals(that.path);
	}

	@Override
	public String toString() {
		return this.asString;
	}

	/// Try to parse an ID from the given String.
	///
	/// If `fallbackNamespace` is null, then a string without a namespace will fail to parse.
	/// If it's not null, then a string without a namespace will be treated as a path. For example:
	/// - No fallback: `"example:test"` -> `example:test`, `"test"` -> `null`
	/// - "h" fallback: `"example:test"` -> `example:test`, `"test"` -> `h:test`
	/// @return the parsed ID, or null if the string is not a valid ID
	@Nullable
	public static Id parseOrNull(@Nullable String fallbackNamespace, String string) {
		String[] split = string.split(":");

		String namespace;
		String path;

		if (split.length == 1 && fallbackNamespace != null) {
			namespace = fallbackNamespace;
			path = split[0];
		} else if (split.length == 2) {
			namespace = split[0];
			path = split[1];
		} else {
			return null;
		}

		try {
			return new Id(namespace, path);
		} catch (InvalidException _) {
			return null;
		}
	}

	public static Result<Id> tryParse(@Nullable String fallbackNamespace, String string) {
		Id parsed = parseOrNull(fallbackNamespace, string);
		if (parsed != null) {
			return Result.success(parsed);
		} else {
			return Result.error("Invalid ID: " + string);
		}
	}

	/// Create a codec that will parse IDs from strings using [#parseOrNull(String, String)].
	public static Codec<Id> fallbackNamespaceCodec(@Nullable String fallbackNamespace) {
		return Codec.STRING.comapFlatMap(s -> tryParse(fallbackNamespace, s), Id::toString);
	}

	public static boolean isValidNamespace(String string) {
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if ((c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '_') {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidPath(String string) {
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if ((c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '_' && c != '/' && c != '.') {
				return false;
			}
		}
		return true;
	}

	private static String validate(String string, String name, Predicate<String> validTest) {
		if (!validTest.test(string)) {
			throw new InvalidException(name + " contains one or more disallowed characters: " + string);
		}
		return string;
	}

	public static final class InvalidException extends RuntimeException {
		private InvalidException(String message) {
			super(message);
		}
	}
}
