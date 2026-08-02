package dundigundi.bwbalanceddiet.mixins.interfaces;

import com.mojang.nbt.tags.ListTag;
import dundigundi.bwbalanceddiet.ItemFoodData;
import org.jetbrains.annotations.NotNull;

public interface IPlayer {
	void better_with_balanced_diet$consumeFood(ItemFoodData itemFoodData);
	int better_with_balanced_diet$countFoodType(ItemFoodData itemFoodData);
	float better_with_balanced_diet$calculateHealMultiplier(ItemFoodData itemFoodData);
	boolean better_with_balanced_diet$listContainsItemFoodData(ItemFoodData itemFoodData);
	ListTag save(@NotNull ListTag parentTag);
	void load(@NotNull ListTag parentTag);
}
