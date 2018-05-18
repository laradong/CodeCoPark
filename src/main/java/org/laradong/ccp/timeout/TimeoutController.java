package org.laradong.ccp.timeout;

import java.util.concurrent.*;

/**
 * Created by Lara on 2017/7/19.
 */
public class TimeoutController {
    public static void main(String[] args) {
        System.out.println("1");
        try {
            runWithTimeout();
            System.out.println("2");
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("3");
    }

    public static void runWithTimeout() throws TimeoutException, ExecutionException {
        FutureTask<Object> task = new FutureTask<Object>(new Callable<Object>() {

            public Object call() throws Exception {
                Thread.sleep(1500);
                return null;
            }

        });
        try {
            Object result = task.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
