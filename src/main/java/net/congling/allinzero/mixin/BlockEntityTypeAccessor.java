package net.congling.allinzero.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/**
 * 暴露 BlockEntityType 的 validBlocks 字段。
 * 原版床方块实体类型 BED 的合法方块集合是不可变的，
 * 自定义灵魂床需要在通用启动阶段把自己加入该集合，
 * 否则放置时 BedBlockEntity 构造校验会抛出 IllegalStateException。
 */
@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAccessor {

    @Accessor("validBlocks")
    Set<Block> allinzero$getValidBlocks();

    @Mutable
    @Accessor("validBlocks")
    void allinzero$setValidBlocks(Set<Block> blocks);
}
