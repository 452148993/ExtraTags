package tfar.extratags.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import tfar.extratags.ExtraTags;
import tfar.extratags.network.PacketHandler;
import tfar.extratags.network.S2CExtraTagsListPacket;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static tfar.extratags.api.TagRegistry.*;

public class ExtraTagManager implements IFutureReloadListener {

	private final Map<NetworkTagCollection, Consumer<TagCollection>> tagCollections = new LinkedHashMap<>();

	public ExtraTagManager() {
		NetworkTagCollection<Enchantment> enchantments = new NetworkTagCollection<>(Registry.ENCHANTMENT, "tags/enchantments", "enchantment");
		tagCollections.put(enchantments, ENCHANTMENT::setCollection);
		NetworkTagCollection<TileEntityType<?>> block_entity_types = new NetworkTagCollection<>(Registry.BLOCK_ENTITY_TYPE, "tags/block_entity_types", "block_entity_type");
		tagCollections.put(block_entity_types, BLOCK_ENTITY_TYPE::setCollection);
		NetworkTagCollection<Biome> biomes = new NetworkTagCollection<>(Registry.BIOME, "tags/biomes", "biome");
		tagCollections.put(biomes, BIOME::setCollection);
		NetworkTagCollection<DimensionType> dimension_types = new NetworkTagCollection<>(Registry.DIMENSION_TYPE, "tags/dimension_types", "dimension_types");
		tagCollections.put(dimension_types, DIMENSION_TYPE::setCollection);
	}

	public void write(PacketBuffer buffer) {
		tagCollections.forEach(((networkTagCollection, tagCollectionConsumer) -> networkTagCollection.write(buffer)));
	}

	public static ExtraTagManager read(PacketBuffer buffer) {
		ExtraTagManager tagManager = new ExtraTagManager();

		tagManager.tagCollections.forEach((tagCollection, tagCollectionConsumer) -> tagCollection.read(buffer));
		return tagManager;
	}

	public void setCollections() {
		tagCollections.forEach((networkTagCollection, consumer) -> consumer.accept(networkTagCollection));
	}

	@Override
	public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler,
																				Executor backgroundExecutor, Executor gameExecutor) {
		CompletableFuture<List<TagInfo<?>>> reloadResults = CompletableFuture.completedFuture(new ArrayList<>());
		for (Map.Entry<NetworkTagCollection,Consumer<TagCollection>> tagCollection : tagCollections.entrySet()) {
			reloadResults = combine(reloadResults, tagCollection.getKey(), resourceManager, backgroundExecutor,tagCollection.getValue());
		}
		return reloadResults.thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync(results -> {
			results.forEach(TagInfo::registerAndSet);
			PacketHandler.sendToAll(new S2CExtraTagsListPacket(ExtraTags.instance.extraTagManager));
		}, gameExecutor);
	}

	private CompletableFuture<List<TagInfo<?>>> combine(CompletableFuture<List<TagInfo<?>>> reloadResults,
																													NetworkTagCollection<?> tagCollection, IResourceManager resourceManager, Executor backgroundExecutor,Consumer<TagCollection> consumer) {
		return reloadResults.thenCombine(tagCollection.reload(resourceManager, backgroundExecutor), (results, result) -> {
			results.add(new TagInfo(tagCollection, result, consumer));
			return results;
		});
	}

	public static class TagInfo<T> {

		private final NetworkTagCollection<T> tagCollection;
		final Map<ResourceLocation, Tag.Builder<T>> results;
		final Consumer<TagCollection<?>> consumer;

		private TagInfo(NetworkTagCollection<T> tagCollection, Map<ResourceLocation, Tag.Builder<T>> result, Consumer<TagCollection<?>> consumer) {
			this.tagCollection = tagCollection;
			this.results = result;
			this.consumer = consumer;
		}

		private void registerAndSet() {
			tagCollection.registerAll(results);
			consumer.accept(tagCollection);
		}
	}
}