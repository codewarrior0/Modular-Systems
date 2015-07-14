package com.pauljoda.modularsystems.power.tiles;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import com.pauljoda.modularsystems.core.providers.FuelProvider;
import com.pauljoda.modularsystems.core.tiles.DummyTile;
import com.teambr.bookshelf.api.waila.IWaila;
import com.teambr.bookshelf.helpers.GuiHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class TilePowerBase extends DummyTile implements IEnergyHandler, FuelProvider, IWaila {

    protected EnergyStorage energy;
    protected int priority;

    public TilePowerBase() {
        priority = 0;
    }

    public int getPowerLevelScaled(int scale) {
        return energy.getEnergyStored() * scale / energy.getMaxEnergyStored();
    }

    @Override
    public void readFromNBT (NBTTagCompound tags) {
        super.readFromNBT(tags);
        energy.readFromNBT(tags);
        priority = tags.getInteger("priority");
    }

    @Override
    public void writeToNBT (NBTTagCompound tags) {
        super.writeToNBT(tags);
        energy.writeToNBT(tags);
        tags.setInteger("priority", priority);
    }

    /*
     * Energy Functions
     */

    @Override
    public int receiveEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return 0;
    }

    @Override
    public int extractEnergy(ForgeDirection forgeDirection, int i, boolean b) {
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
    public boolean canConnectEnergy(ForgeDirection forgeDirection) {
        return false;
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
        return 0;
    }

    @Override
    public double consume() {
        return 0;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public FuelProviderType type() {
        return FuelProviderType.POWER;
    }

    public void setPriority(int value) {
        priority = value;
    }

    /*
     * Waila Info
     */

    @Override
    public void returnWailaHead(List<String> list) {
        list.add(GuiHelper.GuiColor.YELLOW + "Available Power: " + GuiHelper.GuiColor.WHITE + energy.getEnergyStored() + "/" + energy.getMaxEnergyStored());
    }

    @Override
    public void returnWailaBody(List<String> list) {

    }

    @Override
    public void returnWailaTail(List<String> list) {
        list.add(GuiHelper.GuiColor.ORANGE + "§oShift+Click to access GUI");
    }

    @Override
    public ItemStack returnWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }
}
