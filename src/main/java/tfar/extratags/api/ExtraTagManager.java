package tfar.extratags.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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
import tfar.extratags.ExtraTagsContainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ExtraTagManager implements IFutureReloadListener {
		private final List<NetworkTagCollection<?>> tagCollections = new ArrayList<>();

		public ExtraTagManager() {
			NetworkTagCollection<Biome> biomes = new NetworkTagCollection<>(Registry.BIOME, "tags/biomes", "biome");
			this.tagCollections.add(biomes);
			NetworkTagCollection<TileEntityType<?>> block_entity_types = new NetworkTagCollection<>(Registry.BLOCK_ENTITY_TYPE, "tags/block_entity_types", "block_entity_type");
			this.tagCollections.add(block_entity_types);
			NetworkTagCollection<Enchantment> enchantments = new NetworkTagCollection<>(Registry.ENCHANTMENT, "tags/enchantments", "enchantment");
			this.tagCollections.add(enchantments);
		}

		public void write(PacketBuffer buffer) {
			this.tagCollections.forEach((registryTagContainer) -> {
				registryTagContainer.write(buffer);
			});
		}

		public static ExtraTagManager read(PacketBuffer buffer) {
			ExtraTagManager tagManager = new ExtraTagManager();
			tagManager.tagCollections.forEach((tagCollection) -> {
				tagCollection.read(buffer);
			});
			return tagManager;
		}

		public CompletableFuture<Void> reload(IStage synchronizer, IResourceManager manager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor prepareExecutor, Executor applyExecutor) {
			List<CompletableFuture<Map<ResourceLocation, Tag.Builder>>> completableFutureList = new ArrayList();

			for (NetworkTagCollection<?> tagCollection : this.tagCollections) {
				completableFutureList.add(tagCollection.reload(manager, prepareExecutor));
			}

			CompletableFuture<Void> completableFutureVoid = CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0]));
			return completableFutureVoid.thenCompose(synchronizer::markCompleteAwaitingOthers).thenAcceptAsync((void_) -> {
				for(int i = 0; i < this.tagCollections.size(); ++i) {
					NetworkTagCollection<?> registryTagContainer = this.tagCollections.get(i);
					registryTagContainer.registerAll(completableFutureList.get(i).join());
				}

				ExtraTagsContainers.setHolder(this.tagCollections);
				Multimap<String, ResourceLocation> multimap = HashMultimap.create();

				for(int i = 0; i < this.tagCollections.size(); ++i) {
					NetworkTagCollection registryTagContainerx = this.tagCollections.get(i);
					String entryType = registryTagContainerx.itemTypeName;
					multimap.putAll(entryType, (ExtraTagRegistry.tagTypeList.get(i)).set(registryTagContainerx));
				}

				if (!multimap.isEmpty()) {
					throw new IllegalStateException("Missing required tags: " + multimap.entries().stream()
									.map((entry) -> entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(",")));
				}
			}, applyExecutor);
		}

		public void sync() {
			for(int i = 0; i < this.tagCollections.size(); ++i) {
				ModTag modTag = ExtraTagRegistry.tagTypeList.get(i);
				modTag.setContainer(this.tagCollections.get(i));
			}
		}
}