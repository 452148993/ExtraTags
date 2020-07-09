package tfar.extratags;

import net.minecraftforge.common.util.ReverseTagWrapper;
import tfar.extratags.api.ModTag;

public class VanillaReverseTagWrapper<T> extends ReverseTagWrapper<T> {
	public VanillaReverseTagWrapper(T target, ModTag<T> tag) {
		super(target, tag::getContainer);
	}
}
