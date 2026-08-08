package com.afkemergency;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

final class AfkEmergencyOverlay extends Overlay
{
    private static final int PREFERRED_WIDTH = 360;
    private static final int PREFERRED_HEIGHT = 380;
    private static final int LABEL_HEIGHT = 30;
    private static final int SOURCE_WIDTH = 240;
    private static final int SOURCE_HEIGHT = 230;
    private static final BufferedImage[] BUTTON_FRAMES = loadFrames();

    private final AfkEmergencyPlugin plugin;

    @Inject
    private AfkEmergencyOverlay(AfkEmergencyPlugin plugin)
    {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_CENTER);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGHEST);
        setResizable(true);
        setMinimumSize(120);
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        long now = System.currentTimeMillis();
        boolean alerting = plugin.isAlerting();
        boolean celebrating = plugin.isCelebrating(now);
        if (!alerting && !celebrating)
        {
            return null;
        }

        double progress = celebrating
            ? Math.min(1.0, (now - plugin.getCelebrationStartedAt())
                / (double) AfkEmergencyPlugin.CELEBRATION_MILLIS)
            : 0.0;
        int frameIndex = alerting ? 0 : Math.min(7, 1 + (int) (progress * 7.0));

        Dimension available = getPreferredSize() == null
            ? new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT) : getPreferredSize();
        int contentHeight = Math.max(1, available.height - LABEL_HEIGHT);
        double scale = Math.min(available.width / (double) SOURCE_WIDTH,
            contentHeight / (double) SOURCE_HEIGHT);
        int width = Math.max(1, (int) Math.round(SOURCE_WIDTH * scale));
        int height = Math.max(1, (int) Math.round(SOURCE_HEIGHT * scale));
        int x = (available.width - width) / 2;
        int y = LABEL_HEIGHT + (contentHeight - height) / 2;

        Object oldInterpolation = graphics.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.drawImage(BUTTON_FRAMES[frameIndex], x, y, width, height, null);
        if (oldInterpolation != null)
        {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterpolation);
        }

        if (alerting)
        {
            String label = available.width < 280 ? "AFK!" : "AFK EMERGENCY - CLICK TO RESUME";
            drawCentered(graphics, label, 22, available.width);
        }
        return available;
    }

    boolean contains(Point point)
    {
        return getBounds().contains(point);
    }

    private static void drawCentered(Graphics2D graphics, String text, int baseline, int width)
    {
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int fontSize = Math.max(11, Math.min(20, width / 18));
        graphics.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        FontMetrics metrics = graphics.getFontMetrics();
        int x = (width - metrics.stringWidth(text)) / 2;
        graphics.setColor(new Color(0, 0, 0, 190));
        graphics.drawString(text, x + 2, baseline + 2);
        graphics.setColor(new Color(255, 225, 171));
        graphics.drawString(text, x, baseline);
    }

    private static BufferedImage[] loadFrames()
    {
        BufferedImage[] frames = new BufferedImage[8];
        for (int frame = 0; frame < frames.length; frame++)
        {
            String resource = "/com/afkemergency/emergency-button-" + frame + ".png";
            try (InputStream input = AfkEmergencyOverlay.class.getResourceAsStream(resource))
            {
                if (input == null)
                {
                    throw new IllegalStateException("Missing emergency button sprite " + resource);
                }
                frames[frame] = ImageIO.read(input);
            }
            catch (IOException ex)
            {
                throw new IllegalStateException("Unable to load emergency button sprite " + resource, ex);
            }
        }
        return frames;
    }
}