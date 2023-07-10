package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    public static Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final int LENGTH = 100;
    public static final int AMOUNT_OF_THREADS = 1000;
    public static final String LETTERS = "RLRFR";

    public static void main(String[] args) {
        Thread[] threads = new Thread[AMOUNT_OF_THREADS];
        Thread printer = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    printLeader();
                }
            }
        });
        printer.start();

        for (int i = 0; i < AMOUNT_OF_THREADS; i++) {
            threads[i] = getThread();

        }
        for (Thread thread : threads) {
            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        printer.interrupt();
    }

    public static String generateRoute(String letters, int length) {

        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }

        return route.toString();
    }

    public static Thread getThread() {
        return new Thread(() -> {
            int count = 0;
            int frequency = 0;
            String route = generateRoute(LETTERS, LENGTH);
            for (int i = 0; i < route.toCharArray().length; i++) {
                if (route.toCharArray()[i] == 'R') {
                    count++;
                } else {
                    if (count > 0) {
                        frequency++;
                    }
                    count = 0;
                }
            }
            if (route.toCharArray()[route.toCharArray().length - 1] == 'R') frequency++;
            synchronized (sizeToFreq) {
                if (sizeToFreq.containsKey(frequency)) {
                    sizeToFreq.put(frequency, sizeToFreq.get(frequency) + 1);
                } else {
                    sizeToFreq.put(frequency, 1);
                }
                sizeToFreq.notify();
            }
        });
    }

    public static void printLeader() {
        Map.Entry<Integer, Integer> max = sizeToFreq
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get();
        System.out.println("Текущий лидер " + max.getKey()
                + " (встретилось " + max.getValue() + " раз)");
    }

}
