package tfar.extratags.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class ExtraTagRegistry {

	public static final ModTag<Biome> BIOME = new ModTag<>();
	public static final ModTag<TileEntityType<?>> BLOCK_ENTITY_TYPE = new ModTag<>();
	public static final ModTag<Enchantment> ENCHANTMENT = new ModTag<>();
	public static final List<ModTag<?>> tagTypeList = new ArrayList<>();

	public ExtraTagRegistry() {
	}

	public static ITag.INamedTag<Biome> biome(String identifier) {
		return BIOME.register(identifier);
	}

	public static ITag.INamedTag<TileEntityType<?>> blockEntityType(String identifier) {
		return BLOCK_ENTITY_TYPE.register(identifier);
	}

	public static ITag.INamedTag<Enchantment> enchantment(String identifier) {
		return ENCHANTMENT.register(identifier);
	}

	public static <T> ITag.INamedTag<T> create(String identifier, ModTag<T> tags) {
		return tags.register(identifier);
	}

	static {
		tagTypeList.add(BIOME);
		tagTypeList.add(BLOCK_ENTITY_TYPE);
		tagTypeList.add(ENCHANTMENT);
	}
}
