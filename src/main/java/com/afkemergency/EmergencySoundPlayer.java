package com.afkemergency;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.audio.AudioPlayer;

@Singleton
final class EmergencySoundPlayer
{
    private static final float SAMPLE_RATE = 44_100F;
    private static final short[] ALERT = createAlert();
    private static final short[] PRESS = createPress();

    private final AudioPlayer audioPlayer;
    private ExecutorService executor;

    @Inject
    private EmergencySoundPlayer(AudioPlayer audioPlayer)
    {
        this.audioPlayer = audioPlayer;
    }

    synchronized void startUp()
    {
        if (executor == null || executor.isShutdown())
        {
            executor = Executors.newSingleThreadExecutor(task ->
            {
                Thread thread = new Thread(task, "afk-emergency-sound");
                thread.setDaemon(true);
                return thread;
            });
        }
    }

    synchronized void shutDown()
    {
        if (executor != null)
        {
            executor.shutdownNow();
            executor = null;
        }
    }

    void playAlert(int volume)
    {
        play(ALERT, volume);
    }

    void playPress(int volume)
    {
        play(PRESS, volume);
    }

    private synchronized void play(short[] samples, int volume)
    {
        if (executor == null || volume <= 0)
        {
            return;
        }

        int clamped = Math.min(100, volume);
        double gain = Math.pow(clamped / 100.0, 1.45);
        byte[] pcm = toPcm(samples, gain);
        try
        {
            executor.execute(() -> output(toWave(pcm)));
        }
        catch (RejectedExecutionException ignored)
        {
            // The plugin was disabled between scheduling and playback.
        }
    }

    private void output(byte[] wave)
    {
        try (ByteArrayInputStream input = new ByteArrayInputStream(wave))
        {
            audioPlayer.play(input, 0F);
        }
        catch (Exception ignored)
        {
            // Audio is optional; a missing output device must never affect the alert.
        }
    }

    static byte[] toWave(byte[] pcm)
    {
        ByteBuffer wave = ByteBuffer.allocate(44 + pcm.length).order(ByteOrder.LITTLE_ENDIAN);
        wave.put("RIFF".getBytes(StandardCharsets.US_ASCII));
        wave.putInt(36 + pcm.length);
        wave.put("WAVE".getBytes(StandardCharsets.US_ASCII));
        wave.put("fmt ".getBytes(StandardCharsets.US_ASCII));
        wave.putInt(16);
        wave.putShort((short) 1);
        wave.putShort((short) 1);
        wave.putInt((int) SAMPLE_RATE);
        wave.putInt((int) SAMPLE_RATE * 2);
        wave.putShort((short) 2);
        wave.putShort((short) 16);
        wave.put("data".getBytes(StandardCharsets.US_ASCII));
        wave.putInt(pcm.length);
        wave.put(pcm);
        return wave.array();
    }

    static byte[] toPcm(short[] samples, double gain)
    {
        byte[] pcm = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++)
        {
            int value = (int) Math.round(samples[i] * gain);
            value = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, value));
            pcm[i * 2] = (byte) value;
            pcm[i * 2 + 1] = (byte) (value >>> 8);
        }
        return pcm;
    }

    private static short[] createAlert()
    {
        int count = millisecondsToSamples(310);
        short[] samples = new short[count];
        for (int i = 0; i < count; i++)
        {
            double time = i / SAMPLE_RATE;
            double attack = Math.min(1.0, time / 0.025);
            double release = Math.pow(Math.max(0.0, 1.0 - time / 0.31), 1.7);
            double first = Math.sin(2.0 * Math.PI * 523.25 * time);
            double second = Math.sin(2.0 * Math.PI * 659.25 * time);
            double warmth = Math.sin(2.0 * Math.PI * 261.63 * time);
            samples[i] = asSample((first * 0.42 + second * 0.38 + warmth * 0.20)
                * attack * release * 0.32);
        }
        return samples;
    }

    private static short[] createPress()
    {
        int count = millisecondsToSamples(52);
        short[] samples = new short[count];
        long noise = 0x51F15EEDL;
        double previousNoise = 0.0;
        for (int i = 0; i < count; i++)
        {
            double time = i / SAMPLE_RATE;
            noise = (noise * 1_103_515_245L + 12_345L) & 0x7fffffffL;
            double texture = (noise / (double) 0x7fffffffL) * 2.0 - 1.0;
            double highPassedNoise = texture - previousNoise;
            previousNoise = texture;

            double contact = highPassedNoise * Math.exp(-time * 520.0);
            double shell = (Math.sin(2.0 * Math.PI * 2_350.0 * time) * 0.58
                + Math.sin(2.0 * Math.PI * 3_700.0 * time) * 0.42)
                * Math.exp(-time * 260.0);

            double releaseTime = time - 0.024;
            double releaseClick = releaseTime >= 0.0
                ? (Math.sin(2.0 * Math.PI * 2_050.0 * releaseTime) * 0.65
                    + Math.sin(2.0 * Math.PI * 3_250.0 * releaseTime) * 0.35)
                    * Math.exp(-releaseTime * 330.0)
                : 0.0;

            samples[i] = asSample((contact * 0.48 + shell * 0.72
                + releaseClick * 0.34) * 0.46);
        }
        return samples;
    }

    private static int millisecondsToSamples(int milliseconds)
    {
        return Math.round(SAMPLE_RATE * milliseconds / 1000F);
    }

    private static short asSample(double value)
    {
        return (short) Math.round(Math.max(-1.0, Math.min(1.0, value)) * Short.MAX_VALUE);
    }
}