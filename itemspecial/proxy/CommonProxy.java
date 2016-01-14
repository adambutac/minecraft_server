package com.super_deathagon.itemspecial.proxy;


import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.super_deathagon.itemspecial.items.ItemSpecialGreatSword;
import com.super_deathagon.itemspecial.items.ItemSpecialSpear;
import com.super_deathagon.itemspecial.items.itemabilities.EnchantmentAbility;
import com.super_deathagon.itemspecial.network.client.ClientItemAbilityMessage;
import com.super_deathagon.itemspecial.network.client.ClientMessageHandler;
import com.super_deathagon.itemspecial.network.server.ServerItemAbilityMessage;
import com.super_deathagon.itemspecial.network.server.ServerMessageHandler;



public class CommonProxy{
	public static SimpleNetworkWrapper network;
    public static ItemSpecialSpear spear;
    public static ItemSpecialGreatSword greatSword;
    
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event){
		EnchantmentAbility.init();
		registerNetworkWrapper(event);
		registerItems();
	}
	
	public void fmlLifeCycleEvent(FMLInitializationEvent event){}
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event){}
	public void fmlLifeCycleEvent(FMLServerStartedEvent event){}
	
	public void registerItems(){
        spear = (ItemSpecialSpear)(new ItemSpecialSpear().setUnlocalizedName("itemspecialspear"));
        greatSword = (ItemSpecialGreatSword)(new ItemSpecialGreatSword().setUnlocalizedName("itemspecialgreatsword"));
        GameRegistry.registerItem(spear, "itemspecialspear");
        GameRegistry.registerItem(greatSword, "itemspecialgreatsword");
	}
	
	public void registerNetworkWrapper(FMLPreInitializationEvent event){
		// You MUST register the messages in Common, not in ClientOnly.
		final byte SERVER_ITEM_ABILITY_MESSAGE_ID = 65; // a unique ID for this message type. It helps detect errors if you don't use zero!
		final byte CLIENT_ITEM_ABILITY_MESSAGE_ID = 66;
		network = NetworkRegistry.INSTANCE.newSimpleChannel("ItemAbilityChannel");
		network.registerMessage(ServerMessageHandler.class, 
								ClientItemAbilityMessage.class,
								SERVER_ITEM_ABILITY_MESSAGE_ID, Side.SERVER);
		network.registerMessage(ClientMessageHandler.class, 
								ServerItemAbilityMessage.class,
								CLIENT_ITEM_ABILITY_MESSAGE_ID, Side.CLIENT);
	}
}
