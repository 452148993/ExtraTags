package tfar.extratags.api.tagtypes;

import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Optional;

public class BlockEntityTypeTags {

	private static TagCollection<TileEntityType<?>> collection = new TagCollection<>((location) -> Optional.empty(), "", false, "");
	private static int generation;

	public static void setCollection(TagCollection<TileEntityType<?>> collectionIn) {
		collection = collectionIn;
		++generation;
	}

	public static TagCollection<TileEntityType<?>> getCollection() {
		return collection;
	}

	public static int getGeneration() {
		return generation;
	}

	public static Tag<TileEntityType<?>> makeWrapperTag(ResourceLocation location) {
		return new Wrapper(location);
	}

	public static class Wrapper extends Tag<TileEntityType<?>> {
		private int lastKnownGeneration = -1;
		private Tag<TileEntityType<?>> cachedTag;

		public Wrapper(ResourceLocation resourceLocation) {
			super(resourceLocation);
		}

		/**
		 * Returns true if this set contains the specified element.
		 */
		@Override
		public boolean contains(TileEntityType<?> blockEntityType) {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.contains(blockEntityType);
		}

		@Override
		public Collection<TileEntityType<?>> getAllElements() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.getAllElements();
		}

		@Override
		public Collection<Tag.ITagEntry<TileEntityType<?>>> getEntries() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.getEntries();
		}
	}
}
