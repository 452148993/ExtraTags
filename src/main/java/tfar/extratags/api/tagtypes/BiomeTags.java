package tfar.extratags.api.tagtypes;

import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.Collection;
import java.util.Optional;

public class BiomeTags {

	private static TagCollection<Biome> collection = new TagCollection<>((location) -> Optional.empty(), "", false, "");
	private static int generation;

	public static void setCollection(TagCollection<Biome> collectionIn) {
		collection = collectionIn;
		++generation;
	}

	public static TagCollection<Biome> getCollection() {
		return collection;
	}

	public static int getGeneration() {
		return generation;
	}

	public static Tag<Biome> makeWrapperTag(ResourceLocation location) {
		return new Wrapper(location);
	}

	public static class Wrapper extends Tag<Biome> {
		private int lastKnownGeneration = -1;
		private Tag<Biome> cachedTag;

		public Wrapper(ResourceLocation resourceLocation) {
			super(resourceLocation);
		}

		/**
		 * Returns true if this set contains the specified element.
		 */
		@Override
		public boolean contains(Biome biome) {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.contains(biome);
		}

		@Override
		public Collection<Biome> getAllElements() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.getAllElements();
		}

		@Override
		public Collection<ITagEntry<Biome>> getEntries() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.getEntries();
		}
	}
}
