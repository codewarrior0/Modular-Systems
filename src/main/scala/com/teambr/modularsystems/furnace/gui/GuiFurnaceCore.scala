package com.teambr.modularsystems.furnace.gui

import java.awt.Color

import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentText, GuiTabCollection, GuiComponentArrow, GuiComponentFlame}
import com.teambr.modularsystems.furnace.container.ContainerFurnaceCore
import com.teambr.modularsystems.furnace.tiles.TileEntityFurnaceCore
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

import scala.collection.mutable.ArrayBuffer

/**
 * This file was created for Modular-Systems
 *
 * Modular-Systems is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 07, 2015
 */
class GuiFurnaceCore(player: EntityPlayer, tile: TileEntityFurnaceCore) extends
        GuiBase[ContainerFurnaceCore](new ContainerFurnaceCore(player.inventory, tile), 175, 165, "inventory.furnace.title") {

    protected var core = tile
    protected val coreLocation = tile.getPos

    addRightTabs(rightTabs)

    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j:Int): Unit = {
        core = core.getWorld.getTileEntity(coreLocation).asInstanceOf[TileEntityFurnaceCore]
        super[GuiBase].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addComponents(): Unit = {
        components += new GuiComponentFlame(81, 55) {
            override def getCurrentBurn: Int = if (core.isBurning) core.getBurnTimeRemainingScaled(14) else 0
        }
        components += new GuiComponentArrow(79, 34) {
            override def getCurrentProgress: Int = core.getCookProgressScaled(24)
        }
    }

    override def addRightTabs(tabs: GuiTabCollection): Unit = {
        if (core != null) {
            var furnaceInfoSpeed = new ArrayBuffer[BaseComponent]()
            furnaceInfoSpeed += new GuiComponentText("Information", 26, 6, 0xFFCC00)
            furnaceInfoSpeed += new GuiComponentText("Speed: ", 5, 23, 0xFFFFFF)
            //furnaceInfoSpeed += new GuiComponentText(String.format("%.2f", if ((-1 * (((core.getValues.getSpeed + 200) / 200) - 1)) != 0) ((-1 * (((core.getValues.getSpeed + 200) / 200) - 1)) * 100) else 0.00) + "%", 15, 33, if ((-1 * (((core.getValues.getSpeed + 200) / 200) - 1)) > 0) 0x5CE62E else if ((-1 * (((core.getValues.getSpeed + 200) / 200) - 1)) == 0) 0x000000 else 0xE62E00)
            furnaceInfoSpeed += new GuiComponentText("Efficiency: ", 5, 48, 0xFFFFFF)
           // furnaceInfoSpeed += new GuiComponentText(String.format("%.2f", if (-1 * (100 - ((1600 + core.getValues.getEfficiency) / 1600) * 100) != 0) -1 * (100 - ((1600 + core.getValues.getEfficiency) / 1600) * 100) else 0.00) + "%", 15, 58, if (-1 * (100 - ((1600 + core.getValues.getEfficiency) / 1600) * 100) > 0) 0x5CE62E else if (-1 * (100 - ((1600 + core.getValues.getEfficiency) / 1600) * 100) == 0) 0x000000 else 0xE62E00)
            furnaceInfoSpeed += new GuiComponentText("Multiplicity: ", 5, 73, 0xFFFFFF)
            //furnaceInfoSpeed += new GuiComponentText((core.getValues.getMultiplicity + 1).toInt + "x", 15, 83, if (core.getValues.getMultiplicity > 0) 0x5CE62E else 0x000000)
            tabs.addTab(furnaceInfoSpeed.toList, 95, 100, new Color(150, 112, 50), new ItemStack(Items.book))
        }
    }
}