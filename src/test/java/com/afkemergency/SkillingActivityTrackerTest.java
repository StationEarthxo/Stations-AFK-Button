package com.afkemergency;

import net.runelite.api.Skill;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SkillingActivityTrackerTest
{
    @Test
    public void requiresSupportedExperienceGainAndMatchingAnimation()
    {
        SkillingActivityTracker tracker = new SkillingActivityTracker();
        tracker.onExperience(Skill.FISHING, 1_000, 621, 0);
        assertFalse(tracker.isTrackedAnimation(621, 0));

        tracker.onExperience(Skill.FISHING, 1_010, 621, 1_000);
        assertTrue(tracker.isTrackedAnimation(621, 1_000));
        assertFalse(tracker.isTrackedAnimation(4_840, 1_000));
    }

    @Test
    public void magicTeleportExperienceCannotArmReminder()
    {
        SkillingActivityTracker tracker = new SkillingActivityTracker();
        tracker.onExperience(Skill.MAGIC, 2_000, 4_840, 0);
        tracker.onExperience(Skill.MAGIC, 2_035, 4_840, 1_000);
        assertFalse(tracker.isTrackedAnimation(4_840, 1_000));
    }
}
