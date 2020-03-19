package tfar.extratags.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import tfar.extratags.ExtraTags;

public class PacketHandler {
  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    int id = 0;

    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ExtraTags.MODID, channelName), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(id++, S2CExtraTagsListPacket.class,
            S2CExtraTagsListPacket::encode,
            S2CExtraTagsListPacket::new,
            S2CExtraTagsListPacket::handle);
  }

  /**
   * Send this message to everyone connected to the server.
   *
   * @param message - message to send
   */
  public static <MSG> void sendToAll(MSG message) {
    INSTANCE.send(PacketDistributor.ALL.noArg(),message);
  }
}
