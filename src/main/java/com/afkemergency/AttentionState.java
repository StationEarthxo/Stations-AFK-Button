package com.afkemergency;

final class AttentionState
{
    private long activityStartedAt = -1L;
    private long idleStartedAt = -1L;
    private boolean armed;
    private boolean alerting;
    private long celebrationStartedAt = -1L;

    boolean update(long now, boolean animating, boolean moving, long minimumActivityMillis, long idleDelayMillis)
    {
        if (alerting)
        {
            return false;
        }

        if (animating)
        {
            if (activityStartedAt < 0L)
            {
                activityStartedAt = now;
            }
            if (now - activityStartedAt >= minimumActivityMillis)
            {
                armed = true;
            }
            idleStartedAt = -1L;
            return false;
        }

        activityStartedAt = -1L;
        if (!armed || moving)
        {
            if (moving)
            {
                armed = false;
            }
            idleStartedAt = -1L;
            return false;
        }

        if (idleStartedAt < 0L)
        {
            idleStartedAt = now;
        }

        if (now - idleStartedAt >= idleDelayMillis)
        {
            alerting = true;
            armed = false;
            idleStartedAt = -1L;
            return true;
        }
        return false;
    }

    void showAlert()
    {
        alerting = true;
        celebrationStartedAt = -1L;
    }

    void dismiss(long now)
    {
        if (alerting)
        {
            alerting = false;
            celebrationStartedAt = now;
        }
        armed = false;
        activityStartedAt = -1L;
        idleStartedAt = -1L;
    }

    void reset()
    {
        activityStartedAt = -1L;
        idleStartedAt = -1L;
        celebrationStartedAt = -1L;
        armed = false;
        alerting = false;
    }

    boolean isAlerting()
    {
        return alerting;
    }

    boolean isCelebrating(long now, long durationMillis)
    {
        if (celebrationStartedAt < 0L)
        {
            return false;
        }
        if (now - celebrationStartedAt >= durationMillis)
        {
            celebrationStartedAt = -1L;
            return false;
        }
        return true;
    }

    long getCelebrationStartedAt()
    {
        return celebrationStartedAt;
    }
}
