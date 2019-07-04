package it.polimi.se2019.model;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Game timer class that supports pause and resume
 */
public class GameTimer {

    private volatile boolean isRunning = false;
    private long interval;
    private long elapsedTime;
    private long duration;
    private ScheduledExecutorService execService = Executors
            .newSingleThreadScheduledExecutor();
    private Future<?> future = null;
    private Board board;
    private GameCharacter character;
    private int pauseCounter;


    /**
     * Constructor, it builds a game timer
     * @param interval between each tick in millis
     * @param duration in millis for which the timer should run
     * @param board board from which to end the turn
     * @param character currently playing
     */
    GameTimer(long interval, long duration, Board board, GameCharacter character) {
        this.interval = interval;
        this.duration = duration;
        this.elapsedTime = 0;
        this.board = board;
        this.character = character;
    }

    /**
     * Starts the timer
     */
    public void start() {
        if (this.isRunning)
            return;

        this.isRunning = true;
        this.future = this.execService.scheduleWithFixedDelay(() -> {
                GameTimer.this.elapsedTime += GameTimer.this.interval;
                if (GameTimer.this.duration > 0 && GameTimer.this.elapsedTime >= GameTimer.this.duration) {
                    onFinish();
                    GameTimer.this.future.cancel(false);
                }
        }, 0, this.interval, TimeUnit.MILLISECONDS);
    }

    /**
     * Pause the timer
     */
    void pause() {
        this.pauseCounter++;
        if(!this.isRunning) {
            return;
        }
        this.future.cancel(false);
        this.isRunning = false;
    }

    /**
     * Resumes the timer
     */
    void resume() {
        this.pauseCounter--;
        if (this.pauseCounter == 0) {
            this.start();
        }
    }

    /**
     * End player turn at the end of the timer
     */
    private void onFinish() {
        this.board.endTurn(this.character);
    }

    /**
     * Stops the timer
     */
    void cancel() {
        pause();
        this.elapsedTime = 0;
    }

}
