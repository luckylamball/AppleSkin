package squeek.appleskin.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import squeek.appleskin.ModConfig;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.helpers.FoodHelper;
import squeek.appleskin.helpers.KeyHelper;
import squeek.appleskin.helpers.TextureHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SideOnly(Side.CLIENT)
public class TooltipOverlayHandler {

    public static final int CONTAINER_PADDING = 3;

    public static int guiX;
    public static int guiY;
    public static int guiHeight;
    public static int guiWidth;

    public static final Field theSlot =
            ReflectionHelper.findField(
                    GuiContainer.class,
                    ObfuscationReflectionHelper.remapFieldNames(
                            GuiContainer.class.getName(),
                            "theSlot",
                            "field_147006_u",
                            "u"));

    public static void init() {
        FMLCommonHandler.instance().bus().register(new TooltipOverlayHandler());
    }

    enum FoodOutline {
        NEGATIVE,
        EXTRA,
        NORMAL,
        PARTIAL,
        MISSING;

        public void setShaderColor() {
            switch (this) {
                case NEGATIVE:
                    GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                    break;
                case EXTRA:
                    GL11.glClearColor(0.06f, 0.32f, 0.02f, 1.0f);
                    break;
                case NORMAL:
                    GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    break;
                case PARTIAL:
                    GL11.glClearColor(0.53f, 0.21f, 0.08f, 1.0f);
                    break;
                case MISSING:
                    GL11.glClearColor(0.62f, 0.0f, 0.0f, 0.5f);
                default:
                    break;
            }
        }

        public static FoodOutline get(int modifiedFoodHunger, int defaultFoodHunger, int i) {
            if (modifiedFoodHunger < 0)
                return NEGATIVE;
            else if (modifiedFoodHunger > defaultFoodHunger && defaultFoodHunger <= i)
                return EXTRA;
            else if (modifiedFoodHunger > i + 1 || defaultFoodHunger == modifiedFoodHunger)
                return NORMAL;
            else if (modifiedFoodHunger == i + 1)
                return PARTIAL;
            else
                return MISSING;
        }
    }

    static class FoodTooltip {
        private FoodValues defaultFood;
        private FoodValues modifiedFood;

        private int biggestHunger;
        private float biggestSaturationIncrement;

        private int hungerBars;
        private String hungerBarsText;

        private int saturationBars;
        private String saturationBarsText;

        private ItemStack itemStack;

        FoodTooltip(ItemStack itemStack, FoodValues defaultFood, FoodValues modifiedFood) {
            this.itemStack = itemStack;
            this.defaultFood = defaultFood;
            this.modifiedFood = modifiedFood;

            biggestHunger = Math.max(defaultFood.hunger, modifiedFood.hunger);
            biggestSaturationIncrement = Math.max(defaultFood.getSaturationIncrement(), modifiedFood.getSaturationIncrement());

            hungerBars = (int) Math.ceil(Math.abs(biggestHunger) / 2f);
            if (hungerBars > 10) {
                hungerBarsText = "x" + ((biggestHunger < 0 ? -1 : 1) * hungerBars);
                hungerBars = 1;
            }

            saturationBars = (int) Math.ceil(Math.abs(biggestSaturationIncrement) / 2f);
            if (saturationBars > 10 || saturationBars == 0) {
                saturationBarsText = "x" + ((biggestSaturationIncrement < 0 ? -1 : 1) * saturationBars);
                saturationBars = 1;
            }
        }

        boolean shouldRenderHungerBars() {
            return hungerBars > 0;
        }
    }

    private static boolean shouldShowTooltip() {
        return shouldShowTooltip(null);
    }

    private static boolean shouldShowTooltip(ItemStack hoveredStack) {
        boolean shouldShowTooltip = (ModConfig.SHOW_FOOD_VALUES_IN_TOOLTIP.get() && KeyHelper.isShiftKeyDown())
                || ModConfig.ALWAYS_SHOW_FOOD_VALUES_TOOLTIP.get();

        if (null == hoveredStack) {
            return shouldShowTooltip;
        }
        if (hoveredStack.stackSize == 0) {
            return false;
        }

        return shouldShowTooltip && FoodHelper.isFood(hoveredStack);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.isCanceled())
            return;

        if (shouldShowTooltip()) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.thePlayer;
            GuiScreen guiScreen = mc.currentScreen;

            ScaledResolution scale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

            Class<?> foodJournalGui = null;
            boolean isFoodJournalGui = foodJournalGui != null && foodJournalGui.isInstance(guiScreen);
            boolean isValidContainerGui = guiScreen instanceof GuiContainer;
            if (isValidContainerGui) {
                Gui gui = guiScreen;
                int mouseX = Mouse.getX() * scale.getScaledWidth() / mc.displayWidth;
                int mouseY = scale.getScaledHeight() - Mouse.getY() * scale.getScaledHeight() / mc.displayHeight;
                ItemStack hoveredStack = null;

                // get the hovered stack from the active container
                try {
                    // try regular container
                    Slot hoveredSlot = (Slot) TooltipOverlayHandler.theSlot.get(gui);

                    // get the stack
                    if (hoveredSlot != null)
                        hoveredStack = hoveredSlot.getStack();

                    // try NEI
                    Method getStackMouseOver = null;
                    Field itemPanel = null;
                    if (hoveredStack == null && getStackMouseOver != null)
                        hoveredStack = (ItemStack) (getStackMouseOver.invoke(itemPanel.get(null), mouseX, mouseY));

                    // try FoodJournal
                    Field foodJournalHoveredStack = null;
                    if (hoveredStack == null && isFoodJournalGui)
                        hoveredStack = (ItemStack) foodJournalHoveredStack.get(gui);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // if the hovered stack is a food and there is no item being dragged
                if (player.inventory.getItemStack() == null && hoveredStack != null && FoodHelper.isFood(hoveredStack)) {
                    FoodValues defaultFoodValues = FoodHelper.getDefaultFoodValues(hoveredStack);
                    FoodValues modifiedFoodValues = FoodHelper.getModifiedFoodValues(hoveredStack, player);

                    FoodTooltip foodTooltip = new FoodTooltip(hoveredStack, defaultFoodValues, modifiedFoodValues);

                    if (defaultFoodValues.equals(modifiedFoodValues)
                            && defaultFoodValues.hunger == 0
                            && defaultFoodValues.saturationModifier == 0)
                        return;

                    int biggestHunger = foodTooltip.biggestHunger;
                    float biggestSaturationIncrement = foodTooltip.biggestSaturationIncrement;
                    int hungerBars = foodTooltip.hungerBars;
                    int saturationBars = foodTooltip.saturationBars;
                    String saturationText = foodTooltip.saturationBarsText;

                    boolean neiLoaded = false;
                    // boolean needsCoordinateShift = !neiLoaded || isFoodJournalGui;
                    boolean needsCoordinateShift = false;

                    int toolTipBottomY = guiY + (needsCoordinateShift ? 3 : 0);
                    int toolTipRightX = guiX + guiWidth + (needsCoordinateShift ? 3 : 0);

                    boolean shouldDrawBelow = toolTipBottomY + 20 < scale.getScaledHeight() - 3;

                    int rightX = toolTipRightX + CONTAINER_PADDING;
                    int leftX = rightX - (Math.max(hungerBars * 9, saturationBars * 6 + (int) (mc.fontRenderer.getStringWidth(saturationText) * 0.75f))) - 4;
                    int topY = (shouldDrawBelow ? toolTipBottomY : guiY - 20 + (needsCoordinateShift ? -4 : 0));
                    int bottomY = topY + 20;

                    boolean isLightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
                    if (isLightingEnabled) {
                        GL11.glDisable(GL11.GL_LIGHTING);
                    }
                    GL11.glDisable(GL11.GL_DEPTH_TEST);

                    // bg
                    Gui.drawRect(leftX - 1, topY, rightX + 1, bottomY, 0xF0100010);
                    Gui.drawRect(leftX, (shouldDrawBelow ? bottomY : topY - 1), rightX, (shouldDrawBelow ? bottomY + 1 : topY), 0xF0100010);
                    Gui.drawRect(leftX, topY, rightX, bottomY, 0x66FFFFFF);

                    int x = rightX - 2;
                    int startX = x;
                    int y = bottomY - 19;

                    GL11.glColor4f(1f, 1f, 1f, .25f);

                    mc.getTextureManager().bindTexture(Gui.icons);

                    for (int i = 0; i < hungerBars * 2; i += 2) {
                        x -= 9;

                        if (modifiedFoodValues.hunger < 0)
                            gui.drawTexturedModalRect(x, y, 34, 27 - 20, 9, 9);
                        else if (modifiedFoodValues.hunger > defaultFoodValues.hunger && defaultFoodValues.hunger <= i)
                            gui.drawTexturedModalRect(x, y, 133, 27, 9, 9);
                        else if (modifiedFoodValues.hunger > i + 1 || defaultFoodValues.hunger == modifiedFoodValues.hunger)
                            gui.drawTexturedModalRect(x, y, 16, 27, 9, 9);
                        else if (modifiedFoodValues.hunger == i + 1)
                            gui.drawTexturedModalRect(x, y, 124, 27, 9, 9);
                        else
                            gui.drawTexturedModalRect(x, y, 34, 27, 9, 9);

                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        gui.drawTexturedModalRect(x, y, defaultFoodValues.hunger - 1 == i ? 115 : 106, 27, 9, 9);
                        GL11.glDisable(GL11.GL_BLEND);

                        if (modifiedFoodValues.hunger > i)
                            gui.drawTexturedModalRect(x, y, modifiedFoodValues.hunger - 1 == i ? 61 : 52, 27, 9, 9);
                    }

                    y += 11;
                    x = startX;
                    float modifiedSaturationIncrement = modifiedFoodValues.getSaturationIncrement();
                    float absModifiedSaturationIncrement = Math.abs(modifiedSaturationIncrement);

                    GL11.glPushMatrix();
                    GL11.glScalef(0.75F, 0.75F, 0.75F);
                    GL11.glColor4f(1f, 1f, 1f, .5f);
                    for (int i = 0; i < saturationBars * 2; i += 2) {
                        float effectiveSaturationOfBar = (absModifiedSaturationIncrement - i) / 2f;

                        x -= 6;

                        boolean shouldBeFaded = absModifiedSaturationIncrement <= i;
                        if (shouldBeFaded) {
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        }

                        mc.getTextureManager().bindTexture(Gui.icons);
                        gui.drawTexturedModalRect(x * 4 / 3, y * 4 / 3, 16, 27, 9, 9);

                        mc.getTextureManager().bindTexture(TextureHelper.MOD_ICONS);
                        gui.drawTexturedModalRect(x * 4 / 3, y * 4 / 3, effectiveSaturationOfBar >= 1 ? 27 : effectiveSaturationOfBar > 0.5 ? 18 : effectiveSaturationOfBar > 0.25 ? 9 : effectiveSaturationOfBar > 0 ? 0 : 36, modifiedSaturationIncrement >= 0 ? 0 : 9, 9, 9);

                        if (shouldBeFaded)
                            GL11.glDisable(GL11.GL_BLEND);
                    }
                    if (saturationText != null) {
                        mc.fontRenderer.drawStringWithShadow(saturationText, x * 4 / 3 - mc.fontRenderer.getStringWidth(saturationText) + 2, y * 4 / 3 + 1, 0xFFFF0000);
                    }
                    GL11.glPopMatrix();

                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    if (isLightingEnabled) {
                        GL11.glEnable(GL11.GL_LIGHTING);
                    }
                    GL11.glColor4f(1f, 1f, 1f, 1f);
                }
            }
        }
    }
}
