package com.teamcos.modularsystems.collections;

import com.teamcos.modularsystems.core.lib.Reference;
import com.teamcos.modularsystems.core.managers.BlockManager;
import com.teamcos.modularsystems.functions.BlockCountFunction;
import com.teamcos.modularsystems.helpers.LocalBlockCollections;
import com.teamcos.modularsystems.helpers.Locatable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.Map;

public class StandardValues {

    private boolean validSpeed = false;
    private boolean validEfficiency = false;
    private boolean validSmelting = false;
    private boolean validCrafter = false;
    private double speedMultiplier = 8;
    private double efficiencyMultiplier = 1;
    private int smeltingMultiplier = 1;
    private boolean hasCrafter = false;
    private BlockCountFunction blockCount;
    private ItemStack input;
    private ItemStack fuel;
    private ItemStack output;
    private Locatable entity;

    public StandardValues(Locatable entity, BlockCountFunction blockCount) {
        this.blockCount = blockCount;
        this.entity = entity;
    }

    public void setSpeedMultiplier(double smeltingMultiplier) {
        this.validSpeed = true;
        this.speedMultiplier = smeltingMultiplier;
    }

    public void setEfficiencyMultiplier(double efficiencyMultiplier) {
        this.validEfficiency = true;
        this.efficiencyMultiplier = efficiencyMultiplier;
    }

    public void setCrafter(boolean hasCrafter) {
        this.validCrafter = true;
        this.hasCrafter = hasCrafter;
    }

    public void unsetSpeedMultiplier() {
        this.validSpeed = false;
        this.speedMultiplier = 8.0;
    }

    public void unsetEfficiencyMultiplier() {
        this.validEfficiency = false;
        this.efficiencyMultiplier = 1.0;
    }

    public void unsetSmeltingMultiplier() {
        this.validSmelting = false;
        this.smeltingMultiplier = 1;
    }

    public void unsetCrafter() {
        this.validCrafter = false;
        this.hasCrafter = false;
    }

    public double getSpeed() {
        if (!validSpeed) {
            setValues();
        }
        return this.speedMultiplier;
    }

    public double getEfficiency() {
        if (!validEfficiency) {
            setValues();
        }
        return this.efficiencyMultiplier;
    }

    public int getSmeltingMultiplier() {
        if (!validSmelting) {
            setValues();
        }
        return smeltingMultiplier;
    }

    public void setSmeltingMultiplier(int smeltingMultiplier) {
        this.validSmelting = true;
        this.smeltingMultiplier = smeltingMultiplier;
    }

    public boolean hasCrafterUpgrade() {
        if (!validCrafter) {
            setValues();
        }
        return hasCrafter;
    }

    public void setValues() {
        Values ses = getValues(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ());
        setSpeedMultiplier(ses.getSpeedMultiplier());
        setEfficiencyMultiplier(ses.getEfficiencyMultiplier());
        setSmeltingMultiplier(ses.getSmeltingMultiplier());
        setCrafter(ses.isHasCrafter());
    }

    public void unsetValues() {
        unsetSpeedMultiplier();
        unsetEfficiencyMultiplier();
        unsetSmeltingMultiplier();
        unsetCrafter();
    }

    private Values getValues(World worldObj, int x, int y, int z) {

        BlockCountFunction blockCount = this.blockCount.copy();
        LocalBlockCollections.searchCuboidMultiBlock(worldObj, x, y, z, blockCount, Reference.MAX_FURNACE_SIZE);

        double speedMultiplier = 0.0;
        double efficiencyMultiplier = 0.0;
        int smeltingMultiplier = 1;
        boolean hasCrafter = false;

        for (Map.Entry<Block, Integer> blockEntry : blockCount.getBlockCounts().entrySet()) {
            speedMultiplier += Reference.getSpeedMultiplierForBlock(blockEntry.getKey(), blockEntry.getValue());
            efficiencyMultiplier += Reference.getEfficiencyMultiplierForBlock(blockEntry.getKey(), blockEntry.getValue());
            if (blockEntry.getKey().getUnlocalizedName().equals(BlockManager.furnaceAddition.getUnlocalizedName())) {
                smeltingMultiplier += blockEntry.getValue();
            }
            hasCrafter |= blockEntry.getKey().getUnlocalizedName().equals(BlockManager.furnaceCraftingUpgrade);
        }

        return new Values(hasCrafter, speedMultiplier, efficiencyMultiplier, smeltingMultiplier);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot == 0) {
            setInput(stack);
        } else if (slot == 1) {
            setFuel(stack);
        } else if (slot == 2) {
            setOutput(stack);
        }
    }

    public void resetInventory() {
        input = null;
        output = null;
        fuel = null;
    }

    public ItemStack getInput() {
        return input;
    }

    public void setInput(ItemStack is) {
        input = is;
    }

    public ItemStack getFuel() {
        return fuel;
    }

    public void setFuel(ItemStack is) {
        fuel = is;
    }

    public ItemStack getOutput() {
        return output;
    }

    public void setOutput(ItemStack is) {
        output = is;
    }

    public void checkInput() {
        if (getInput() != null && getInput().stackSize <= 0) {
            setInput(null);
        }
    }

    public void checkInventorySlots() {
        checkInput();
        checkOutput();
        checkFuel();
    }

    public void checkOutput() {
        if (getOutput() != null && getOutput().stackSize <= 0) {
            setOutput(null);
        }
    }

    public void checkFuel() {
        if (getFuel() != null && getFuel().stackSize <= 0) {
            setFuel(null);
        }
    }

    public void readFromNBT(NBTTagCompound tagCompound) {
        setSpeedMultiplier(tagCompound.getDouble("Speed"));
        setEfficiencyMultiplier(tagCompound.getDouble("Efficiency"));
        setCrafter(tagCompound.getBoolean("Enabled"));
        setSmeltingMultiplier(tagCompound.getInteger("SmeltingMultiplier"));
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("Efficiency", getEfficiency());
        tagCompound.setBoolean("Enabled", hasCrafterUpgrade());
        tagCompound.setDouble("Speed", getSpeed());
        tagCompound.setInteger("SmeltingMultiplier", getSmeltingMultiplier());
    }
}