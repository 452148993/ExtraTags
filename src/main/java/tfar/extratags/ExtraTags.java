package tfar.extratags;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import tfar.extratags.api.ReverseTag;
import tfar.extratags.network.PacketHandler;
import tfar.extratags.network.S2CExtraTagsListPacket;
import tfar.extratags.api.ExtraTagManager;

import java.util.Set;

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
		MinecraftForge.EVENT_BUS.addListener(this::onTooltip);
		instance = this;
	}

	private void common(FMLCommonSetupEvent e) {
		PacketHandler.registerMessages(MODID);
	}

	private void onServerStarting(FMLServerAboutToStartEvent e){
		ExtraTags.instance.extraTagManager = new ExtraTagManager();
		e.getServer().getResourceManager().addReloadListener(ExtraTags.instance.extraTagManager);
	}

	private void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.getPlayer().world.isRemote) {
			PacketHandler.INSTANCE.sendTo(new S2CExtraTagsListPacket(ExtraTags.instance.extraTagManager),
							((ServerPlayerEntity) event.getPlayer()).connection.getNetworkManager(),
							NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	public void onTooltip(ItemTooltipEvent e) {
		if (e.getItemStack().getItem() == Items.ENCHANTED_BOOK){
			Set<Enchantment> enchantments = EnchantmentHelper.getEnchantments(e.getItemStack()).keySet();
			if (enchantments.size() == 1) {
				e.getToolTip().add(new StringTextComponent("Enchantment Tags:").applyTextStyle(TextFormatting.AQUA));
				enchantments.forEach(enchantment -> {
					Set<ResourceLocation> tags = ((ReverseTag) enchantment).getTags();
					if (!tags.isEmpty())
						tags.forEach(resourceLocation -> e.getToolTip().add(new StringTextComponent(resourceLocation.toString())
										.applyTextStyle(TextFormatting.DARK_GRAY)));
					else e.getToolTip().add(new StringTextComponent("none").applyTextStyle(TextFormatting.DARK_GRAY));
				});
			}
		}
	}
}
