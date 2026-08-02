package dundigundi.bwbalanceddiet.mixins.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dundigundi.bwbalanceddiet.ItemFoodData;
import dundigundi.bwbalanceddiet.mixins.interfaces.IPlayer;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Mob.class, remap = false)
public abstract class MobMixin {
	@WrapMethod(method = "eatFood")
	private void rememberEatenFood(ItemStack stack, Operation<Void> original) {
		if ((Object) this instanceof Player) {
			((IPlayer)this).better_with_balanced_diet$consumeFood(new ItemFoodData(stack));
		}
		original.call(stack);
	}
}
