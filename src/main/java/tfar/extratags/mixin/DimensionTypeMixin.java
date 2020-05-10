package tfar.extratags.mixin;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.ReverseTagWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfar.extratags.api.ReverseTag;
import tfar.extratags.api.tagtypes.BiomeTags;
import tfar.extratags.api.tagtypes.DimensionTypeTags;

@Mixin(Biome.class)
public class DimensionTypeMixin implements ReverseTag {

	@Unique
	private final ReverseTagWrapper<DimensionType> reverseTags =
					new ReverseTagWrapper<DimensionType>((DimensionType)(Object)this, BiomeTags::getGeneration, DimensionTypeTags::getCollection);

	@Override
	public java.util.Set<ResourceLocation> getTags() {
		return reverseTags.getTagNames();
	}

}
