package net.congling.allinzero.datagen;

import net.congling.allinzero.Allinzero;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AllinzeroItemModelProvider extends ItemModelProvider {
    public AllinzeroItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Allinzero.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
