package com.pauljoda.modularsystems.power.tiles;

import cofh.api.energy.EnergyStorage;
import com.pauljoda.modularsystems.core.registries.FluidFuelValues;
import com.pauljoda.modularsystems.power.container.ContainerLiquidsBank;
import com.pauljoda.modularsystems.power.gui.GuiLiquidsBank;
import com.teambr.bookshelf.collections.InventoryTile;
import com.teambr.bookshelf.common.tiles.IOpensGui;
import com.teambr.bookshelf.helpers.GuiHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.List;

public class TileLiquidsBank extends TilePowerBase implements IOpensGui, IFluidHandler {

    public static final int BUCKET_IN = 0;
    public static final int BUCKET_OUT = 1;

    private InventoryTile inventory;
    public FluidTank tank;
    private int cooldown;

    public TileLiquidsBank() {
        inventory = new InventoryTile(2);
        tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 20);
        energy = new EnergyStorage(FluidContainerRegistry.BUCKET_VOLUME * 20);
        cooldown = 0;
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;

        if (cooldown >= 0)
            cooldown++;

        if (cooldown >= 20) {
            cooldown = 0;

            //Fill Tank from Buckets
            if (inventory.getStackInSlot(BUCKET_IN) != null &&
                    FluidContainerRegistry.isFilledContainer(inventory.getStackInSlot(BUCKET_IN)) &&
                    FluidFuelValues.INSTANCE.getFluidFuelValue(FluidContainerRegistry.getFluidForFilledItem(inventory.getStackInSlot(BUCKET_IN)).getFluid().getName()) > 0 &&
                    tank.getFluidAmount() + FluidContainerRegistry.getContainerCapacity(inventory.getStackInSlot(BUCKET_IN)) <= tank.getCapacity()) {

                if (inventory.getStackInSlot(BUCKET_OUT) == null ||
                        inventory.getStackInSlot(BUCKET_OUT).isItemEqual(FluidContainerRegistry.drainFluidContainer(inventory.getStackInSlot(BUCKET_IN))) ||
                        inventory.getStackInSlot(BUCKET_IN).stackSize < inventory.getStackInSlot(BUCKET_IN).getMaxStackSize()) {

                    if (tank.getFluid() == null ||
                            tank.getFluid().getFluid() == FluidContainerRegistry.getFluidForFilledItem(inventory.getStackInSlot(BUCKET_IN)).getFluid()) {
                        fill(null, FluidContainerRegistry.getFluidForFilledItem(inventory.getStackInSlot(BUCKET_IN)), true);

                        //return Empty Container
                        if (inventory.getStackInSlot(BUCKET_OUT) == null)
                            inventory.setStackInSlot(FluidContainerRegistry.drainFluidContainer(inventory.getStackInSlot(BUCKET_IN)), BUCKET_OUT);
                        else
                            inventory.getStackInSlot(BUCKET_OUT).stackSize++;

                        //clear input slot
                        decrStackSize(BUCKET_IN, 1);

                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    }
                }
            } else if (inventory.getStackInSlot(BUCKET_IN) != null &&
                    FluidContainerRegistry.isEmptyContainer(inventory.getStackInSlot(BUCKET_IN)) &&
                    tank.getFluid() != null &&
                    tank.getFluidAmount() > FluidContainerRegistry.getContainerCapacity(inventory.getStackInSlot(BUCKET_IN))) {

                if (inventory.getStackInSlot(BUCKET_OUT) != null) return;

                //return full container
                inventory.setStackInSlot(FluidContainerRegistry.fillFluidContainer(tank.getFluid(), inventory.getStackInSlot(BUCKET_IN)), BUCKET_OUT);

                drain(null, FluidContainerRegistry.BUCKET_VOLUME, true);

                //clear input slot
                decrStackSize(BUCKET_IN, 1);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

            }
        }
    }

    /*
     * Fuel Provider Functions
     */

    @Override
    public double fuelProvided() {
        if (tank.getFluid() != null && tank.getFluidAmount() > 0) {
            FluidStack fluid = tank.getFluid();
            int value = FluidFuelValues.INSTANCE.getFluidFuelValue(fluid.getFluid().getName());
            FluidStack actualFluid = tank.drain(FluidContainerRegistry.BUCKET_VOLUME, false);

            return (actualFluid.amount / FluidContainerRegistry.BUCKET_VOLUME) * value;
        }
        return 0;
    }

    @Override
    public double consume() {
        if (tank.getFluid() != null && tank.getFluidAmount() > 0) {
            FluidStack fluid = tank.getFluid();
            int value = FluidFuelValues.INSTANCE.getFluidFuelValue(fluid.getFluid().getName());
            FluidStack actualFluid = tank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);
            energy.extractEnergy(actualFluid.amount, false);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

            return (actualFluid.amount / FluidContainerRegistry.BUCKET_VOLUME) * value;
        }
        return 0;
    }

    @Override
    public boolean canProvide() {
        return tank.getFluid() != null && tank.getFluidAmount() > 0;
    }

    /*
     * Tile Entity Functions
     */

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        inventory.readFromNBT(tags, 27);
        tank.readFromNBT(tags);
        cooldown = tags.getInteger("cooldown");
    }

    @Override
    public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        inventory.writeToNBT(tags);
        tank.writeToNBT(tags);
        tags.setInteger("cooldown", cooldown);
    }

    /*
     * Gui Functions
     */
    @Override
    public Object getServerGuiElement(int i, EntityPlayer entityPlayer, World world, int i1, int i2, int i3) {
        return getCore() != null ? new ContainerLiquidsBank(entityPlayer.inventory, this) : null;
    }

    @Override
    public Object getClientGuiElement(int i, EntityPlayer entityPlayer, World world, int i1, int i2, int i3) {
        return getCore() != null ? new GuiLiquidsBank(entityPlayer.inventory, this) : null;
    }

    /*
     * Inventory Functions
     */
    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack itemStack = inventory.getStackInSlot(slot);
        if (itemStack != null) {
            if (itemStack.stackSize <= count) {
                setInventorySlotContents(slot, null);
            }
            itemStack = itemStack.splitStack(count);
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
            setInventorySlotContents(slot, null);
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory.setStackInSlot(stack, slot);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return false;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        int[] sides = new int[inventory.getSizeInventory()];
        for (int x = 0; x < inventory.getSizeInventory(); x++) {
            sides[x] = x;
        }
        return sides;
    }

    /*
     * Fluid Tank Functions
     */

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (getCore() != null) {
            if (FluidFuelValues.INSTANCE.getFluidFuelValue(resource.getFluid().getName()) > 0) {
                int amount = tank.fill(resource, doFill);
                energy.receiveEnergy(amount, false);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                return amount;
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
            return null;
        }
        FluidStack fluid = tank.drain(resource.amount, doDrain);
        energy.extractEnergy(fluid.amount, false);
        return fluid;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        FluidStack fluid = tank.drain(maxDrain, doDrain);
        energy.extractEnergy(fluid.amount, false);
        return fluid;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        int value = FluidFuelValues.INSTANCE.getFluidFuelValue(fluid.getName());
        return getCore() != null && value > 0;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{tank.getInfo()};
    }

    /*
     * Waila
     */
    @Override
    public void returnWailaHead(List<String> list) {
        list.add(tank.getFluid() != null ? GuiHelper.GuiColor.YELLOW + tank.getFluid().getLocalizedName() : "Empty");
        list.add((tank.getFluid() != null ? GuiHelper.GuiColor.WHITE + Integer.toString(tank.getFluidAmount()) : "0")
                + "/" + tank.getCapacity() + GuiHelper.GuiColor.WHITE + " mB");
    }
}