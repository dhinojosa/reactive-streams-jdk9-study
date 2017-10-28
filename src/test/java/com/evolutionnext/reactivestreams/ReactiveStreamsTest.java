package com.evolutionnext.reactivestreams;


import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

public class ReactiveStreamsTest {

    @Test
    public void testBasicStream() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        MyPublisher myPublisher = new MyPublisher(executorService);
        myPublisher.subscribe(new Flow.Subscriber<>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1000);
            }

            @Override
            public void onNext(Long item) {
                System.out.println("[1:" + Thread.currentThread().getName() + "] On Next: " + item);
                if (item >= 10) {
                    subscription.cancel();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("[1:" + Thread.currentThread().getName() + "] On Error: " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("[1:" + Thread.currentThread().getName() + "] On Complete: ");
            }
        });

        myPublisher.subscribe(new Flow.Subscriber<>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1000);
            }

            @Override
            public void onNext(Long item) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("[2:" + Thread.currentThread().getName() + "] On Next: " + item);
                if (item >= 100) {
                    subscription.cancel();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("[2:" + Thread.currentThread().getName() + "] On Error: " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("[2:" + Thread.currentThread().getName() + "] On Complete: ");
            }
        });
        Thread.sleep(10000);
    }
}
