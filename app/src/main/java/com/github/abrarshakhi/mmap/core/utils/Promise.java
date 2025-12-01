package com.github.abrarshakhi.mmap.core.utils;

import java.util.concurrent.*;
import java.util.function.Function;

public class Promise<V, E> {

    private final Executor executor;
    private volatile Outcome<V, E> outcome = null;
    private final Object lock = new Object();

    private Runnable onResolve = null;

    public Promise(Executor executor, Callable<Outcome<V, E>> task) {
        this.executor = executor;
        executor.execute(() -> {
            Outcome<V, E> result = null;
            try {
                result = task.call();
            } catch (Exception e) {
                result = Outcome.err((E) e);
            }
            resolve(result);
        });
    }

    private void resolve(Outcome<V, E> result) {
        synchronized (lock) {
            if (outcome != null) return;
            outcome = result;

            if (onResolve != null) {
                executor.execute(onResolve);
            }
        }
    }

    public boolean isDone() {
        return outcome != null;
    }

    public Outcome<V, E> get() {
        return outcome;
    }

    public <U> Promise<U, E> then(Function<V, Outcome<U, E>> next) {
        return new Promise<>(executor, () -> {
            waitForCompletion();
            Outcome<V, E> oc = outcome;

            if (oc.isOK()) {
                return next.apply(oc.unwrap());
            } else {
                return Outcome.err(oc.unwrapErr());
            }
        });
    }

    // ------------------------------------------
    // MAP (transform value, wrap into Outcome)
    // ------------------------------------------
    public <U> Promise<U, E> map(Function<V, U> mapper) {
        return then(v -> Outcome.ok(mapper.apply(v)));
    }

    // ------------------------------------------
    // ERROR HANDLING
    // ------------------------------------------
    public Promise<V, E> onError(Function<E, Outcome<V, E>> handler) {
        return new Promise<>(executor, () -> {
            waitForCompletion();
            Outcome<V, E> oc = outcome;

            if (oc.hasErr()) {
                return handler.apply(oc.unwrapErr());
            } else {
                return oc;
            }
        });
    }

    private void waitForCompletion() {
        synchronized (lock) {
            if (outcome != null) return;

            CompletableFuture<Void> cf = new CompletableFuture<>();
            onResolve = () -> cf.complete(null);

            try { cf.join(); }
            catch (Exception ignored) {}
        }
    }
}
