package io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.codec;

import fish.cichlidmc.fishflakes.api.value.Either;
import fish.cichlidmc.fishflakes.api.value.Result;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@NullMarked
public record EitherMapCodec<L, R>(MapCodec<L> left, MapCodec<R> right, boolean strict) implements MapCodec<Either<L, R>> {
    @Override
    public Result<Either<L, R>> decode(JsonObject json) {
        var leftCopy = json.copy();
        Result<Either<L, R>> leftResult = this.left.decode(leftCopy).map(Either::left);
        if (!this.strict && leftResult.isSuccess()) {
            leftCopy.queriedKeys().forEach(json::get);
            return leftResult;
        }
        var rightCopy = json.copy();
        Result<Either<L, R>> rightResult = this.right.decode(rightCopy).map(Either::right);
        if (!this.strict && rightResult.isSuccess()) {
            rightCopy.queriedKeys().forEach(json::get);
            return rightResult;
        }

        if (this.strict && leftResult.isSuccess() && rightResult.isSuccess()) {
            return Result.error("Both formats were successful");
        } else if (leftResult.isSuccess()) {
            leftCopy.queriedKeys().forEach(json::get);
            return leftResult;
        } else if (rightResult.isSuccess()) {
            rightCopy.queriedKeys().forEach(json::get);
            return rightResult;
        } else {
            // both must be an Error based on previous conditions
            String firstMessage = ((Result.Error<?>) leftResult).message();
            String secondMessage = ((Result.Error<?>) rightResult).message();
            return Result.error("Both formats failed to decode: " + firstMessage + "; " + secondMessage);
        }
    }

    @Override
    public Optional<String> encode(JsonObject json, Either<L, R> value) {
        Result<JsonObject> result = Either.join(value.map(l -> {
            JsonObject copy = json.copy();
            return this.left.encode(copy, l)
                    .<Result<JsonObject>>map(Result::error)
                    .orElseGet(() -> Result.success(copy));
        }, r -> {
            JsonObject copy = json.copy();
            return this.right.encode(copy, r)
                    .<Result<JsonObject>>map(Result::error)
                    .orElseGet(() -> Result.success(copy));
        }));
        if (result.isSuccess()) {
            var queried = new ArrayList<>(json.queriedKeys());
            List.copyOf(json.keys()).forEach(json::remove);
            result.valueOrThrow().forEach(json::put);
            queried.addAll(result.valueOrThrow().queriedKeys());
            queried.forEach(json::get);
            return Optional.empty();
        }
        return Optional.of(result.messageOrThrow());
    }

    public static <L, R> MapCodec<Either<L, R>> lenient(MapCodec<L> left, MapCodec<R> right) {
        return new EitherMapCodec<>(left, right, false);
    }
    public static <L, R> MapCodec<Either<L, R>> strict(MapCodec<L> left, MapCodec<R> right) {
        return new EitherMapCodec<>(left, right, true);
    }
}