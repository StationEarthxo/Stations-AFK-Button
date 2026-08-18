package com.afkemergency;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttentionStateTest
{
    @Test
    public void sustainedActivityThenIdleTriggersAlert()
    {
        AttentionState state = new AttentionState();
        assertFalse(state.update(0, true, false, 3_000, 2_000));
        assertFalse(state.update(3_000, true, false, 3_000, 2_000));
        assertFalse(state.update(3_500, false, false, 3_000, 2_000));
        assertTrue(state.update(5_500, false, false, 3_000, 2_000));
        assertTrue(state.isAlerting());
    }

    @Test
    public void shortAnimationDoesNotArmAlert()
    {
        AttentionState state = new AttentionState();
        state.update(0, true, false, 3_000, 1_000);
        state.update(1_000, false, false, 3_000, 1_000);
        assertFalse(state.update(5_000, false, false, 3_000, 1_000));
    }

    @Test
    public void trustedMotherlodeSwingCanArmImmediately()
    {
        AttentionState state = new AttentionState();
        assertFalse(state.update(0, true, false, 0, 2_000));
        assertFalse(state.update(500, false, false, 3_000, 2_000));
        assertTrue(state.update(2_500, false, false, 3_000, 2_000));
    }

    @Test
    public void movementDisarmsPendingAlert()
    {
        AttentionState state = new AttentionState();
        state.update(0, true, false, 3_000, 1_000);
        state.update(3_000, true, false, 3_000, 1_000);
        state.update(3_500, false, true, 3_000, 1_000);
        state.update(4_000, false, false, 3_000, 1_000);
        assertFalse(state.update(8_000, false, false, 3_000, 1_000));
    }

    @Test
    public void clickStartsAndCompletesCelebration()
    {
        AttentionState state = new AttentionState();
        state.showAlert();
        state.dismiss(10_000);
        assertFalse(state.isAlerting());
        assertTrue(state.isCelebrating(10_300, 650));
        assertFalse(state.isCelebrating(10_651, 650));
    }
}
