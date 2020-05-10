package tfar.extratags.mixin;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.ReverseTagWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfar.extratags.api.ReverseTag;
import tfar.extratags.api.tagtypes.BiomeTags;

@Mixin(Biome.class)
public class BiomeMixin implements ReverseTag {

	@Unique
	private final ReverseTagWrapper<Biome> reverseTags =
					new ReverseTagWrapper<>((Biome)(Object)this, BiomeTags::getGeneration, BiomeTags::getCollection);

	@Override
	public java.util.Set<ResourceLocation> getTags() {
		return reverseTags.getTagNames();
	}

}
