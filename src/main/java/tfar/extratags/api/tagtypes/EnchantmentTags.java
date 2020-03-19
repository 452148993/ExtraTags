package tfar.extratags.api.tagtypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Optional;

public class EnchantmentTags {

	private static TagCollection<Enchantment> collection = new TagCollection<>((location) -> Optional.empty(), "", false, "");
	private static int generation;

	public static void setCollection(TagCollection<Enchantment> collectionIn) {
		collection = collectionIn;
		++generation;
	}

	public static TagCollection<Enchantment> getCollection() {
		return collection;
	}

	public static int getGeneration() {
		return generation;
	}

	public static Tag<Enchantment> makeWrapperTag(ResourceLocation location) {
		return new Wrapper(location);
	}

	public static class Wrapper extends Tag<Enchantment> {
		private int lastKnownGeneration = -1;
		private Tag<Enchantment> cachedTag;

		public Wrapper(ResourceLocation resourceLocation) {
			super(resourceLocation);
		}

		/**
		 * Returns true if this set contains the specified element.
		 */
		@Override
		public boolean contains(Enchantment enchantment) {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.contains(enchantment);
		}

		@Override
		public Collection<Enchantment> getAllElements() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.getAllElements();
		}

		@Override
		public Collection<Tag.ITagEntry<Enchantment>> getEntries() {
			if (this.lastKnownGeneration != generation) {
				this.cachedTag = collection.getOrCreate(this.getId());
				this.lastKnownGeneration = generation;
			}
			return this.cachedTag.getEntries();
		}
	}
}
