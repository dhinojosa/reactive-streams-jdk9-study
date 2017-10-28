package com.evolutionnext.reactivestreams;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MyPublisher implements Flow.Publisher<Long> {
    private final ExecutorService executorService;

    public MyPublisher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Long> subscriber) {
        AtomicBoolean atomicDone = new AtomicBoolean(false);
        AtomicLong atomicCount = new AtomicLong(0);

        subscriber.onSubscribe(new Flow.Subscription() {
            @Override
            public void request(long n) {
                if (n < 0) subscriber.onError(
                        new Throwable("Cannot request less than 0"));
                getMore(n);
            }

            private void getMore(long items) {
                executorService.submit(() -> {
                    long goal = atomicCount.get() + items;
                    while(atomicCount.get() < goal && !atomicDone.get()) {
                        subscriber.onNext(atomicCount.getAndAdd(1));
                    }
                });
            }

            @Override
            public void cancel() {
                atomicDone.set(true);
            }
        });
    }
}
