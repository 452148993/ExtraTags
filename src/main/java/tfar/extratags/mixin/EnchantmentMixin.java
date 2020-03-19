package tfar.extratags.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ReverseTagWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfar.extratags.api.ReverseTag;
import tfar.extratags.api.tagtypes.EnchantmentTags;

@Mixin(Enchantment.class)
public class EnchantmentMixin implements ReverseTag {

	@Unique
	private final net.minecraftforge.common.util.ReverseTagWrapper<Enchantment> reverseTags =
					new ReverseTagWrapper<>((Enchantment)(Object)this, EnchantmentTags::getGeneration, EnchantmentTags::getCollection);

	@Override
	public java.util.Set<ResourceLocation> getTags() {
		return reverseTags.getTagNames();
	}

}
