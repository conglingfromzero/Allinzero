package net.congling.allinzero.entity.touling.animal.soulsheep;

import net.congling.allinzero.blocks.AllinzeroBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public class SoulSheepEntity extends Sheep {
    public SoulSheepEntity(EntityType<? extends Sheep> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal ->
                goal instanceof BreedGoal
                        || goal instanceof TemptGoal
                        || goal instanceof FollowParentGoal);
    }


    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, (double)100.0F);
        builder = builder.add(Attributes.FOLLOW_RANGE, (double)16.0F);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
        return builder;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public Sheep getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void shear(SoundSource category) {
        this.level().playSound(null, this, net.minecraft.sounds.SoundEvents.SHEEP_SHEAR, category, 1.0F, 1.0F);
        this.setSheared(true);
        this.spawnAtLocation(new ItemStack(AllinzeroBlocks.SOUL_WOOL.get()), 1);
    }

    @Override
    public ResourceKey<LootTable> getDefaultLootTable() {
        if (this.isSheared()) {
            return this.getType().getDefaultLootTable();
        }
        return ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.parse("allinzero:entities/soul_sheep_wooled"));
    }
}
