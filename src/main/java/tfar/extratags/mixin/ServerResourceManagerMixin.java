package tfar.extratags.mixin;

import net.minecraft.resources.DataPackRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.extratags.ExtraTags;

@Mixin(DataPackRegistries.class)
public class ServerResourceManagerMixin {

	@Inject(
					method = {"Lnet/minecraft/resources/DataPackRegistries;updateTags()V",
									"Lnet/minecraft/resources/DataPackRegistries;func_240971_i_()V"},
					at = @At("RETURN")
					,require = 1,remap = false
	)
	private void applyExtraTags(CallbackInfo ci) {
		ExtraTags.instance.extraTagManager.sync();
	}
}
