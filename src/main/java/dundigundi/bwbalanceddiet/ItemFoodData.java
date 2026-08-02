package dundigundi.bwbalanceddiet;

import net.minecraft.core.item.ItemStack;

public class ItemFoodData {
	public int itemID;
	public String bucketState;

	public ItemFoodData(ItemStack itemStack) {
		itemID = itemStack.itemID;
		// item is either iron or steel bucket
		bucketState = (itemID == 16562 || itemID == 16563) ? itemStack.getData().getString("State") : "";
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
}
