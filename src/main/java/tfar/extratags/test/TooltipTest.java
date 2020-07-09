package tfar.extratags.test;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import tfar.extratags.api.ReverseTag;

import java.util.Set;

public class TooltipTest {
	public static void onTooltip(ItemTooltipEvent e) {
		if (e.getItemStack().getItem() == Items.ENCHANTED_BOOK){
			Set<Enchantment> enchantments = EnchantmentHelper.getEnchantments(e.getItemStack()).keySet();
			if (enchantments.size() == 1) {
				e.getToolTip().add(new StringTextComponent("Enchantment Tags:").func_240699_a_(TextFormatting.AQUA));
				enchantments.forEach(enchantment -> {
					Set<ResourceLocation> tags = ((ReverseTag) enchantment).getTags();
					if (!tags.isEmpty())
						tags.forEach(resourceLocation -> e.getToolTip().add(new StringTextComponent(resourceLocation.toString())
										.func_240699_a_(TextFormatting.DARK_GRAY)));
					else e.getToolTip().add(new StringTextComponent("none").func_240699_a_(TextFormatting.DARK_GRAY));
				});
			}
		}
	}
}
