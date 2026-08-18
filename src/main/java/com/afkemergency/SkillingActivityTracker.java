package com.afkemergency;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.gameval.AnimationID;

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

    static boolean isMotherlodeMiningAnimation(int animation)
    {
        switch (animation)
        {
            case AnimationID.HUMAN_MINING_BRONZE_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_IRON_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_STEEL_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_BLACK_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_MITHRIL_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_ADAMANT_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_RUNE_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_GILDED_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_DRAGON_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_DRAGON_PICKAXE_PRETTY_WALL:
            case AnimationID.HUMAN_MINING_ZALCANO_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_TRAILBLAZER_PICKAXE_NO_INFERNAL_WALL:
            case AnimationID.HUMAN_MINING_INFERNAL_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_TRAILBLAZER_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_TRAILBLAZER_RELOADED_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_3A_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_CRYSTAL_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_LEAGUE_TRAILBLAZER_PICKAXE_WALL:
            case AnimationID.HUMAN_MINING_TRAILBLAZER_RELOADED_PICKAXE_NO_INFERNAL_WALL:
                return true;
            default:
                return false;
        }
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
