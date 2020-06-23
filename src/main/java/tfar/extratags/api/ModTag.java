package tfar.extratags.api;

import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public class ModTag<T> {

	private TagCollection<T> collection = new TagCollection<>(location -> Optional.empty(), "", false, "");
	private int generation;

	public void setCollection(TagCollection<T> collection) {
		this.collection = collection;
		generation++;
	}

	public TagCollection<T> getCollection() {
		return collection;
	}

	public int getGeneration() {
		return generation;
	}

	public Tag<T> create(ResourceLocation resourceLocation) {
		return new ModTag.CachingTag<>(resourceLocation, this);
	}

	public static class CachingTag<T> extends Tag<T> {

		private final ModTag<T> tagCollection;
		private int lastKnownGeneration = -1;
		private Tag<T> cachedTag;

		public CachingTag(ResourceLocation resourceLocation, ModTag<T> tagCollection) {
			super(resourceLocation);
			this.tagCollection = tagCollection;
		}

		private void validateCache() {
			int generation = tagCollection.getGeneration();
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = tagCollection.getCollection().getOrCreate(getId());
				this.lastKnownGeneration = generation;
			}
		}

		@Override
		public boolean contains(@Nonnull T chemical) {
			validateCache();
			return this.cachedTag.contains(chemical);
		}

		@Nonnull
		@Override
		public Collection<T> getAllElements() {
			validateCache();
			return this.cachedTag.getAllElements();
		}

		@Nonnull
		@Override
		public Collection<ITagEntry<T>> getEntries() {
			validateCache();
			return this.cachedTag.getEntries();
		}
	}
}
