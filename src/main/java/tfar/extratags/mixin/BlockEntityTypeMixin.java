package tfar.extratags.mixin;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ReverseTagWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfar.extratags.api.ReverseTag;
import tfar.extratags.api.tagtypes.BlockEntityTypeTags;

@Mixin(TileEntityType.class)
public class BlockEntityTypeMixin implements ReverseTag {

	@Unique
	private final ReverseTagWrapper<TileEntityType<?>> reverseTags =
					new ReverseTagWrapper<>((TileEntityType<?>)(Object)this, BlockEntityTypeTags::getGeneration, BlockEntityTypeTags::getCollection);

	@Override
	public java.util.Set<ResourceLocation> getTags() {
		return reverseTags.getTagNames();
	}

}
