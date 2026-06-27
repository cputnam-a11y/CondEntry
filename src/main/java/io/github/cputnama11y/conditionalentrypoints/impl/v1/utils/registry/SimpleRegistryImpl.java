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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public final class SimpleRegistryImpl<T> implements SimpleRegistry<T> {
	private final Map<Id, T> map;
	private final Map<T, Id> reverseMap;
	private final Codec<T> codec;

	public SimpleRegistryImpl(@Nullable String fallbackNamespace) {
		this.map = new HashMap<>();
		this.reverseMap = new IdentityHashMap<>();
		this.codec = Id.fallbackNamespaceCodec(fallbackNamespace).flatXmap(this::decode, this::encode);
	}

	@Override
	public void register(Id id, T value) throws IllegalArgumentException {
		if (this.map.containsKey(id)) {
			throw new IllegalArgumentException("A mapping for id " + id + " already present");
		} else {
			this.map.put(id, value);
			this.reverseMap.put(value, id);
		}
	}

	@Override
	@Nullable
	public T get(Id id) {
		Objects.requireNonNull(id);
		return this.map.get(id);
	}

	@Nullable
	@Override
	public Id getId(T value) {
		Objects.requireNonNull(value);
		return this.reverseMap.get(value);
	}

	@Override
	public Codec<T> byIdCodec() {
		return this.codec;
	}

	private Result<T> decode(Id id) {
		T value = this.get(id);
		if (value != null) {
			return Result.success(value);
		} else {
			return Result.error("Unknown ID: " + id);
		}
	}

	private Result<Id> encode(T value) {
		Id id = this.getId(value);
		if (id != null) {
			return Result.success(id);
		} else {
			return Result.error("Unknown object: " + value);
		}
	}
}
