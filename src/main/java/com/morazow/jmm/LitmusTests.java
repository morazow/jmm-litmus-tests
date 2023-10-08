package com.morazow.jmm;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.IIII_Result;
import org.openjdk.jcstress.infra.results.II_Result;

/**
 * Litmus Tests.
 *
 * Run with:
 * java -jar target/jmm-litmus-tests-1.0.0-assembly.jar -t LitmusTests[.SubtestName] -v
 */
public class LitmusTests {

    /**
     * Message passing test with plain variables.
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = ACCEPTABLE, desc = "Thread1 runs after thread2 finishes.")
    @Outcome(id = "1, 1", expect = ACCEPTABLE, desc = "Thread2 runs after thread1 finishes.")
    @Outcome(id = "0, 1", expect = ACCEPTABLE, desc = "Thread2 is interleaved between writes.")
    @Outcome(id = "1, 0", expect = ACCEPTABLE_INTERESTING, desc = "Observed y write but not x, operations reordered?!")
    @State
    public static class MessagePassingPlain {
        private int x;
        private int y;

        @Actor
        public void actor1() {
            x = 1;
            y = 1;
        }

        @Actor
        public void actor2(final II_Result r) {
            r.r1 = y;
            r.r2 = x;
        }
    }

    /**
     * Message passing test with volatile (happens-before edge) variable.
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = ACCEPTABLE, desc = "Thread1 runs after thread2 finishes.")
    @Outcome(id = "1, 1", expect = ACCEPTABLE, desc = "Thread2 runs after thread1 finishes.")
    @Outcome(id = "0, 1", expect = ACCEPTABLE, desc = "Thread2 is interleaved between writes.")
    @Outcome(id = "1, 0", expect = FORBIDDEN, desc = "Observed y write but not x, operations reordered?!")
    @State
    public static class MessagePassingVolatile {
        private int x;
        private volatile int y;

        @Actor
        public void actor1() {
            x = 1;
            y = 1;
        }

        @Actor
        public void actor2(final II_Result r) {
            r.r1 = y;
            r.r2 = x;
        }
    }

    /**
     * Store buffering test with plain variables.
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = ACCEPTABLE_INTERESTING, desc = "Reads before writes, operations reordered?!")
    @Outcome(id = "0, 1", expect = ACCEPTABLE, desc = "One thread is interleaved between write and read operations.")
    @Outcome(id = "1, 0", expect = ACCEPTABLE, desc = "One thread is interleaved between write and read operations.")
    @Outcome(id = "1, 1", expect = ACCEPTABLE, desc = "Both variables written, and then read.")
    @State
    public static class StoreBufferingPlain {
        private int x;
        private int y;

        @Actor
        public void actor1(final II_Result r) {
            x = 1;
            r.r1 = y;
        }

        @Actor
        public void actor2(final II_Result r) {
            y = 1;
            r.r2 = x;
        }
    }

    /**
     * Store buffering test with volatile variables.
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = FORBIDDEN, desc = "Reads before writes, operations reordered?!")
    @Outcome(id = "0, 1", expect = ACCEPTABLE, desc = "One thread is interleaved between write and read operations.")
    @Outcome(id = "1, 0", expect = ACCEPTABLE, desc = "One thread is interleaved between write and read operations.")
    @Outcome(id = "1, 1", expect = ACCEPTABLE, desc = "Both variables written, and then read.")
    @State
    public static class StoreBufferingVolatile {
        private volatile int x;
        private volatile int y;

        @Actor
        public void actor1(final II_Result r) {
            x = 1;
            r.r1 = y;
        }

        @Actor
        public void actor2(final II_Result r) {
            y = 1;
            r.r2 = x;
        }
    }

    /**
     * Load buffering test with plain variables.
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = ACCEPTABLE, desc = "One thread is interleaved between read and write operations.")
    @Outcome(id = "0, 1", expect = ACCEPTABLE, desc = "Thread2 runs after thread1 finishes.")
    @Outcome(id = "1, 0", expect = ACCEPTABLE, desc = "Thread1 runs after thread2 finishes.")
    @Outcome(id = "1, 1", expect = ACCEPTABLE_INTERESTING, desc = "Reads see later writes, operations reordered?!")
    @State
    public static class LoadBufferingPlain {
        private final DataX dx = new DataX();
        private final DataY dy = new DataY();

        private static class DataX {
            int x;
            int trap;
        }

        private static class DataY {
            int y;
            int trap;
        }

        @Actor
        public void actor1(final II_Result r) {
            final DataX dx = this.dx;
            final DataY dy = this.dy;
            dx.trap = 0;
            dy.trap = 0;
            r.r2 = dy.y;
            dx.x = 1;
        }

        @Actor
        public void actor2(final II_Result r) {
            final DataX dx = this.dx;
            final DataY dy = this.dy;
            dx.trap = 0;
            dy.trap = 0;
            r.r1 = dx.x;
            dy.y = 1;
        }
    }

    /**
     * Load buffering test with volatile variables.
     */
    @JCStressTest
    @Outcome(id = "0, 0", expect = ACCEPTABLE, desc = "One thread is interleaved between read and write operations.")
    @Outcome(id = "0, 1", expect = ACCEPTABLE, desc = "Thread2 runs after thread1 finishes.")
    @Outcome(id = "1, 0", expect = ACCEPTABLE, desc = "Thread1 runs after thread2 finishes.")
    @Outcome(id = "1, 1", expect = FORBIDDEN, desc = "Reads see later writes, operations reordered?!")
    @State
    public static class LoadBufferingVolatile {
        private final DataX dx = new DataX();
        private final DataY dy = new DataY();

        private static class DataX {
            int x;
            int trap;
        }

        private static class DataY {
            volatile int y;
            int trap;
        }

        @Actor
        public void actor1(final II_Result r) {
            final DataX dx = this.dx;
            final DataY dy = this.dy;
            dx.trap = 0;
            dy.trap = 0;
            r.r2 = dy.y;
            dx.x = 1;
        }

        @Actor
        public void actor2(final II_Result r) {
            final DataX dx = this.dx;
            final DataY dy = this.dy;
            dx.trap = 0;
            dy.trap = 0;
            r.r1 = dx.x;
            dy.y = 1;
        }
    }

    /**
     * Coherence test with plain variable.
     *
     * The {@code trap} variable is used to force the read from memory and remove compiler's barrier on the {@link
     * NullPointerException} exceptions. This ensures that no NPE exceptions will be thrown later, enabling compiler to
     * perform read reorderings.
     *
     * https://github.com/openjdk/jcstress/blob/master/jcstress-samples/src/main/java/org/openjdk/jcstress/samples/jmm/basic/BasicJMM_05_Coherence.java#L85-L94
     * https://stackoverflow.com/questions/45259702/order-of-null-pointer-checking
     */
    @JCStressTest
    @Outcome(expect = ACCEPTABLE, desc = "Reads observer total order of writes on a single memory location.")
    @Outcome(id = "1, 2, 2, 1", expect = ACCEPTABLE_INTERESTING, desc = "Threads see different order of writes on single memory location!")
    @Outcome(id = "2, 1, 1, 2", expect = ACCEPTABLE_INTERESTING, desc = "Threads see different order of writes on single memory location!")
    @State
    public static class CoherencePlain {
        private final Holder h1 = new Holder();
        private final Holder h2 = h1;

        private static class Holder {
            int x;
            int trap;
        }

        @Actor
        public void actor1() {
            h1.x = 1;
        }

        @Actor
        public void actor2() {
            h1.x = 2;
        }

        @Actor
        public void actor3(final IIII_Result r) {
            final Holder h1 = this.h1;
            final Holder h2 = this.h2;
            h1.trap = 0;
            h2.trap = 0;
            r.r1 = h1.x;
            r.r2 = h2.x;
        }

        @Actor
        public void actor4(final IIII_Result r) {
            final Holder h1 = this.h1;
            final Holder h2 = this.h2;
            h1.trap = 0;
            h2.trap = 0;
            r.r3 = h1.x;
            r.r4 = h2.x;
        }
    }

    /**
     * Coherence test with volatile variable.
     */
    @JCStressTest
    @Outcome(expect = ACCEPTABLE, desc = "Reads observer same order of writes on a single memory location.")
    @Outcome(id = "1, 2, 2, 1", expect = FORBIDDEN, desc = "Threads see different order of writes on single memory location!")
    @Outcome(id = "2, 1, 1, 2", expect = FORBIDDEN, desc = "Threads see different order of writes on single memory location!")
    @State
    public static class CoherenceVolatile {
        private final Holder h1 = new Holder();
        private final Holder h2 = h1;

        private static class Holder {
            volatile int x;
            int trap;
        }

        @Actor
        public void actor1() {
            h1.x = 1;
        }

        @Actor
        public void actor2() {
            h1.x = 2;
        }

        @Actor
        public void actor3(final IIII_Result r) {
            final Holder h1 = this.h1;
            final Holder h2 = this.h2;
            h1.trap = 0;
            h2.trap = 0;
            r.r1 = h1.x;
            r.r2 = h2.x;
        }

        @Actor
        public void actor4(final IIII_Result r) {
            final Holder h1 = this.h1;
            final Holder h2 = this.h2;
            h1.trap = 0;
            h2.trap = 0;
            r.r3 = h1.x;
            r.r4 = h2.x;
        }
    }

    @JCStressTest
    @Outcome(id = "1, 0, 1, 0", expect = ACCEPTABLE_INTERESTING, desc = "Thread3 and thread4 see the writes in different order.")
    @Outcome(id = "0, 1, 0, 1", expect = ACCEPTABLE, desc = "First read operations, then writes, then second read operations.")
    @Outcome(                   expect = ACCEPTABLE, desc = "Rest are acceptable.")
    @State
    public static class IRIWPlain {
        private int x;
        private int y;

        @Actor
        public void actor1() {
            x = 1;
        }

        @Actor
        public void actor2() {
            y = 1;
        }

        @Actor
        public void actor3(final IIII_Result r) {
            r.r1 = x;
            r.r2 = y;
        }

        @Actor
        public void actor4(final IIII_Result r) {
            r.r3 = y;
            r.r4 = x;
        }
    }

    @JCStressTest
    @Outcome(id = "1, 0, 1, 0", expect = FORBIDDEN, desc = "Thread3 and thread4 see the writes in different order.")
    @Outcome(id = "0, 1, 0, 1", expect = ACCEPTABLE, desc = "First read operations, then writes, then second read operations.")
    @Outcome(                   expect = ACCEPTABLE, desc = "Rest are acceptable.")
    @State
    public static class IRIWVolatile {
        private volatile int x;
        private volatile int y;

        @Actor
        public void actor1() {
            x = 1;
        }

        @Actor
        public void actor2() {
            y = 1;
        }

        @Actor
        public void actor3(final IIII_Result r) {
            r.r1 = x;
            r.r2 = y;
        }

        @Actor
        public void actor4(final IIII_Result r) {
            r.r3 = y;
            r.r4 = x;
        }
    }

}
