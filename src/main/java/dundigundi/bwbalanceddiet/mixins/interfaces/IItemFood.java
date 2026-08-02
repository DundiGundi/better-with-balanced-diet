package dundigundi.bwbalanceddiet.mixins.interfaces;

import net.minecraft.core.item.ItemStack;

public interface IItemFood {
	void better_with_balanced_diet$setHealAmountMultiplier(float healAmountMultiplier);
	int better_with_balanced_diet$getDefaultHealAmount(ItemStack stack);
}
