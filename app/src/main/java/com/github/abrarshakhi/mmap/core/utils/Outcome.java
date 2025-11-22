package com.github.abrarshakhi.mmap.core.utils;


import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;


/**
 * A lightweight, functional-style container that represents either a successful value ({@link Ok})
 * or an error ({@link Err}). <br><br>
 * <p>
 * This type is similar to Rust's <code>Result&lt;T, E&gt;</code> and Go-style error handling.
 * It is designed to safely wrap operations that may fail without throwing exceptions.
 *
 * <p>Usage example:</p>
 *
 * <pre>
 * Outcome&lt;Integer, String&gt; parsed = parseInt("10");
 * if (parsed.isOK()) {
 *     System.out.println(parsed.unwrap());     // Success
 * } else {
 *     System.err.println(parsed.unwrapErr());  // Error
 * }
 * </pre>
 *
 * @param <V> The success value type.
 * @param <E> The error value type.
 */
public interface Outcome<V, E> {
    /**
     * Creates a successful {@link Ok} outcome containing the given value.
     *
     * @param value The value representing a successful result (may be null).
     * @param <V>   The success value type.
     * @param <E>   The error value type.
     * @return A new {@link Ok} instance containing {@code value}.
     */
    @NonNull
    @Contract(value = "_ -> new", pure = true)
    static <V, E> Outcome<V, E> ok(V value) {
        return new Ok<>(value);
    }

    /**
     * Creates an error {@link Err} result containing the given error.
     *
     * @param error The error value representing a failure (may be null).
     * @param <V>   The success value type.
     * @param <E>   The error value type.
     * @return A new {@link Err} instance containing {@code error}.
     */
    @NonNull
    @Contract(value = "_ -> new", pure = true)
    static <V, E> Outcome<V, E> err(E error) {
        return new Err<>(error);
    }

    /**
     * Alias of {@link #ok(Object)} for readability when handling results that represent success.
     *
     * @param value The successful value.
     * @param <V>   The success value type.
     * @param <E>   The error value type.
     * @return A new {@link Ok} containing {@code value}.
     */
    @NonNull
    @Contract(value = "_ -> new", pure = true)
    static <V, E> Outcome<V, E> success(V value) {
        return new Ok<>(value);
    }

    /**
     * Alias of {@link #err(Object)} for readability when handling results that represent failure.
     *
     * @param error The error value.
     * @param <V>   The success value type.
     * @param <E>   The error value type.
     * @return A new {@link Err} containing {@code error}.
     */
    @NonNull
    @Contract(value = "_ -> new", pure = true)
    static <V, E> Outcome<V, E> failure(E error) {
        return new Err<>(error);
    }

    /**
     * Returns {@code true} if this result represents success ({@link Ok}).
     *
     * @return {@code true} if the result is an {@link Ok}; {@code false} otherwise.
     */
    boolean isOK();

    /**
     * Returns {@code true} if this result represents failure ({@link Err}).
     *
     * @return {@code true} if the result is an {@link Err}; {@code false} otherwise.
     */
    boolean hasErr();

    /**
     * Indicates whether the wrapped value (in the case of {@link Ok}) is {@code null}.
     * <p>
     * This method:
     * <ul>
     *     <li>Returns {@code true} if {@link Ok} contains a null value.</li>
     *     <li>Returns {@code false} if {@link Ok} contains a non-null value.</li>
     *     <li>Throws {@link IllegalStateException} if called on an {@link Err}.</li>
     * </ul>
     *
     * @return {@code true} if the success value is {@code null}, {@code false} otherwise.
     * @throws IllegalStateException if called on an {@link Err}.
     */
    boolean isValueNull();

    /**
     * Retrieves the value inside this result if it is an {@link Ok}.
     *
     * @return The success value.
     * @throws IllegalStateException If called on an {@link Err}.
     */
    V unwrap();

    /**
     * Retrieves the value if this is an {@link Ok}.
     * Returns the provided fallback if this is an {@link Err}.
     *
     * @param fallback The value to return if this result contains an error.
     * @return The wrapped value or {@code fallback}.
     */
    V unwrapOr(V fallback);

    /**
     * Retrieves the error inside this result if it is an {@link Err}.
     *
     * @return The error value.
     * @throws IllegalStateException If called on an {@link Ok}.
     */
    E unwrapErr();

    /**
     * Represents a failed result containing an error value.
     *
     * @param <V> Success value type
     * @param <E> Error value type
     */
    class Err<V, E> implements Outcome<V, E> {
        private final E error;

        public Err(E error) {
            this.error = error;
        }

        @Override
        public boolean isOK() {
            return false;
        }

        @Override
        public boolean hasErr() {
            return true;
        }

        @Override
        public boolean isValueNull() {
            throw new IllegalStateException("Called isValueNull() on Err: " + error);
        }

        @Override
        public V unwrap() {
            throw new IllegalStateException("Called unwrap() on Err: " + error);
        }

        @Override
        public V unwrapOr(V fallback) {
            return fallback;
        }

        @Override
        public E unwrapErr() {
            return error;
        }

        @NonNull
        @Override
        public String toString() {
            return "Err(" + error + ")";
        }
    }

    /**
     * Represents a successful result containing a value.
     *
     * @param <V> Success value type
     * @param <E> Error value type
     */
    class Ok<V, E> implements Outcome<V, E> {
        private final V value;

        public Ok(V value) {
            this.value = value;
        }

        @Override
        public boolean isOK() {
            return true;
        }

        @Override
        public boolean hasErr() {
            return false;
        }

        @Override
        public boolean isValueNull() {
            return value == null;
        }

        @Override
        public V unwrap() {
            return value;
        }

        @Override
        public V unwrapOr(V fallback) {
            return value;
        }

        @Override
        public E unwrapErr() {
            throw new IllegalStateException("Called unwrapErr() on Ok");
        }

        @NonNull
        @Override
        public String toString() {
            return "Ok(" + value + ")";
        }
    }
}
