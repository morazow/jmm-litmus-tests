package com.morazow.jmm;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.III_Result;

@JCStressTest
@State
// @Outcome(id = { "0, 0, 0", "1, 1, 1" }, expect = ACCEPTABLE, desc = "Boring")
// @Outcome(id = { "0, .*, 1", "1, .*, 1" }, expect = ACCEPTABLE, desc = "Irrelevant")
// @Outcome(id = "1, 0, 0", expect = ACCEPTABLE_INTERESTING, desc = "Whoa")
// @Outcome(id = "0, 0, 1", expect = FORBIDDEN, desc = "Reordering is not allowed.")
// @Outcome(id = "1, 0, 1", expect = FORBIDDEN, desc = "Reordering is not allowed.")
// @Outcome(id = "0, 1, 0", expect = FORBIDDEN, desc = "Reordering is not allowed.")
// @Outcome(id = "1, 1, 0", expect = FORBIDDEN, desc = "Reordering is not allowed.")
// @Outcome(id = "1, 0, 1", expect = FORBIDDEN, desc = "Reordering is not allowed.")
@Outcome(id = { ".*, .*, .*" }, expect = ACCEPTABLE, desc = "Irrelevant")
public class RoachMotelOrderingTests {
    int x, y;
    volatile int v;

    @Actor
    void thread1() {
        x = 1;
        v = 1;
        y = 1;
    }

    @Actor
    void thread2(III_Result r) {
        r.r1 = x;
        r.r2 = v;
        r.r3 = y;
    }
}
