package tfar.extratags.mixin;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ReverseTagWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfar.extratags.VanillaReverseTagWrapper;
import tfar.extratags.api.ReverseTag;

import static tfar.extratags.api.ExtraTagRegistry.BLOCK_ENTITY_TYPE;

@Mixin(TileEntityType.class)
public class BlockEntityTypeMixin implements ReverseTag {

	@Unique
	private final ReverseTagWrapper<TileEntityType<?>> reverseTags = new VanillaReverseTagWrapper<>((TileEntityType<?>) (Object)this, BLOCK_ENTITY_TYPE);

	@Override
	public java.util.Set<ResourceLocation> getTags() {
		return reverseTags.getTagNames();
	}

}
