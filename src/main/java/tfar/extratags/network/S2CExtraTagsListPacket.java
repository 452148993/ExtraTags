package tfar.extratags.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.extratags.api.tagtypes.BiomeTags;
import tfar.extratags.api.tagtypes.BlockEntityTypeTags;
import tfar.extratags.api.tagtypes.DimensionTypeTags;
import tfar.extratags.api.tagtypes.EnchantmentTags;
import tfar.extratags.api.ExtraTagManager;

import java.util.function.Supplier;

public class S2CExtraTagsListPacket {

	private ExtraTagManager tags;

	public S2CExtraTagsListPacket(){}

	public S2CExtraTagsListPacket(ExtraTagManager p_i48211_1_) {
		tags = p_i48211_1_;
	}

	public S2CExtraTagsListPacket(PacketBuffer buffer) {
		tags = ExtraTagManager.read(buffer);
	}

	public void encode(PacketBuffer buffer) {
		tags.write(buffer);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			EnchantmentTags.setCollection(tags.getEnchantments());
			BlockEntityTypeTags.setCollection(tags.getBlockEntityTypes());
			BiomeTags.setCollection(tags.getBiomes());
			DimensionTypeTags.setCollection(tags.getDimensionTypes());
		});
		context.get().setPacketHandled(true);
	}
}
