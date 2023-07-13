package com.example.assignment3.Tracker;

import junit.framework.TestCase;

public class TrackerTest extends TestCase {

    Tracker tracker = new Tracker();

    public void testGetDistance() {
        tracker.totalDistanceTravel= 0.0F;
        assertEquals(0.0F,tracker.getDistance());
    }


    public void testTimerLogicMinutes() {
        assertEquals("0:01:00",tracker.runTimerTest(60));
    }
    public void testTimerLogicSeconds() {
        assertEquals("0:00:01",tracker.runTimerTest(1));
    }
    public void testTimerLogicHours() {
        assertEquals("1:00:00",tracker.runTimerTest(3600));
    }

    public void testMaxSpeed() {
        tracker.maxSpeed(5);
        tracker.maxSpeed(10);
        assertEquals(10F,tracker.getMaxSpeed());
    }
    public void testGetCurrentStateWalking() {
        Tracker.Mode mode = tracker.testDynamicLogic(3);
        assertEquals(Tracker.Mode.WALKING,mode);
    }
    public void testGetCurrentStateStanding() {
        Tracker.Mode mode = tracker.testDynamicLogic(1);
        assertEquals(Tracker.Mode.STANDING,mode);
    }
    public void testGetCurrentStateRunning() {
        Tracker.Mode mode = tracker.testDynamicLogic(6);
        assertEquals(Tracker.Mode.RUNNING,mode);
    }
    public void testGetCurrentStateCycling() {
        Tracker.Mode mode = tracker.testDynamicLogic(8);
        assertEquals(Tracker.Mode.CYCLING,mode);
    }
    public void testGetCurrentStateDriving() {
        Tracker.Mode mode = tracker.testDynamicLogic(15);
        assertEquals(Tracker.Mode.DRIVING,mode);
    }
}