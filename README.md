# jmm-litmus-tests

[![Build Status](https://github.com/morazow/jmm-litmus-tests/actions/workflows/ci.yml/badge.svg)](https://github.com/morazow/jmm-litmus-tests/actions/workflows/ci.yml)

Java Memory Model Litmus Tests.

This repository contains JCStress tests for `Understanding Java Memory Model (JMM)` blog post.

## Atomicity Tests

[`AtomicityTest.java`](src/main/java/com/morazow/jmm/AtomicityTest.java) to check Java `atomicity` mechanisms.

- Plain variables
- Volatile variables
- Synchronized
- AtomicInteger

## Java Memory Model (JMM) Litmus Tests

[`LitmusTests.java`](src/main/java/com/morazow/jmm/LitmusTests.java) to evaluate Java JMM guarantees.

- Message Passing
- Store Buffering
- Load Buffering
- Coherence
- Independent Reads of Independent Writes (IRIW)

## References

The official [JCStress](https://github.com/openjdk/jcstress) framework includes [many more samples](https://github.com/openjdk/jcstress/tree/master/jcstress-samples), for further information please refer to it.

## License

[MIT License (MIT)](LICENSE)
