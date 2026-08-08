package com.afkemergency;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import net.runelite.api.Skill;

final class SkillingActivityTracker
{
    private static final long MAX_TRACKED_AGE_MILLIS = 60_000L;
    private static final EnumSet<Skill> SUPPORTED_SKILLS = EnumSet.of(
        Skill.AGILITY, Skill.CONSTRUCTION, Skill.COOKING, Skill.CRAFTING,
        Skill.FARMING, Skill.FIREMAKING, Skill.FISHING, Skill.FLETCHING,
        Skill.HERBLORE, Skill.HUNTER, Skill.MINING, Skill.RUNECRAFT,
        Skill.SMITHING, Skill.THIEVING, Skill.WOODCUTTING
    );

    private final Map<Skill, Integer> experience = new EnumMap<>(Skill.class);
    private int trackedAnimation = -1;
    private long trackedAt = -1L;

    void onExperience(Skill skill, int xp, int animation, long now)
    {
        Integer previous = experience.put(skill, xp);
        if (previous != null && xp > previous && animation != -1 && isSupportedSkill(skill))
        {
            trackedAnimation = animation;
            trackedAt = now;
        }
    }

    boolean isTrackedAnimation(int animation, long now)
    {
        boolean tracked = animation != -1 && animation == trackedAnimation
            && trackedAt >= 0L && now - trackedAt <= MAX_TRACKED_AGE_MILLIS;
        if (tracked)
        {
            trackedAt = now;
        }
        return tracked;
    }

    void clearAnimation()
    {
        trackedAnimation = -1;
        trackedAt = -1L;
    }

    void reset()
    {
        experience.clear();
        clearAnimation();
    }

    static boolean isSupportedSkill(Skill skill)
    {
        return SUPPORTED_SKILLS.contains(skill);
    }
}
