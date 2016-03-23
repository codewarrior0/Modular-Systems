package com.teambr.modularsystems.core.client

import com.teambr.bookshelf.common.blocks.properties.Properties
import com.teambr.modularsystems.core.client.models.BakedFurnaceCore
import com.teambr.modularsystems.core.common.CommonProxy
import com.teambr.modularsystems.core.common.blocks.traits.CoreStates
import com.teambr.modularsystems.core.managers.{BlockManager, ItemRenderManager}
import net.minecraft.client.renderer.block.statemap.StateMap.Builder
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader

/**
 * This file was created for the Modular-Systems
 *
 * Modular-Systems if licensed under the is licensed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis <pauljoda>
 * @since August 02, 2015
 */
class ClientProxy extends CommonProxy {

    override def preInit() = {

        // Ignore rotation, we have models for that
        ModelLoader.setCustomStateMapper(BlockManager.furnaceCore,
            (new Builder).ignore(Properties.FOUR_WAY)
                    .ignore(CoreStates.PROPERTY_ACTIVE).build())

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BlockManager.furnaceCore), 0,
            BakedFurnaceCore.MODEL_RESOURCE_LOCATION_NORMAL)

        ItemRenderManager.registerBlockModel(BlockManager.crusherCore, "crusherCore", "facing=east,is_active=false")

        RenderRegistry.doTheThing()
    }

    override def init() = {

        //Register Inventory Renderer for Items
        ItemRenderManager.registerItemRenderer()
    }
}
