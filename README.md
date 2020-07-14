# pomodoro-timer-java
Simple pomodoro timer in java.

Use constructer to create new instance.


        Pomodoro pomodoro = new Pomodoro(10, 2, 5, 2); // 10 seconds, 2 seconds, 5 seconds, 2 loop;
                            new Pomodor(); // Or defaults 25min 5min 15min 4 loop.


Set Listeners to handle time and state change


      // Everysecond this method called with the current second.
      pomodoro.setTimeChangeListener(current -> System.out.println("Current Time= " + format.format(new Date(current * 1000))));
      
      // When the state changed for example work to short break this listener called.
      pomodoro.setStateChangeListener((oldState, newState) -> System.out.println(oldState + " changed to " + newState));
   
   



Call start() method on instance.

      pomodoro.start();



