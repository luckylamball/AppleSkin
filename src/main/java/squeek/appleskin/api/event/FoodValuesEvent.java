package squeek.appleskin.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import squeek.appleskin.api.food.FoodValues;

/**
 * Can be used to customize the displayed hunger/saturation values of foods.
 * Called whenever the food values of items are being determined.
 */
public class FoodValuesEvent extends Event {
    public FoodValues defaultFoodValues;
    public FoodValues modifiedFoodValues;
    public final ItemStack itemStack;
    public final EntityPlayer player;

    public FoodValuesEvent(EntityPlayer player, ItemStack itemStack, FoodValues defaultFoodValues, FoodValues modifiedFoodValues) {
        this.player = player;
        this.itemStack = itemStack;
        this.defaultFoodValues = defaultFoodValues;
        this.modifiedFoodValues = modifiedFoodValues;
    }
}
