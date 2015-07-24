package com.pauljoda.modularsystems.power.tiles;

import com.pauljoda.modularsystems.core.tiles.DummyTile;
import com.pauljoda.modularsystems.generator.tiles.TileGeneratorCore;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Modular-Systems
 * Created by Dyonovan on 24/07/15
 */
public class TileSupplierBase extends DummyTile {

    protected TileGeneratorCore genCore;

    @Override
    public void updateEntity() { //TODO make sure output isnt same as input
        if (validCore())
            genCore = (TileGeneratorCore) getCore();
        else
            genCore = null;
    }

    public boolean validCore() {
        return getCore() != null && worldObj.getTileEntity(coreLocation.x, coreLocation.y, coreLocation.z) instanceof TileGeneratorCore;
    }

    /*
     * Waila Methods
     */
    @Override
    public NBTTagCompound returnNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
        if (tag.hasKey("Energy")) {
            tag.removeTag("Energy");
            tag.removeTag("MaxStorage");
        }
        return tag;
    }
}
