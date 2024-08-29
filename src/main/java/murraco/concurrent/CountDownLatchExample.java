package murraco.concurrent;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {

    public static void main(String[] args) {
        // 创建一个 CountDownLatch，计数值为3
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 创建并启动三个工作线程
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Worker(latch)).start();
        }

        try {
            // 主线程等待，直到 CountDownLatch 计数减为0
            System.out.println("主线程等待所有工作线程完成...");
            latch.await();
            System.out.println("所有工作线程已完成，主线程继续执行。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Worker 类实现了 Runnable 接口，表示一个工作线程
class Worker implements Runnable {
    private CountDownLatch latch;

    public Worker(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            // 模拟任务执行，线程休眠一段时间
            System.out.println( new Date() + " " + Thread.currentThread().getName() + " 正在执行任务...");
            Thread.sleep((long) (3000));
            System.out.println( new Date() + " " +  Thread.currentThread().getName() + " 任务完成。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 每个工作线程在完成任务后将 CountDownLatch 的计数减1
            latch.countDown();
        }
    }
}
