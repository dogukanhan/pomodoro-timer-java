package com.dogukanhan.pomodoro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class Pomodoro {

    interface TimeChangeListener {
        void onTimeChange(int current);
    }

    interface StateChangeListener {
        void onStateChange(State oldState, State newState);
    }

    public enum State {
        WORK, SHORT_BREAK, LONG_BREAK, STOP
    }

    private long workTime;
    private long shortBreakTime;
    private long longBreakTime;
    private int stepCountForLongBreak;
    private int currentCountForLongBreak;
    private State state = State.STOP;
    private State pausedState = State.WORK;
    private Timer timer;
    private int passedSeconds;

    private TimeChangeListener timeChangeListener;
    private StateChangeListener stateChangeListener;

    public Pomodoro(int workTimeInSeconds, int shortBreakInSeconds, int longBreakInSeconds, int stepCountForLongBreak) {
        this.workTime = workTimeInSeconds;
        this.shortBreakTime = shortBreakInSeconds;
        this.longBreakTime = longBreakInSeconds;
        this.stepCountForLongBreak = stepCountForLongBreak;
        this.timer = new Timer();
    }

    @SuppressWarnings("ConstantConditions")
    public Pomodoro() {

        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            this.workTime = (format.parse("25:00").getTime() / 1000);
            this.shortBreakTime = (format.parse("05:00").getTime() / 1000);
            this.longBreakTime = (format.parse("15:00").getTime() / 1000);
            this.stepCountForLongBreak = 4;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.timer = new Timer();
    }

    private long getLimitTime() {
        switch (state) {
            case WORK:
                return workTime;
            case SHORT_BREAK:
                return shortBreakTime;
            case LONG_BREAK:
                return longBreakTime;
            default: {
                throw new IllegalCallerException("getLimitTime can't be called.");
            }
        }
    }

    private TimerTask pomodoroTask() {
        return new TimerTask() {
            @Override
            public void run() {
                passedSeconds++;

                if (timeChangeListener != null) {
                    timeChangeListener.onTimeChange(passedSeconds);
                }

                if (passedSeconds >= getLimitTime()) {
                    callInterrupt();
                }
            }
        };
    }

    private void callInterrupt() {
        timer.purge();
        timer.cancel();
        timer = new Timer();
        startNextState();
    }

    private void startNextState() {
        passedSeconds = 0;
        State oldState = state;

        if (state == null) {
            state = State.WORK;
        } else if (state == State.WORK) {
            if (currentCountForLongBreak >= stepCountForLongBreak) {
                state = State.LONG_BREAK;
            } else {
                currentCountForLongBreak++;
                state = State.SHORT_BREAK;
            }
        } else if (state == State.SHORT_BREAK) {
            state = State.WORK;
        } else if (state == State.LONG_BREAK) {
            state = State.WORK;
            stepCountForLongBreak = 0;
        } else if (state == State.STOP) {
            state = pausedState;
        }

        if (stateChangeListener != null) {
            stateChangeListener.onStateChange(oldState, state);
        }

        timer.scheduleAtFixedRate(pomodoroTask(), 0, 1000);
    }

    public void start() {
        if (state != State.STOP)
            throw new IllegalStateException("start called more than ones!");

        startNextState();
    }

    public void stop() {
        if (state == State.STOP) {
            throw new IllegalStateException("stop called more than ones!");
        } else {
            pausedState = state;
            state = State.STOP;
        }
    }

    public void setTimeChangeListener(TimeChangeListener timeChangeListener) {
        this.timeChangeListener = timeChangeListener;
    }

    public void setStateChangeListener(StateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }
}
