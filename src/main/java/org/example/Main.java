package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    public static Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final Object lock = new Object();

    public static void main(String[] args) {
        Thread[] threads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(() -> System.out.println(generateRoute("RLRFR", 100)));
            threads[i].start();

        }
        for (Thread thread : threads
        ) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new Thread(() -> {
            synchronized (lock) {
                for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
                    int key = entry.getKey();
                    int value = entry.getValue();
                    System.out.println("- " + key + " повторений " + value + " раз");
                }
            }

        }).start();
    }

    public static String generateRoute(String letters, int length) {
        int count = 0;
        int maxCount = 0;
        int frequency = 0;
        int times = 1;
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        for (int i = 0; i < route.toString().toCharArray().length; i++) {
            if (route.toString().toCharArray()[i] == 'R') {
                count++;
                maxCount++;
            } else {
                if (count > 0) {
                    frequency++;
                }
                count = 0;
            }
        }
        if (route.toString().toCharArray()[route.toString().toCharArray().length - 1] == 'R') frequency++;
        System.out.println("'R' symbols quantity is " + maxCount + " " + frequency + " times");
        synchronized (lock) {
            for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
                int key = entry.getKey();
                if (maxCount != key) {
                    sizeToFreq.put(key, 1);
                } else {
                    times++;
                    sizeToFreq.put(key, times);
                }
            }
        }
        return route.toString();
    }
}
