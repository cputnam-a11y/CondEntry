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

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import org.jspecify.annotations.Nullable;

/// A simple bidirectional mapping between entries and IDs.
public sealed interface SimpleRegistry<T> permits SimpleRegistryImpl {
	/// Register a new mapping.
	/// @throws IllegalArgumentException if a mapping for the given key already exists
	void register(Id id, T value) throws IllegalArgumentException;

	@Nullable
	T get(Id id);

	@Nullable
	Id getId(T value);

	Codec<T> byIdCodec();

	/// Create a new, empty registry.
	/// @param fallbackNamespace the fallback namespace to use when one isn't present when decoding via [#byIdCodec()]
	static <T> SimpleRegistry<T> create(@Nullable String fallbackNamespace) {
		return new SimpleRegistryImpl<>(fallbackNamespace);
	}
}
