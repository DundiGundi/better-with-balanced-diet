package dundigundi.bwbalanceddiet.mixins.mixin;

import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static dundigundi.bwbalanceddiet.BWBalancedDiet.LOGGER;
import static net.minecraft.core.item.ItemBucket.getBucketState;
import static net.minecraft.core.item.ItemBucket.getState;

@Mixin(value = ItemBucket.class, remap = false)
public abstract class ItemBucketMixin extends ItemFoodMixin{
	/**
	 * @author DundiGundi
	 * @reason multiplying healAmount before sending it out
	 */
	@Overwrite
	public int getHealAmount(ItemStack stack) {
		LOGGER.info("healMultiplier: {}", healAmountMultiplier);
		return (int) (getBucketState(getState(stack)).healAmount() * healAmountMultiplier);
	}
}
