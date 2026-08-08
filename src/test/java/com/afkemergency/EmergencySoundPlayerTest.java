package com.afkemergency;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class EmergencySoundPlayerTest
{
    @Test
    public void pcmConversionAppliesVolumeWithoutChangingLength()
    {
        short[] samples = {0, 1000, -1000, Short.MAX_VALUE};

        byte[] silent = EmergencySoundPlayer.toPcm(samples, 0.0);
        byte[] half = EmergencySoundPlayer.toPcm(samples, 0.5);

        assertEquals(samples.length * 2, half.length);
        assertArrayEquals(new byte[samples.length * 2], silent);
        assertEquals(500, decode(half, 1));
        assertEquals(-500, decode(half, 2));
    }

    private static short decode(byte[] pcm, int index)
    {
        return (short) ((pcm[index * 2] & 0xff) | (pcm[index * 2 + 1] << 8));
    }
}