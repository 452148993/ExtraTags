package tfar.extratags.mixin;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.ReverseTagWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfar.extratags.VanillaReverseTagWrapper;
import tfar.extratags.api.ReverseTag;

import static tfar.extratags.api.ExtraTagRegistry.BIOME;

@Mixin(Biome.class)
public class BiomeMixin implements ReverseTag {

	@Unique
	private final ReverseTagWrapper<Biome> reverseTags = new VanillaReverseTagWrapper<>((Biome)(Object)this,BIOME);

	@Override
	public java.util.Set<ResourceLocation> getTags() {
		return reverseTags.getTagNames();
	}

}
