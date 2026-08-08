package com.afkemergency;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

final class AfkEmergencyDimOverlay extends Overlay
{
    private final Client client;
    private final AfkEmergencyPlugin plugin;
    private final AfkEmergencyConfig config;

    @Inject
    private AfkEmergencyDimOverlay(Client client, AfkEmergencyPlugin plugin, AfkEmergencyConfig config)
    {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isAlerting())
        {
            return null;
        }
        int width = client.getCanvasWidth();
        int height = client.getCanvasHeight();
        int alpha = Math.round(255 * config.dimOpacity() / 100f);
        graphics.setColor(new Color(0, 0, 0, alpha));
        graphics.fillRect(0, 0, width, height);
        return new Dimension(width, height);
    }
}