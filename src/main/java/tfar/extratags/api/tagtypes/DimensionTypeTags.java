package tfar.extratags.api.tagtypes;

import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import java.util.Collection;
import java.util.Optional;

public class DimensionTypeTags {

	private static TagCollection<DimensionType> collection = new TagCollection<>((location) -> Optional.empty(), "", false, "");
	private static int generation;

	public static void setCollection(TagCollection<DimensionType> collectionIn) {
		collection = collectionIn;
		++generation;
	}

	public static TagCollection<DimensionType> getCollection() {
		return collection;
	}

	public static int getGeneration() {
		return generation;
	}

	public static Tag<DimensionType> makeWrapperTag(ResourceLocation location) {
		return new Wrapper(location);
	}

	public static class Wrapper extends Tag<DimensionType> {
		private int lastKnownGeneration = -1;
		private Tag<DimensionType> cachedTag;

		public Wrapper(ResourceLocation resourceLocation) {
			super(resourceLocation);
		}

		/**
		 * Returns true if this set contains the specified element.
		 */
		@Override
		public boolean contains(DimensionType dimensionType) {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.contains(dimensionType);
		}

		@Override
		public Collection<DimensionType> getAllElements() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.getAllElements();
		}

		@Override
		public Collection<ITagEntry<DimensionType>> getEntries() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.getEntries();
		}
	}
}
