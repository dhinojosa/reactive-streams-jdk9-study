package com.evolutionnext.rxjava;

import com.evolutionnext.other.TickerPriceFinder;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.reactivestreams.Subscription;

import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ObservableBasicCreationTest {


    @Test
    public void testManualObservable() throws InterruptedException {

    }


    @Test
    public void testMap() {
        Observable<Integer> integerObservable =
                Observable.just(1, 2, 3);

        Observable<String> stringObservable =
                integerObservable
                        .map(integer -> "Hello: " + integer);

        stringObservable
                .subscribe(System.out::println);
    }

    @Test
    public void testBasicFlatMap() throws InterruptedException {
        Observable<Integer> a =
                Observable.just(1, 2, 3);
        Observable<Integer> b =
                a.flatMap(x ->
                        Observable.just(x - 1, x, x + 1));
        b.subscribe(System.out::println);
        System.out.println("-----------");
        Thread.sleep(2000);
        b.map(x -> "Hello:" + x).repeat(4).subscribe(System.out::println);
        Thread.sleep(2000);
    }

    @Test
    public void testFromWithFuture() throws InterruptedException {
        ExecutorService executorService =
                Executors.newCachedThreadPool();

        Future<Integer> future = executorService.submit(
                () -> {
                    System.out.println
                            ("Thread name in future" +
                                    Thread.currentThread().getName());
                    Thread.sleep(1000);
                    return 19;
                });

        Observable<Integer> observable = Observable.fromFuture(future);

        observable.map(x -> x + 30)
                  .doOnNext(x ->
                          System.out.println(Thread.currentThread().getName()))
                  .repeat(5)
                  .subscribe(System.out::println);

        System.out.println(Thread.currentThread().getName());

        observable.flatMap(x -> Observable.just(x + 40, x + 50))
                  .subscribe(System.out::println);

        Thread.sleep(15000);
    }

    @Test
    public void testInterval() throws InterruptedException {
        Observable<String> interval =
                Observable.interval
                        (1, TimeUnit.SECONDS)
                          .map(Long::toHexString);

        interval.doOnNext(x -> System.out.println(Thread.currentThread().getName()))
                .subscribe(lng ->
                        System.out.println("1: lng = " + lng));

        Thread.sleep(5000);
        interval.doOnNext(x -> System.out.println(Thread.currentThread().getName()))
                .subscribe(lng ->
                System.out.println("2: lng = " + lng));

        Thread.sleep(10000);
    }

    /**
     * Defer will delay any emission of items until an Observer subscribes
     *
     * @throws InterruptedException
     */
    @Test
    public void testDefer() throws InterruptedException {
        Observable<LocalTime> localTimeObservable =
                Observable.defer(
                        () -> Observable
                                .just(LocalTime.now()))
                          .repeat(3);
        localTimeObservable.subscribe(System.out::println);
        Thread.sleep(3000);
        System.out.println("Next Subscriber");
        localTimeObservable.subscribe(System.out::println);
    }


    @Test
    public void testRange() throws InterruptedException {
        Observable<Integer> rangeObservable =
                Observable.range(10, 20);

        rangeObservable.subscribe(System.out::println);

        Thread.sleep(3000);

        System.out.println("-------------");
        System.out.println("Next Subscriber");
        System.out.println("-------------");

        rangeObservable
                .subscribe(System.out::println);
    }


    @Test
    public void testTicker() throws InterruptedException {
        String[] ticker = {"MSFT", "GOOG", "YHOO", "APPL"};
        Observable<String> stockObservable =
                Observable.fromArray(ticker);
        TickerPriceFinder tickerPriceFinder =
                TickerPriceFinder.create();
        stockObservable
                .flatMap(s ->
                        Observable.fromFuture
                                (tickerPriceFinder.getPrice(s)))
                .subscribe(System.out::println);
    }
}
