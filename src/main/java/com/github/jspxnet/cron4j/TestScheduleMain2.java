package com.github.jspxnet.cron4j;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/7/6 0:39
 * @description: cron4j
 **/
public class TestScheduleMain2 {
    public static void main(String[] args) {

        // Creates a Scheduler instance.
        Scheduler s = new Scheduler();
        // Schedule a once-a-minute task.

        s.schedule("* * * * *", new Runnable() {
            int i = 0;
            @Override
            public void run() {
                i++;
                System.out.println("1 Another minute ticked away..."+System.currentTimeMillis()/1000);
            }
        });
        // Starts the scheduler.
        s.start();
        // Will run for ten minutes.
        try {
            Thread.sleep(1000L * 60L * 10L);
        } catch (InterruptedException e) {
            ;
        }
        // Stops the scheduler.
        s.stop();

    }
}
