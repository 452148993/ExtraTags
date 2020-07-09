package tfar.extratags;

import net.minecraft.tags.TagCollection;
import tfar.extratags.api.ExtraTagRegistry;
import tfar.extratags.api.ModTag;

import java.util.Collection;
import java.util.stream.Collectors;

public class ExtraTagsContainers {
	private static volatile ExtraTagsContainers extraTagContainers;
	private final Collection<? extends TagCollection> containers;

	private ExtraTagsContainers(Collection<? extends TagCollection> containers) {
		this.containers = containers;
	}

	public static ExtraTagsContainers instance() {
		return extraTagContainers;
	}

	public static void setHolder(Collection<? extends TagCollection> tagContainerHolders) {
		extraTagContainers = new ExtraTagsContainers(tagContainerHolders);
	}

	static {
		extraTagContainers = new ExtraTagsContainers(ExtraTagRegistry.tagTypeList.stream().map(ModTag::getContainer).collect(Collectors.toList()));
	}
}