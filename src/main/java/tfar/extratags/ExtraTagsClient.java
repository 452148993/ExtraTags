package tfar.extratags;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import tfar.extratags.api.ExtraTagRegistry;
import tfar.extratags.api.ModTag;

public class ExtraTagsClient {

	public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
		if (!event.getNetworkManager().isLocalChannel())
			ExtraTagRegistry.tagTypeList.forEach(ModTag::markReady);
		/*if (!event.getPlayer().world.isRemote) {
			PacketHandler.INSTANCE.sendTo(new S2CExtraTagsListPacket(ExtraTags.instance.extraTagManager),
							((ServerPlayerEntity) event.getPlayer()).connection.getNetworkManager(),
							NetworkDirection.PLAY_TO_CLIENT);
		}*/
	}
}
