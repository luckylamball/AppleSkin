package squeek.appleskin.helpers;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;

import java.lang.reflect.Field;

public class HungerHelper {
    protected static final Field foodExhaustion = ReflectionHelper.findField(FoodStats.class, "foodExhaustionLevel", "field_75126_c", "c");

    public static float getMaxExhaustion(EntityPlayer player) {
        return 4.0f;
    }

    public static float getExhaustion(EntityPlayer player) {
        try {
            return foodExhaustion.getFloat(player.getFoodStats());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setExhaustion(EntityPlayer player, float exhaustion) {
        try {
            foodExhaustion.setFloat(player.getFoodStats(), exhaustion);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
