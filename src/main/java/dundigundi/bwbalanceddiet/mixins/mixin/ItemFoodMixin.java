package dundigundi.bwbalanceddiet.mixins.mixin;

import dundigundi.bwbalanceddiet.mixins.interfaces.IItemFood;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.core.item.ItemBucket.getBucketState;

@Mixin(value = ItemFood.class, remap = false)
public abstract class ItemFoodMixin implements IItemFood {

	@Mutable
	@Shadow
	@Final
	private int healAmount;

	@Unique
	public int defaultHealAmount;

	@Unique
	public float healAmountMultiplier;

	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void setDefaultHealAmount(String name, String namespaceId, int id, int healAmount, int ticksPerHeal, boolean favouriteWolfMeat, int maxStackSize, CallbackInfo ci) {
		// no need to get the defaultHealAmount for buckets, because bucket healAmount can't be set
		defaultHealAmount = healAmount;
	}

	//TODO: buckets can go to hell
	@Override
	public int better_with_balanced_diet$getDefaultHealAmount(ItemStack itemStack) {
		if ((Object) this instanceof ItemBucket) {
			//LOGGER.info(String.valueOf(getBucketState(ItemBucket.getState(itemStack)).healAmount()));
			return getBucketState(ItemBucket.getState(itemStack)).healAmount();
		}

		return defaultHealAmount;
	}

	//this sets the healAmount for all ItemFoods, except for buckets
	@Override
	public void better_with_balanced_diet$setHealAmountMultiplier(float healAmountMultiplier) {
		this.healAmountMultiplier = healAmountMultiplier;
		healAmount = (int) (defaultHealAmount * healAmountMultiplier);
	}
}
