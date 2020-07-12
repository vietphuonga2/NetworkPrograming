/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiclientserver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import olympia.Player;
import olympia.Quiz;
import olympia.QuizModel;
import org.apache.commons.lang3.time.StopWatch;

/**
 *
 * @author H
 */
public class test {

    public static void main(String[] args) throws InterruptedException {
        String str = "ASDKjsakdjas";
        long duration;
        Scanner sc = new Scanner(System.in);
        String test = "";
        String str2 = str.toLowerCase();
        System.out.println(str);
        long startTime, endTime;
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        stopwatch.suspend();
        TimeProcess timeprocess = new TimeProcess();
        timeprocess.start();
        //divide by 1000000 to get milliseconds.
    }
}

class TimeProcess extends Thread {

    private StopWatch stopwatch;

    public TimeProcess() {

    }

    @Override
    public void run() {
        try {
            stopwatch = new StopWatch();
            stopwatch.start();
            long check = 0;
            boolean flag = true;
            while (true) {
                if (flag == true) {
                    System.out.println(15-stopwatch.getTime() / 1000);
                    check = stopwatch.getTime()/1000;
                }
                if (check == (stopwatch.getTime()/1000)) {
                    flag = false;
                }else{
                    flag =true;
                }
                if (stopwatch.getTime() > 5000) {
                    System.out.println("time > 5000");
                    stopwatch.reset();
                }
                
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
