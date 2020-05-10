package tfar.extratags.api;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import tfar.extratags.ExtraTags;
import tfar.extratags.api.tagtypes.BiomeTags;
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
	private final NetworkTagCollection<Biome> biomes = new NetworkTagCollection<>(Registry.BIOME, "tags/biomes", "biome");
	private final NetworkTagCollection<DimensionType> dimension_types = new NetworkTagCollection<>(Registry.DIMENSION_TYPE, "tags/dimension_types", "dimension_types");

	public NetworkTagCollection<Enchantment> getEnchantments() {
		return this.enchantments;
	}

	public NetworkTagCollection<TileEntityType<?>> getBlockEntityTypes() {
		return this.block_entity_types;
	}

	public NetworkTagCollection<Biome> getBiomes() {
		return this.biomes;
	}

	public NetworkTagCollection<DimensionType> getDimensionTypes() {
		return this.dimension_types;
	}

	public void write(PacketBuffer buffer) {
		this.enchantments.write(buffer);
		this.block_entity_types.write(buffer);
		this.biomes.write(buffer);
		this.dimension_types.write(buffer);
	}

	public static ExtraTagManager read(PacketBuffer buffer) {
		ExtraTagManager tagManager = new ExtraTagManager();
		tagManager.getEnchantments().read(buffer);
		tagManager.getBlockEntityTypes().read(buffer);
		tagManager.getBiomes().read(buffer);
		return tagManager;
	}

	@Override
	public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		CompletableFuture<Map<ResourceLocation, Tag.Builder<Enchantment>>> enchantmentReload = this.enchantments.reload(resourceManager, backgroundExecutor);
		CompletableFuture<Map<ResourceLocation, Tag.Builder<TileEntityType<?>>>> blockEntityReload = this.block_entity_types.reload(resourceManager, backgroundExecutor);
		CompletableFuture<Map<ResourceLocation, Tag.Builder<Biome>>> biomeReload = this.biomes.reload(resourceManager, backgroundExecutor);

		return enchantmentReload.thenCombine(blockEntityReload, Pair::of)
						.thenCombine(biomeReload, (Pair<Map<ResourceLocation, Tag.Builder<Enchantment>>,
										Map<ResourceLocation, Tag.Builder<TileEntityType<?>>>> first,
																			 Map<ResourceLocation, Tag.Builder<Biome>> second) ->
										new ReloadResults(first.getFirst(), first.getSecond(), second))
						.thenCompose(stage::markCompleteAwaitingOthers)
						.thenAcceptAsync(reloadResults -> {
							this.enchantments.registerAll(reloadResults.enchantments);
							this.block_entity_types.registerAll(reloadResults.blockEntites);
							this.biomes.registerAll(reloadResults.biomes);
							EnchantmentTags.setCollection(this.enchantments);
							BlockEntityTypeTags.setCollection(this.block_entity_types);
							BiomeTags.setCollection(this.biomes);
							PacketHandler.sendToAll(new S2CExtraTagsListPacket(ExtraTags.instance.extraTagManager));
						}, gameExecutor);
	}

	public static class ReloadResults {
		final Map<ResourceLocation, Tag.Builder<Enchantment>> enchantments;
		final Map<ResourceLocation, Tag.Builder<TileEntityType<?>>> blockEntites;
		final Map<ResourceLocation, Tag.Builder<Biome>> biomes;

		public ReloadResults(Map<ResourceLocation, Tag.Builder<Enchantment>> enchantments,
												 Map<ResourceLocation, Tag.Builder<TileEntityType<?>>> blockEntities,
												 Map<ResourceLocation, Tag.Builder<Biome>> biomes) {
			this.enchantments = enchantments;
			this.blockEntites = blockEntities;
			this.biomes = biomes;
		}
	}
}