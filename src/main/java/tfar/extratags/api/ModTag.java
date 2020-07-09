package tfar.extratags.api;

import net.minecraft.tags.ITag;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.Set;

public class ModTag<T> {

	private final TagRegistry<T> ACCESSOR = new TagRegistry<>();

	public ModTag() {
	}

	public ITag.INamedTag<T> register(String id) {
		return this.ACCESSOR.func_232937_a_(id);
	}

	public void setContainer(TagCollection<T> container) {
		this.ACCESSOR.func_232935_a_(container);
	}

	public void markReady() {
		this.ACCESSOR.func_232932_a_();
	}

	public TagCollection<T> getContainer() {
		return this.ACCESSOR.func_232939_b_();
	}

	public Set<ResourceLocation> set(TagCollection<T> tagContainer) {
		return this.ACCESSOR.func_232940_b_(tagContainer);
	}
}
