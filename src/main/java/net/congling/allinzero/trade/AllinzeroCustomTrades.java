package net.congling.allinzero.trade;

import net.congling.allinzero.Allinzero;
import net.congling.allinzero.items.AllinzeroItems;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = Allinzero.MODID)
public class AllinzeroCustomTrades {
    @SubscribeEvent
    public static void addTrades(VillagerTradesEvent event){
        if (event.getType() == VillagerProfession.WEAPONSMITH){
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add((trader, random) -> new MerchantOffer(
                    new ItemCost(AllinzeroItems.GHOST_STONE_BLOCK, 10),
                    Optional.of(new ItemCost(AllinzeroItems.SOULSTONE, 3)),
                    new ItemStack(AllinzeroItems.SOUL_UPGRADE_SMITHING_TEMPLATE.get(), 1),
                    2,20,1.0F
            ));
        }
    }
}
