package dundigundi.bwbalanceddiet.mixins.interfaces;

import com.mojang.nbt.tags.ListTag;
import dundigundi.bwbalanceddiet.ItemFoodData;
import org.jetbrains.annotations.NotNull;

public interface IPlayer {
	void better_with_balanced_diet$consumeFood(ItemFoodData itemFoodData);
	int better_with_balanced_diet$countFoodType(ItemFoodData itemFoodData);
	float better_with_balanced_diet$calculateHealMultiplier(ItemFoodData itemFoodData);
	void better_with_balanced_diet$refreshHealMultipliers();
	float better_with_balanced_diet$getHealMultiplier(ItemFoodData itemFoodData);
	boolean better_with_balanced_diet$listContainsItemFoodData(ItemFoodData itemFoodData);
	ListTag better_with_balanced_diet$saveFoods(@NotNull ListTag parentTag);
	ListTag better_with_balanced_diet$saveHealMultiplier(@NotNull ListTag parentTag);
	void better_with_balanced_diet$loadFoods(@NotNull ListTag parentTag);
	void better_with_balanced_diet$loadHealMultiplier(@NotNull ListTag parentTag);
}
