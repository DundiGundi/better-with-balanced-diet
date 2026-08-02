package dundigundi.bwbalanceddiet.mixins.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import dundigundi.bwbalanceddiet.ItemFoodData;
import dundigundi.bwbalanceddiet.mixins.interfaces.IItemFood;
import dundigundi.bwbalanceddiet.mixins.interfaces.IPlayer;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
			if (better_with_balanced_diet$countFoodType(first) == 0) first.removeFromMap(consumedFoodsMultiplier);
		}
		itemFoodData.putInMapOrEdit(consumedFoodsMultiplier, better_with_balanced_diet$calculateHealMultiplier(itemFoodData));
		better_with_balanced_diet$refreshHealMultipliers();

		//LOGGER.info(String.valueOf(itemFoodData.getFromMap(consumedFoodsMultiplier)));
		//LOGGER.info(String.valueOf(better_with_balanced_diet$countFoodType(itemFoodData)));
	}

	@Override
	public float better_with_balanced_diet$calculateHealMultiplier(ItemFoodData itemFoodData) {
		int count = better_with_balanced_diet$countFoodType(itemFoodData);
		float healAmountMultiplier = 1;

		if (count >= (consumedFoodHistory * 0.9)) {
			healAmountMultiplier = 0.25f;
		}
		else if (count >= (consumedFoodHistory * 0.75)) {
			healAmountMultiplier = 0.5f;
		}
		else if (count >= (consumedFoodHistory * 0.5)) {
			healAmountMultiplier = 0.75f;
		}

		//LOGGER.info("healAmountMultiplier and count: {}, {}\n##########", healAmountMultiplier, count);
		return healAmountMultiplier;
	}

	@Override
	public int better_with_balanced_diet$countFoodType(ItemFoodData itemFoodData) {
		if (better_with_balanced_diet$listContainsItemFoodData(itemFoodData)) {
			int count = 0;
			for (int i = 0; i < recentConsumedFoods.size(); i++) {
				if (recentConsumedFoods.get(i).compare(itemFoodData)) count++;
			}
			//LOGGER.info("count: {}", count);
			//LOGGER.info("bucketstate: {}", itemFoodData.bucketState);
			return count;
		}else {
			return 0;
		}
	}

	@WrapMethod(method = "getHeldItem")
	private ItemStack addMultiplier(Operation<ItemStack> original) {
		ItemStack held = this.inventory.getCurrentItem();
		if (held != null && held.getItem() instanceof ItemFood) {
			((IItemFood)held.getItem()).better_with_balanced_diet$setHealAmountMultiplier(better_with_balanced_diet$getHealMultiplier(new ItemFoodData(held)));
		}
		return held;
	}

	@Override
	public float better_with_balanced_diet$getHealMultiplier(ItemFoodData itemFoodData) {
		float f = itemFoodData.getFromMap(consumedFoodsMultiplier);
		if (f < 0) {
			return better_with_balanced_diet$calculateHealMultiplier(itemFoodData);
		} else {
			return f;
		}
	}

	@Override
	public boolean better_with_balanced_diet$listContainsItemFoodData(ItemFoodData itemFoodData) {
		for (ItemFoodData recentFoodData : recentConsumedFoods) {
			if (recentFoodData.compare(itemFoodData)) return true;
		}
		return false;
	}

	@Override
	public void better_with_balanced_diet$refreshHealMultipliers() {
		for(Map.Entry<ItemFoodData, Float> set : consumedFoodsMultiplier.entrySet()) {
			set.setValue(better_with_balanced_diet$calculateHealMultiplier(set.getKey()));
		}
	}

	@WrapMethod(method = "addAdditionalSaveData")
	private void saveRecentConsumedFoods(CompoundTag tag, Operation<Void> original) {
		tag.putList("RecentConsumedFoods", better_with_balanced_diet$saveFoods(new ListTag()));
		tag.putList("ConsumedFoodsMultiplier", better_with_balanced_diet$saveHealMultiplier(new ListTag()));
		//LOGGER.info("save:");
		//LOGGER.info("Recent consumed foods: {}", ItemFoodData.printList(recentConsumedFoods));
		//LOGGER.info("Multipliers: {}", ItemFoodData.printMap(consumedFoodsMultiplier));
		original.call(tag);
	}

	@WrapMethod(method = "readAdditionalSaveData")
	private void loadRecentConsumedFoods(CompoundTag tag, Operation<Void> original) {
		better_with_balanced_diet$loadFoods(tag.getList("RecentConsumedFoods"));
		better_with_balanced_diet$loadHealMultiplier(tag.getList("ConsumedFoodsMultiplier"));
		//LOGGER.info("load:");
		//LOGGER.info("Recent consumed foods: {}", ItemFoodData.printList(recentConsumedFoods));
		//LOGGER.info("Multipliers: {}", ItemFoodData.printMap(consumedFoodsMultiplier));
		original.call(tag);
	}

	@NotNull
	@Override
	public ListTag better_with_balanced_diet$saveFoods(@NotNull ListTag parentTag) {
		for (ItemFoodData itemFoodData : recentConsumedFoods) {
			parentTag.addTag(itemFoodData.generateCompoundTag());
		}
		return parentTag;
	}
	@NotNull
	@Override
	public ListTag better_with_balanced_diet$saveHealMultiplier(@NotNull ListTag parentTag) {
		for (Map.Entry<ItemFoodData, Float> set : consumedFoodsMultiplier.entrySet()) {
			CompoundTag tag = new CompoundTag();
			tag.putInt("ItemID", set.getKey().itemID);
			tag.putString("BucketState", set.getKey().bucketState);
			tag.putFloat("HealAmountMultiplier", set.getValue());
			parentTag.addTag(tag);
		}
		return parentTag;
	}

	@Override
	public void better_with_balanced_diet$loadFoods(@NotNull ListTag parentTag) {
		for(int i = 0; i < parentTag.tagCount(); ++i) {
			CompoundTag tag = (CompoundTag)parentTag.tagAt(i);
			recentConsumedFoods.add(new ItemFoodData(tag));
		}
	}
	@Override
	public void better_with_balanced_diet$loadHealMultiplier(@NotNull ListTag parentTag) {
		for(int i = 0; i < parentTag.tagCount(); ++i) {
			CompoundTag tag = (CompoundTag)parentTag.tagAt(i);
			consumedFoodsMultiplier.put(new ItemFoodData(tag.getInteger("ItemID"), tag.getString("BucketState")), tag.getFloat("HealAmountMultiplier"));
			//LOGGER.info(String.valueOf(tag.getInteger("ItemID")));
		}
	}
}
