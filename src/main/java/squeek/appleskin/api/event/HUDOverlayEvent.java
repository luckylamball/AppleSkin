package squeek.appleskin.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import squeek.appleskin.api.food.FoodValues;

@Cancelable
public class HUDOverlayEvent extends Event {
    /**
     * If cancelled, will stop all rendering of the exhaustion meter.
     */
    public static class Exhaustion extends HUDOverlayEvent {
        public Exhaustion(float exhaustion, int x, int y, Gui gui) {
            super(x, y, gui);
            this.exhaustion = exhaustion;
        }

        public final float exhaustion;
    }

    /**
     * If cancelled, will stop all rendering of the saturation overlay.
     */
    public static class Saturation extends HUDOverlayEvent {
        public Saturation(float saturationLevel, int x, int y, Gui gui) {
            super(x, y, gui);
            this.saturationLevel = saturationLevel;
        }

        public final float saturationLevel;
    }

    /**
     * If cancelled, will stop all rendering of the hunger restored overlay.
     */
    public static class HungerRestored extends HUDOverlayEvent {
        public HungerRestored(int foodLevel, ItemStack itemStack, FoodValues foodValues, int x, int y, Gui gui) {
            super(x, y, gui);
            this.currentFoodLevel = foodLevel;
            this.itemStack = itemStack;
            this.foodValues = foodValues;
        }

        public final FoodValues foodValues;
        public final ItemStack itemStack;
        public final int currentFoodLevel;
    }

    /**
     * If cancelled, will stop all rendering of the estimated health overlay.
     */
    public static class HealthRestored extends HUDOverlayEvent {
        public HealthRestored(float modifiedHealth, ItemStack itemStack, FoodValues foodValues, int x, int y, Gui gui) {
            super(x, y, gui);
            this.modifiedHealth = modifiedHealth;
            this.itemStack = itemStack;
            this.foodValues = foodValues;
        }

        public final FoodValues foodValues;
        public final ItemStack itemStack;
        public final float modifiedHealth;
    }

    private HUDOverlayEvent(int x, int y, Gui gui) {
        this.x = x;
        this.y = y;
        this.gui = gui;
    }

    public int x;
    public int y;
    public Gui gui;

    @Override
    public boolean isCancelable() {
        return true;
    }
}
