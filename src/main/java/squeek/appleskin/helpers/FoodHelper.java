package squeek.appleskin.helpers;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import squeek.appleskin.api.food.FoodValues;

public class FoodHelper {

    private static final String RULE_NATURAL_REGENERATION = "naturalRegeneration";

    public static boolean isFood(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() instanceof ItemFood;
    }

    public static boolean canConsume(ItemStack itemStack, EntityPlayer player) {
        // item is not food that can be consumed
        if (!isFood(itemStack))
            return false;

        ItemFood itemFood = (ItemFood) itemStack.getItem();
        if (itemFood == null)
            return false;

        boolean alwaysEat = ReflectionHelper.getPrivateValue(ItemFood.class, itemFood, "alwaysEdible");
        return player.canEat(alwaysEat);
    }

    public static FoodValues getDefaultFoodValues(ItemStack itemStack) {
        return getDefaultFoodValues(itemStack, null);
    }

    public static FoodValues getDefaultFoodValues(ItemStack itemStack, EntityPlayer player) {
        ItemFood itemFood = (ItemFood) itemStack.getItem();
        int hunger = itemFood != null ? itemFood.func_150905_g(itemStack) : 0;
        float saturationModifier = itemFood != null ? itemFood.func_150906_h(itemStack) : 0;

        return new FoodValues(hunger, saturationModifier);
    }

    public static FoodValues getModifiedFoodValues(ItemStack itemStack, EntityPlayer player) {
        // Previously, this would use AppleCore to get the modified values, but since AppleCore doesn't
        // exist on this MC version and https://github.com/MinecraftForge/MinecraftForge/pull/7266
        // hasn't been merged, we just return the defaults here.
        return getDefaultFoodValues(itemStack, player);
    }

    public static boolean isRotten(ItemStack itemStack, EntityPlayer player) {
        if (!isFood(itemStack))
            return false;

        ItemFood itemFood = (ItemFood) itemStack.getItem();
        int potionId = ReflectionHelper.getPrivateValue(ItemFood.class, itemFood, "potionId");
        return potionId == Potion.harm.getId();
        //
        // for (Pair<MobEffectInstance, Float> effect : itemStack.getItem().getFoodProperties(itemStack, player).getEffects()) {
        //     if (effect.getFirst() != null && effect.getFirst().getEffect() != null && effect.getFirst().getEffect().getCategory() == MobEffectCategory.HARMFUL) {
        //         return true;
        //     }
        // }
        // return false;
    }

    public static float getEstimatedHealthIncrement(ItemStack itemStack, FoodValues modifiedFoodValues, EntityPlayer player) {
        if (!isFood(itemStack))
            return 0;

        if (!player.shouldHeal())
            return 0;

        FoodStats stats = player.getFoodStats();
        World world = player.getEntityWorld();

        int foodLevel = Math.min(stats.getFoodLevel() + modifiedFoodValues.hunger, 20);
        float healthIncrement = 0;

        // health for natural regen
        if (foodLevel >= 18.0F && world != null && world.getGameRules().getGameRuleBooleanValue(RULE_NATURAL_REGENERATION)) {
            float saturationLevel = Math.min(stats.getSaturationLevel() + modifiedFoodValues.getSaturationIncrement(), (float) foodLevel);
            float exhaustionLevel = HungerHelper.getExhaustion(player);
            healthIncrement = getEstimatedHealthIncrement(foodLevel, saturationLevel, exhaustionLevel);
        }

        // health for regeneration effect
        ItemFood itemFood = (ItemFood) itemStack.getItem();
        int potionId = ReflectionHelper.getPrivateValue(ItemFood.class, itemFood, "potionId");
        if (potionId == Potion.regeneration.getId()) {
            int potionDuration = ReflectionHelper.getPrivateValue(ItemFood.class, itemFood, "potionDuration");
            int potionAmplifier = ReflectionHelper.getPrivateValue(ItemFood.class, itemFood, "potionAmplifier");

            healthIncrement += (float) Math.floor(potionDuration / Math.max(50 >> potionAmplifier, 1));
        }

        // for (Pair<MobEffectInstance, Float> effect : itemStack.getItem().getFoodProperties(itemStack, player).getEffects()) {
        //     MobEffectInstance effectInstance = effect.getFirst();
        //     if (effectInstance != null && effectInstance.getEffect() == MobEffects.REGENERATION) {
        //         int amplifier = effectInstance.getAmplifier();
        //         int duration = effectInstance.getDuration();
        //
        //         // Refer: https://minecraft.fandom.com/wiki/Regeneration
        //         // Refer: net.minecraft.world.effect.MobEffect.isDurationEffectTick
        //         healthIncrement += (float) Math.floor(duration / Math.max(50 >> amplifier, 1));
        //         break;
        //     }
        // }

        return healthIncrement;
    }

    public static float REGEN_EXHAUSTION_INCREMENT = 6.0F;
    public static float MAX_EXHAUSTION = 4.0F;

    public static float getEstimatedHealthIncrement(int foodLevel, float saturationLevel, float exhaustionLevel) {
        float health = 0;

        if (!Float.isFinite(exhaustionLevel) || !Float.isFinite(saturationLevel))
            return 0;

        while (foodLevel >= 18) {
            while (exhaustionLevel > MAX_EXHAUSTION) {
                exhaustionLevel -= MAX_EXHAUSTION;
                if (saturationLevel > 0)
                    saturationLevel = Math.max(saturationLevel - 1, 0);
                else
                    foodLevel -= 1;
            }
            // Without this Float.compare, it's possible for this function to get stuck in an infinite loop
            // if saturationLevel is small enough that exhaustionLevel does not actually change representation
            // when it's incremented. This Float.compare makes it so we treat such close-to-zero values as zero.
            if (foodLevel >= 20 && Float.compare(saturationLevel, Float.MIN_NORMAL) > 0) {
                // fast regen health
                //
                // Because only health and exhaustionLevel increase in this branch,
                // we know that we will enter this branch again and again on each iteration
                // if exhaustionLevel is not incremented above MAX_EXHAUSTION before the
                // next iteration.
                //
                // So, instead of actually performing those iterations, we can calculate
                // the number of iterations it would take to reach max exhaustion, and
                // add all the health/exhaustion in one go. In practice, this takes the
                // worst-case number of iterations performed in this function from the millions
                // all the way down to around 18.
                //
                // Note: Due to how floating point works, the results of actually doing the
                // iterations and 'simulating' them using multiplication will differ. That is, small increments
                // in a loop can end up with a different (and higher) final result than multiplication
                // due to floating point rounding. In degenerate cases, the difference can be fairly high
                // (when testing, I found a case that had a difference of ~0.3), but this isn't a concern in
                // this particular instance because the 'real' difference as seen by the player
                // would likely take hundreds of thousands of ticks to materialize (since the
                // `limitedSaturationLevel / REGEN_EXHAUSTION_INCREMENT` value must be very
                // small for a difference to occur at all, and therefore numIterationsUntilAboveMax would
                // be very large).
                float limitedSaturationLevel = Math.min(saturationLevel, REGEN_EXHAUSTION_INCREMENT);
                float exhaustionUntilAboveMax = Math.nextUp(MAX_EXHAUSTION) - exhaustionLevel;
                int numIterationsUntilAboveMax = Math.max(1, (int) Math.ceil(exhaustionUntilAboveMax / limitedSaturationLevel));

                health += (limitedSaturationLevel / REGEN_EXHAUSTION_INCREMENT) * numIterationsUntilAboveMax;
                exhaustionLevel += limitedSaturationLevel * numIterationsUntilAboveMax;
            } else if (foodLevel >= 18) {
                // slow regen health
                health += 1;
                exhaustionLevel += REGEN_EXHAUSTION_INCREMENT;
            }
        }

        return health;
    }
}
