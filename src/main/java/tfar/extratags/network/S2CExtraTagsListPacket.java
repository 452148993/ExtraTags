package tfar.extratags.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.extratags.api.ExtraTagManager;

import java.util.function.Supplier;

public class S2CExtraTagsListPacket {

	private ExtraTagManager tags;

	public S2CExtraTagsListPacket(){}

	public S2CExtraTagsListPacket(ExtraTagManager tagManager) {
		tags = tagManager;
	}

	public S2CExtraTagsListPacket(PacketBuffer buffer) {
		tags = ExtraTagManager.read(buffer);
	}

	public void encode(PacketBuffer buffer) {
		tags.write(buffer);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(tags::sync);
		context.get().setPacketHandled(true);
	}
}
