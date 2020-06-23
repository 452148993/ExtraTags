package tfar.extratags.mixin;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.ReverseTagWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfar.extratags.VanillaReverseTagWrapper;
import tfar.extratags.api.ReverseTag;

import static tfar.extratags.api.TagRegistry.DIMENSION_TYPE;

@Mixin(DimensionType.class)
public class DimensionTypeMixin implements ReverseTag {

	@Unique
	private final ReverseTagWrapper<DimensionType> reverseTags = new VanillaReverseTagWrapper<>((DimensionType)(Object)this,DIMENSION_TYPE);

	@Override
	public java.util.Set<ResourceLocation> getTags() {
		return reverseTags.getTagNames();
	}

}
