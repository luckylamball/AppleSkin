package squeek.appleskin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.appleskin.client.TooltipOverlayHandler;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {

    @Inject(method = "Lnet/minecraft/client/gui/GuiScreen;drawHoveringText(Ljava/util/List;IILnet/minecraft/client/gui/FontRenderer;)V",
            at = @At(value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V"),
            remap = false)
    private void generatePos(CallbackInfo ci,
                             @Local(ordinal = 3) int j2,
                             @Local(ordinal = 4) int k2,
                             @Local(ordinal = 2) int k,
                             @Local(ordinal = 5) int i1) {
        TooltipOverlayHandler.guiX = j2;
        TooltipOverlayHandler.guiY = k2;
        TooltipOverlayHandler.guiWidth = k;
        TooltipOverlayHandler.guiHeight = i1;
    }
}
