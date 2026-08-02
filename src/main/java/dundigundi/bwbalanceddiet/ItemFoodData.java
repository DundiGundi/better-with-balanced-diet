package dundigundi.bwbalanceddiet;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.item.ItemStack;

import java.util.LinkedList;
import java.util.Map;

public class ItemFoodData {
	public int itemID;
	public String bucketState;

	public ItemFoodData(int itemID, String bucketState) {
		this.itemID = itemID;
		this.bucketState = bucketState;
	}

	public ItemFoodData(ItemStack itemStack) {
		itemID = itemStack.itemID;
		// item is either iron or steel bucket
		bucketState = (itemID == 16562 || itemID == 16563) ? itemStack.getData().getString("State") : "";
	}

	public ItemFoodData(CompoundTag compoundTag) {
		this.itemID = compoundTag.getInteger("ItemID");
		this.bucketState = compoundTag.getStringOrDefault("BucketState", "");
	}

	public boolean compare(ItemFoodData itemFoodData) {
		if (itemFoodData.itemID == this.itemID) {

			if (itemFoodData.bucketState.isEmpty()) {
				return true;
			}
			else return itemFoodData.bucketState.equals(this.bucketState);
		}
		return false;
	}

	public CompoundTag generateCompoundTag() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("ItemID", itemID);
		if (!bucketState.isEmpty()) tag.putString("BucketState", bucketState);

		return tag;
	}

	public void putInMapOrEdit(Map<ItemFoodData, Float> map, float multiplier) {
		for(Map.Entry<ItemFoodData, Float> set : map.entrySet()) {
			if (this.compare(set.getKey())) {
				set.setValue(multiplier);
				return;
			}
		}
		map.put(this, multiplier);
	}

	public void removeFromMap(Map<ItemFoodData, Float> map) {
		ItemFoodData itemFoodData = null;
		for(Map.Entry<ItemFoodData, Float> set : map.entrySet()) {
			if (this.compare(set.getKey())) {
				itemFoodData = set.getKey();
				break;
			}
		}
		if (itemFoodData != null) map.remove(itemFoodData);
	}

	public float getFromMap(Map<ItemFoodData, Float> map) {
		for(Map.Entry<ItemFoodData, Float> set : map.entrySet()) {
			if (this.compare(set.getKey())) {
				return set.getValue();
			}
		}
		return -1f;
	}

	public boolean containsMap(Map<ItemFoodData, Float> map) {
		for(Map.Entry<ItemFoodData, Float> set : map.entrySet()) {
			if (this.compare(set.getKey())) {
				return true;
			}
		}
		return false;
	}

	public String print() {
		return String.format("( %d, %s)", itemID, bucketState);
	}

	public static String printMap(Map<ItemFoodData, Float> map) {
		String string = "[";
		for(Map.Entry<ItemFoodData, Float> set : map.entrySet()) {
			string = string.concat(String.format("( %s, %s )", set.getKey().print(), String.valueOf(set.getValue())));
		}
		return string.concat("]");
	}

	public static String printList(LinkedList<ItemFoodData> list) {
		String string = "[";
		for(ItemFoodData itemFoodData : list) {
			string = string.concat(itemFoodData.print());
		}
		return string.concat("]");
	}
}
