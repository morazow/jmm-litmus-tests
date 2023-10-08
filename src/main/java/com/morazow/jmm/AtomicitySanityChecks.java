package com.morazow.jmm;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

/**
 * Checks atomicity of increment operations.
 *
 * Run with:
 * java -jar target/jmm-litmus-tests-1.0.0-assembly.jar -t AtomicitySanityChecks[.SubtestName] -v
 */
public class AtomicitySanityChecks {

    /**
     * Plain variable increments are not atomic.
     */
    @JCStressTest
    @Outcome(id = "1, 1", expect = ACCEPTABLE_INTERESTING, desc = "Update lost, data race.")
    @Outcome(id = "1, 2", expect = ACCEPTABLE, desc = "Actor1 incremented -> then Actor2.")
    @Outcome(id = "2, 1", expect = ACCEPTABLE, desc = "Actor2 incremented -> then Actor1.")
    @State
    public static class AtomicityPlain {
        private int v;

        @Actor
        public void actor1(final II_Result r) {
            r.r1 = ++v;
        }

        @Actor
        public void actor2(final II_Result r) {
            r.r2 = ++v;
        }
    }

    /**
     * Volatile increments are not atomic.
     *
     * Volatile guarantees visibility, but not atomicity. The increment operation, first reads the value from memory,
     * updates and then writes back to memory, making it possible for another thread to update the value in between.
     */
    @JCStressTest
    @Outcome(id = "1, 1", expect = ACCEPTABLE_INTERESTING, desc = "Update lost, data race.")
    @Outcome(id = "1, 2", expect = ACCEPTABLE, desc = "Actor1 incremented -> then Actor2.")
    @Outcome(id = "2, 1", expect = ACCEPTABLE, desc = "Actor2 incremented -> then Actor1.")
    @State
    public static class AtomicityVolatile {
        private volatile int v;

        @Actor
        public void actor1(final II_Result r) {
            r.r1 = ++v;
        }

        @Actor
        public void actor2(final II_Result r) {
            r.r2 = ++v;
        }
    }

    /**
     * Synchronized increments are atomic.
     */
    @JCStressTest
    @Outcome(id = "1, 1", expect = FORBIDDEN, desc = "Update lost, data race.")
    @Outcome(id = "1, 2", expect = ACCEPTABLE, desc = "Actor1 incremented -> then Actor2.")
    @Outcome(id = "2, 1", expect = ACCEPTABLE, desc = "Actor2 incremented -> then Actor1.")
    @State
    public static class AtomicitySynchronized {
        private int v;

        @Actor
        public void actor1(final II_Result r) {
            synchronized (this) {
                r.r1 = ++v;
            }
        }

        @Actor
        public void actor2(final II_Result r) {
            synchronized (this) {
                r.r2 = ++v;
            }
        }
    }

    /**
     * Reentrant lock increments are atomic.
     */
    @JCStressTest
    @Outcome(id = "1, 1", expect = FORBIDDEN, desc = "Update lost, data race.")
    @Outcome(id = "1, 2", expect = ACCEPTABLE, desc = "Actor1 incremented -> then Actor2.")
    @Outcome(id = "2, 1", expect = ACCEPTABLE, desc = "Actor2 incremented -> then Actor1.")
    @State
    public static class AtomicityReantrantLock {
        private ReentrantLock lock = new ReentrantLock();
        private int v;

        @Actor
        public void actor1(final II_Result r) {
            lock.lock();
            try {
                r.r1 = ++v;
            } finally {
                lock.unlock();
            }
        }

        @Actor
        public void actor2(final II_Result r) {
            lock.lock();
            try {
                r.r2 = ++v;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Atomic integer increments are atomic.
     */
    @JCStressTest
    @Outcome(id = "1, 1", expect = FORBIDDEN, desc = "Update lost, data race.")
    @Outcome(id = "1, 2", expect = ACCEPTABLE, desc = "Actor1 incremented -> then Actor2.")
    @Outcome(id = "2, 1", expect = ACCEPTABLE, desc = "Actor2 incremented -> then Actor1.")
    @State
    public static class AtomicityAtomicInteger {
        private AtomicInteger v = new AtomicInteger();

        @Actor
        public void actor1(final II_Result r) {
            r.r1 = v.incrementAndGet();
        }

        @Actor
        public void actor2(final II_Result r) {
            r.r2 = v.incrementAndGet();
        }
    }
}
