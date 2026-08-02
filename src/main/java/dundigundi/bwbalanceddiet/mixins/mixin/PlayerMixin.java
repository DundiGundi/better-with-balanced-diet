package dundigundi.bwbalanceddiet.mixins.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dundigundi.bwbalanceddiet.ItemFoodData;
import dundigundi.bwbalanceddiet.mixins.interfaces.IItemFood;
import dundigundi.bwbalanceddiet.mixins.interfaces.IPlayer;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static dundigundi.bwbalanceddiet.BWBalancedDiet.LOGGER;

//TODO: instantheal?
@Mixin(value = Player.class, remap = false)
public abstract class PlayerMixin implements IPlayer {
	@Shadow
	@Final
	@NotNull
	public ContainerInventory inventory;

	@Unique
	public LinkedList<ItemFoodData> recentConsumedFoods = new LinkedList<>();
	@Unique
	private final int consumedFoodHistory = 10;
	@Unique
	public Map<ItemFoodData, Float> consumedFoodsMultiplier = new HashMap<>();


	@Override
	public void better_with_balanced_diet$consumeFood(ItemFoodData itemFoodData) {
		recentConsumedFoods.addLast(itemFoodData);
		if (recentConsumedFoods.size() > consumedFoodHistory) {
			ItemFoodData first = recentConsumedFoods.getFirst();
			recentConsumedFoods.removeFirst();
			if (better_with_balanced_diet$countFoodType(first) == 0) consumedFoodsMultiplier.remove(first);
		}
		//LOGGER.info(String.valueOf(recentConsumedFoods));
		consumedFoodsMultiplier.putIfAbsent(itemFoodData, better_with_balanced_diet$calculateHealMultiplier(itemFoodData));
	}

	@Override
	public float better_with_balanced_diet$calculateHealMultiplier(ItemFoodData itemFoodData) {
		int count = better_with_balanced_diet$countFoodType(itemFoodData);
		float healMultiplier = 1;

		if (count >= (consumedFoodHistory * 0.9)) {
			healMultiplier = 0.25f;
			//iItemFood.better_with_cheese$setSaturationMultiplier(0.25f);
			//healAmount = (int) (healAmount * 0.25);
		}
		else if (count >= (consumedFoodHistory * 0.75)) {
			healMultiplier = 0.5f;
			//iItemFood.better_with_cheese$setSaturationMultiplier(0.5f);
			//healAmount = (int) (healAmount * 0.5);
		}
		else if (count >= (consumedFoodHistory * 0.5)) {
			healMultiplier = 0.75f;
			//iItemFood.better_with_cheese$setSaturationMultiplier(0.75f);
			//healAmount = (int) (healAmount * 0.75);
		}
		//if (count < (consumedFoodHistory * 0.5)) {
			//iItemFood.better_with_cheese$setSaturationMultiplier(1);
		//}

		//LOGGER.info("healAmount: {} \n##########", healAmount);
		return healMultiplier;
	}

	@Override
	public int better_with_balanced_diet$countFoodType(ItemFoodData itemFoodData) {
		if (better_with_balanced_diet$listContainsItemFoodData(itemFoodData)) {
			int count = 0;
			for (int i = 0; i < recentConsumedFoods.size(); i++) {
				if (recentConsumedFoods.get(i).compare(itemFoodData)) count++;
			}
			LOGGER.info("count: {}", count);
			LOGGER.info("bucketstate: {}", itemFoodData.bucketState);
			return count;
		}else {
			return 0;
		}
	}

	@WrapMethod(method = "getHeldItem")
	private ItemStack addMultiplier(Operation<ItemStack> original) {
		ItemStack held = this.inventory.getCurrentItem();
		if (held != null && held.getItem() instanceof ItemFood) {
			((IItemFood)held.getItem()).better_with_balanced_diet$setHealAmountMultiplier(better_with_balanced_diet$calculateHealMultiplier(new ItemFoodData(held)));
		}
		return held;
	}

	@Override
	public boolean better_with_balanced_diet$listContainsItemFoodData(ItemFoodData itemFoodData) {
		for (ItemFoodData recentFoodData : recentConsumedFoods) {
			if (recentFoodData.compare(itemFoodData)) return true;
		}
		return false;
	}

	/*@WrapMethod(method = "addAdditionalSaveData")
	private void saveRecentConsumedFoods(CompoundTag tag, Operation<Void> original) {
		tag.putList("RecentConsumedFoods", better_with_cheese$save(new ListTag()));
		original.call(tag);
	}

	@WrapMethod(method = "readAdditionalSaveData")
	private void loadRecentConsumedFoods(CompoundTag tag, Operation<Void> original) {
		better_with_cheese$load(tag.getList("RecentConsumedFoods"));
		original.call(tag);
	}

	//TODO: save map too
	@NotNull
	@Override
	public ListTag better_with_cheese$save(@NotNull ListTag parentTag) {
		for (ItemFood itemFood : recentConsumedFoods) {
			CompoundTag itemTag = new CompoundTag();
			itemTag.putInt("ItemFood", itemFood.id);
			parentTag.addTag(itemTag);
		}

		return parentTag;
	}

	@Override
	public void better_with_cheese$load(@NotNull ListTag parentTag) {
		for(int i = 0; i < parentTag.tagCount(); ++i) {
			CompoundTag itemTag = (CompoundTag)parentTag.tagAt(i);
			recentConsumedFoods.add((ItemFood) ItemFood.getItem(itemTag.getInteger("ItemFood")));
		}

	}*/
}
