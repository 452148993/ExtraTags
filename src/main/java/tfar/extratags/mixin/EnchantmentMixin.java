package tfar.extratags.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ReverseTagWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfar.extratags.VanillaReverseTagWrapper;
import tfar.extratags.api.ReverseTag;

import static tfar.extratags.api.TagRegistry.ENCHANTMENT;

@Mixin(Enchantment.class)
public class EnchantmentMixin implements ReverseTag {

	@Unique
	private final ReverseTagWrapper<Enchantment> reverseTags = new VanillaReverseTagWrapper<>((Enchantment) (Object)this, ENCHANTMENT);

	@Override
	public java.util.Set<ResourceLocation> getTags() {
		return reverseTags.getTagNames();
	}

}
