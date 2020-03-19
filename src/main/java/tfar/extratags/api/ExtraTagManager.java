package tfar.extratags.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import tfar.extratags.ExtraTags;
import tfar.extratags.network.PacketHandler;
import tfar.extratags.network.S2CExtraTagsListPacket;
import tfar.extratags.api.tagtypes.EnchantmentTags;
import tfar.extratags.api.tagtypes.BlockEntityTypeTags;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ExtraTagManager implements IFutureReloadListener {

	private final NetworkTagCollection<Enchantment> enchantments = new NetworkTagCollection<>(Registry.ENCHANTMENT, "tags/enchantments", "enchantment");
	private final NetworkTagCollection<TileEntityType<?>> block_entity_types = new NetworkTagCollection<>(Registry.BLOCK_ENTITY_TYPE, "tags/block_entity_types", "block_entity_type");

	public NetworkTagCollection<Enchantment> getEnchantments() {
		return this.enchantments;
	}

	public NetworkTagCollection<TileEntityType<?>> getBlockEntityTypes() {
		return this.block_entity_types;
	}

	public void write(PacketBuffer buffer) {
		this.enchantments.write(buffer);
		this.block_entity_types.write(buffer);
	}

	public static ExtraTagManager read(PacketBuffer buffer) {
		ExtraTagManager tagManager = new ExtraTagManager();
		tagManager.getEnchantments().read(buffer);
		tagManager.getBlockEntityTypes().read(buffer);
		return tagManager;
	}

	@Override
	public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		CompletableFuture<Map<ResourceLocation, Tag.Builder<Enchantment>>> enchantmentReload = this.enchantments.reload(resourceManager, backgroundExecutor);
		CompletableFuture<Map<ResourceLocation, Tag.Builder<TileEntityType<?>>>> blockEntityReload = this.block_entity_types.reload(resourceManager, backgroundExecutor);
		return enchantmentReload.thenCombine(blockEntityReload, ReloadResults::new)
						.thenCompose(stage::markCompleteAwaitingOthers)
						.thenAcceptAsync(reloadResults -> {
							this.enchantments.registerAll(reloadResults.enchantments);
							this.block_entity_types.registerAll(reloadResults.blockEntites);
							EnchantmentTags.setCollection(this.enchantments);
							BlockEntityTypeTags.setCollection(this.block_entity_types);
							PacketHandler.sendToAll(new S2CExtraTagsListPacket(ExtraTags.instance.extraTagManager));
						}, gameExecutor);
	}
	public static class ReloadResults {
		final Map<ResourceLocation, Tag.Builder<Enchantment>> enchantments;
		final Map<ResourceLocation, Tag.Builder<TileEntityType<?>>> blockEntites;

		public ReloadResults(Map<ResourceLocation, Tag.Builder<Enchantment>> enchantments,
												 Map<ResourceLocation, Tag.Builder<TileEntityType<?>>> blockEntities) {
			this.enchantments = enchantments;
			this.blockEntites = blockEntities;
		}
	}
}