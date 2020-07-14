package com.dogukanhan.pomodoro;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Test {

    public static void main(String[] args) {

        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        Pomodoro pomodoro = new Pomodoro(10, 2, 5, 2);

        pomodoro.setTimeChangeListener(current -> System.out.println("Current Time= " + format.format(new Date(current * 1000))));
        pomodoro.setStateChangeListener((oldState, newState) -> System.out.println(oldState + " changed to " + newState));
        pomodoro.start();
    }
}
