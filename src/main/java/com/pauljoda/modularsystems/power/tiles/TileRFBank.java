package com.pauljoda.modularsystems.power.tiles;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.pauljoda.modularsystems.power.container.ContainerRFBank;
import com.pauljoda.modularsystems.power.gui.GuiRFBank;
import com.teambr.bookshelf.common.tiles.IOpensGui;
import com.teambr.bookshelf.helpers.GuiHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class TileRFBank extends TilePowerBase implements IOpensGui, IEnergyHandler {

    public static final int RF_PROCESS = 80;
    private EnergyStorage energy;

    public TileRFBank() {
        energy = new EnergyStorage(10000);
    }

    @Override
    public int getPowerLevelScaled(int scale) {
        return energy.getEnergyStored() * scale / energy.getMaxEnergyStored();
    }

    /*
     * Fuel Provider Functions
     */

    @Override
    public boolean canProvide() {
        return energy.getEnergyStored() > 0;
    }

    @Override
    public double fuelProvided() {
        return energy.extractEnergy(RF_PROCESS, true);
    }

    @Override
    public double consume() {
        int actual = energy.extractEnergy(RF_PROCESS, false);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return actual;
    }

    /*
     * RF Functions
     */

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (getCore() != null) {
            int actual = energy.receiveEnergy(maxReceive, simulate);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            return actual;
        }
        return 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection forgeDirection) {
        return energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return energy.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    /*
     * Gui Functions
     */

    @Override
    public Object getServerGuiElement(int i, EntityPlayer entityPlayer, World world, int i1, int i2, int i3) {
        return new ContainerRFBank();
    }

    @Override
    public Object getClientGuiElement(int i, EntityPlayer entityPlayer, World world, int i1, int i2, int i3) {
        return new GuiRFBank(this);
    }

    /*
     * Tile Entity Functions
     */
    @Override
    public void readFromNBT (NBTTagCompound tags) {
        super.readFromNBT(tags);
        energy.readFromNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags) {
        super.writeToNBT(tags);
        energy.writeToNBT(tags);
    }

    /*
     * Waila Functions
     */
    @Override
    public void returnWailaHead(List<String> list) {
        list.add(GuiHelper.GuiColor.YELLOW + "Available Power: " + GuiHelper.GuiColor.WHITE + energy.getEnergyStored() + "/" + energy.getMaxEnergyStored());
    }
}
