package tfar.extratags;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tfar.extratags.api.ExtraTagManager;
import tfar.extratags.api.ExtraTagRegistry;
import tfar.extratags.api.ModTag;
import tfar.extratags.network.PacketHandler;
import tfar.extratags.test.TooltipTest;

@Mod(ExtraTags.MODID)
public class ExtraTags {
	public static final String MODID = "extratags";

	public static ExtraTags instance;

	public ExtraTagManager extraTagManager;

	public ExtraTags(){
		IEventBus iEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		iEventBus.addListener(this::common);
		MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerLogin);
		if (dev()) {
			MinecraftForge.EVENT_BUS.addListener(TooltipTest::onTooltip);
		}
		instance = this;
	}

	private void common(FMLCommonSetupEvent e) {
		PacketHandler.registerMessages(MODID);
	}

	private void onServerStarting(AddReloadListenerEvent e){
		ExtraTags.instance.extraTagManager = new ExtraTagManager();
		e.addListener(ExtraTags.instance.extraTagManager);
	}

	private void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
		if (!event.getNetworkManager().isLocalChannel())
		ExtraTagRegistry.tagTypeList.forEach(ModTag::markReady);
		/*if (!event.getPlayer().world.isRemote) {
			PacketHandler.INSTANCE.sendTo(new S2CExtraTagsListPacket(ExtraTags.instance.extraTagManager),
							((ServerPlayerEntity) event.getPlayer()).connection.getNetworkManager(),
							NetworkDirection.PLAY_TO_CLIENT);
		}*/
	}

	public static boolean dev() {
		try {
			ItemStack.class.getMethod("func_190926_b");
			return false;
		} catch (Exception e) {
			return true;
		}
	}
}
