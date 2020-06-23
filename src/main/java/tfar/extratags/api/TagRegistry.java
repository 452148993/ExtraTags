package tfar.extratags.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class TagRegistry {

	public static final ModTag<Biome> BIOME = new ModTag<>();
	public static final ModTag<TileEntityType<?>> BLOCK_ENTITY_TYPE = new ModTag<>();
	public static final ModTag<DimensionType> DIMENSION_TYPE = new ModTag<>();
	public static final ModTag<Enchantment> ENCHANTMENT = new ModTag<>();

	public static Tag<Biome> biome(ResourceLocation resourceLocation) {
		return BIOME.create(resourceLocation);
	}

	public static Tag<TileEntityType<?>> blockEntityType(ResourceLocation resourceLocation) {
		return BLOCK_ENTITY_TYPE.create(resourceLocation);
	}

	public static Tag<DimensionType> dimensionType(ResourceLocation resourceLocation) {
		return DIMENSION_TYPE.create(resourceLocation);
	}

	public static Tag<Enchantment> enchantment(ResourceLocation resourceLocation) {
		return ENCHANTMENT.create(resourceLocation);
	}

	public static <T> Tag<T> create(ResourceLocation resourceLocation, ModTag<T> tags) {
		return tags.create(resourceLocation);
	}
}
