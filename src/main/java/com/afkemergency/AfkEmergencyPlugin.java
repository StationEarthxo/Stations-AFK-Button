package com.afkemergency;

import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
    name = "Station's AFK Button",
    description = "Flashes a big red attention button when a sustained skilling action stops",
    tags = {"afk", "idle", "notification", "skilling", "accessibility", "adhd"}
)
public class AfkEmergencyPlugin extends Plugin
{
    static final long CELEBRATION_MILLIS = 240L;

    @Inject private Client client;
    @Inject private AfkEmergencyConfig config;
    @Inject private AfkEmergencyOverlay overlay;
    @Inject private AfkEmergencyDimOverlay dimOverlay;
    @Inject private OverlayManager overlayManager;
    @Inject private MouseManager mouseManager;
    @Inject private KeyManager keyManager;
    @Inject private EmergencySoundPlayer soundPlayer;

    private final AttentionState state = new AttentionState();
    private final SkillingActivityTracker skillingActivity = new SkillingActivityTracker();
    private final MouseListener mouseListener = new MouseListener()
    {
        @Override
        public MouseEvent mousePressed(MouseEvent event)
        {
            if (state.isAlerting() && (config.dismissAnywhere() || overlay.contains(event.getPoint())))
            {
                event.consume();
                state.dismiss(System.currentTimeMillis());
                skillingActivity.clearAnimation();
                if (config.pressSound())
                {
                    soundPlayer.playPress(config.soundVolume());
                }
            }
            return event;
        }

        @Override public MouseEvent mouseReleased(MouseEvent event) { return event; }
        @Override public MouseEvent mouseClicked(MouseEvent event) { return event; }
        @Override public MouseEvent mouseEntered(MouseEvent event) { return event; }
        @Override public MouseEvent mouseExited(MouseEvent event) { return event; }
        @Override public MouseEvent mouseDragged(MouseEvent event) { return event; }
        @Override public MouseEvent mouseMoved(MouseEvent event) { return event; }
    };
    private final KeyListener keyListener = new KeyListener()
    {
        @Override
        public void keyPressed(KeyEvent event)
        {
            if (config.previewHotkey().matches(event) && !state.isAlerting())
            {
                state.showAlert();
                playAlertSound();
            }
        }

        @Override public void keyReleased(KeyEvent event) { }
        @Override public void keyTyped(KeyEvent event) { }
    };

    @Provides
    AfkEmergencyConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AfkEmergencyConfig.class);
    }

    @Override
    protected void startUp()
    {
        state.reset();
        skillingActivity.reset();
        soundPlayer.startUp();
        overlayManager.add(dimOverlay);
        overlayManager.add(overlay);
        mouseManager.registerMouseListener(mouseListener);
        keyManager.registerKeyListener(keyListener);
    }

    @Override
    protected void shutDown()
    {
        keyManager.unregisterKeyListener(keyListener);
        mouseManager.unregisterMouseListener(mouseListener);
        overlayManager.remove(overlay);
        overlayManager.remove(dimOverlay);
        soundPlayer.shutDown();
        state.reset();
        skillingActivity.reset();
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            state.reset();
            skillingActivity.reset();
            return;
        }

        Player player = client.getLocalPlayer();
        if (player == null)
        {
            state.reset();
            skillingActivity.reset();
            return;
        }

        long now = System.currentTimeMillis();
        int animation = player.getAnimation();
        boolean motherlodeMining = SkillingActivityTracker.isMotherlodeMiningAnimation(animation);
        boolean animating = motherlodeMining || skillingActivity.isTrackedAnimation(animation, now);
        boolean moving = player.getPoseAnimation() != player.getIdlePoseAnimation();
        if (animation != -1 && !animating)
        {
            state.update(now, false, true,
                config.minimumActivitySeconds() * 1000L,
                config.idleDelaySeconds() * 1000L);
            skillingActivity.clearAnimation();
            return;
        }
        boolean alertStarted = state.update(
            now,
            animating,
            moving,
            motherlodeMining ? 0L : config.minimumActivitySeconds() * 1000L,
            config.idleDelaySeconds() * 1000L
        );
        if (alertStarted)
        {
            playAlertSound();
        }
        if (moving)
        {
            skillingActivity.clearAnimation();
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged event)
    {
        Player player = client.getLocalPlayer();
        int animation = player == null ? -1 : player.getAnimation();
        skillingActivity.onExperience(
            event.getSkill(), event.getXp(), animation, System.currentTimeMillis());
    }

    boolean isAlerting()
    {
        return state.isAlerting();
    }

    boolean isCelebrating(long now)
    {
        return state.isCelebrating(now, CELEBRATION_MILLIS);
    }

    long getCelebrationStartedAt()
    {
        return state.getCelebrationStartedAt();
    }

    private void playAlertSound()
    {
        if (config.alertSound())
        {
            soundPlayer.playAlert(config.soundVolume());
        }
    }
}
