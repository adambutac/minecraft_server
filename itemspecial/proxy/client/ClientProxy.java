package com.super_deathagon.itemspecial.proxy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import com.super_deathagon.itemspecial.SpecialItems;
import com.super_deathagon.itemspecial.proxy.CommonProxy;


public class ClientProxy extends CommonProxy{

	
	@Override
	public void fmlLifeCycleEvent(FMLInitializationEvent event){
		super.fmlLifeCycleEvent(event);
		 // required in order for the renderer to know how to render your item.  Likely to change in the near future.
	    ModelResourceLocation spearResource = new ModelResourceLocation(SpecialItems.MODID + ":itemspecialspear", "inventory");
	    ModelResourceLocation bastardSwordResource = new ModelResourceLocation(SpecialItems.MODID + ":itemspecialgreatsword", "inventory");

	    final int DEFAULT_ITEM_SUBTYPE = 0;
	    Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(CommonProxy.spear, DEFAULT_ITEM_SUBTYPE, spearResource);
	    Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(CommonProxy.greatSword, DEFAULT_ITEM_SUBTYPE, bastardSwordResource);
	}
}
