package com.afkemergency;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(AfkEmergencyConfig.GROUP)
public interface AfkEmergencyConfig extends Config
{
    String GROUP = "afkEmergency";

    @ConfigItem(
        keyName = "minimumActivitySeconds",
        name = "Activity time",
        description = "How long a verified skilling animation must continue before the reminder is armed",
        position = 0
    )
    @Range(min = 1, max = 30)
    @Units(Units.SECONDS)
    default int minimumActivitySeconds()
    {
        return 3;
    }

    @ConfigItem(
        keyName = "idleDelaySeconds",
        name = "Grace period",
        description = "How long to wait after the action stops before showing the button",
        position = 1
    )
    @Range(min = 0, max = 30)
    @Units(Units.SECONDS)
    default int idleDelaySeconds()
    {
        return 2;
    }

    @ConfigItem(
        keyName = "dimOpacity",
        name = "Screen dimming",
        description = "How strongly the rest of the game is dimmed",
        position = 2
    )
    @Range(min = 0, max = 90)
    @Units(Units.PERCENT)
    default int dimOpacity()
    {
        return 62;
    }

    @ConfigItem(
        keyName = "dismissAnywhere",
        name = "Any click dismisses",
        description = "Dismiss on any game click. The dismissing click is blocked for safety",
        position = 3
    )
    default boolean dismissAnywhere()
    {
        return true;
    }

    @ConfigItem(
        keyName = "previewHotkey",
        name = "Preview hotkey",
        description = "A hotkey for previewing the alert and its dismissal animation",
        position = 4
    )
    default Keybind previewHotkey()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
        keyName = "alertSound",
        name = "Alert sound",
        description = "Play a gentle chime when the emergency button appears",
        position = 5
    )
    default boolean alertSound()
    {
        return true;
    }

    @ConfigItem(
        keyName = "pressSound",
        name = "Button press sound",
        description = "Play a short pop when the alert is dismissed",
        position = 6
    )
    default boolean pressSound()
    {
        return true;
    }

    @ConfigItem(
        keyName = "soundVolume",
        name = "Sound volume",
        description = "Volume of the alert and button press sounds",
        position = 7
    )
    @Range(min = 0, max = 100)
    @Units(Units.PERCENT)
    default int soundVolume()
    {
        return 35;
    }
}