//=============================================================================
/**
 * @file MultiTaskTimeCalculator.java
 */
//=============================================================================
package space.network.cleancloud;

public interface MultiTaskTimeCalculator {

    class TimeData {
        public int  mTaskId;
        public long mStartTime;
        public long mEndTime;
    }

     boolean setTimeDurationThreshold(long threshold);

     TimeData taskStart();

     long taskEnd(TimeData startData);

     boolean isDurationOverThreshold();

     long getTaskTimeDuration();

     void resetStatus();
}