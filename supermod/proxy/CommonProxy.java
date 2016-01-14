package com.super_deathagon.supermod.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.super_deathagon.supermod.SuperModEventHandler;
import com.super_deathagon.supermod.SuperModFMLEventHandler;
import com.super_deathagon.supermod.network.AbilityMessage;
import com.super_deathagon.supermod.network.client.ClientMessageHandler;
import com.super_deathagon.supermod.network.server.ServerMessageHandler;
import com.super_deathagon.supermod.seras.Seras;



public class CommonProxy{
	public static SimpleNetworkWrapper simpleNetworkWrapper;
	
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event){
		//registerSimpleNetworkWrapper();
		// You MUST register the messages in Common, not in ClientOnly.
		final byte ABILITY_MESSAGE_ID = 64; // a unique ID for this message type. It helps detect errors if you don't use zero!
		simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("SupermodChannel");
		simpleNetworkWrapper.registerMessage(	ServerMessageHandler.class, 
												AbilityMessage.class,
												ABILITY_MESSAGE_ID, Side.SERVER);
		if(event.getSide() != Side.SERVER)
		simpleNetworkWrapper.registerMessage(ClientMessageHandler.class, 
											 AbilityMessage.class,
											 ABILITY_MESSAGE_ID, Side.CLIENT);
	}
	
	public void fmlLifeCycleEvent(FMLInitializationEvent event){
		//registerEventListeners();
		MinecraftForge.EVENT_BUS.register(new SuperModEventHandler());
		FMLCommonHandler.instance().bus().register(new SuperModFMLEventHandler());
	}
	
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event){}
	
	public void fmlLifeCycleEvent(FMLServerStartedEvent event){}
	
	//all gui code is handled on the client side
	@SideOnly(Side.CLIENT)
	public void renderGameOverlayEventPre(RenderGameOverlayEvent.Pre event){}
	
	//this must be handled on the client side
	//server side code cant see the clients keyboard
	@SideOnly(Side.CLIENT)
	public void keyInputEvent(KeyInputEvent event){}
	
	public void attackEntityEvent(AttackEntityEvent event){
		if(Seras.isMaster(event.entityPlayer))
			Seras.attackEntityEvent(event);
	}

	public void livingHealEvent(LivingHealEvent event) {
		if(event.entityLiving instanceof EntityPlayer
		&& Seras.isMaster((EntityPlayer)event.entityLiving)){
			Seras.livingHealEvent(event);
		}
	}
	
	public void playerTickEvent(EntityPlayer player){
		if(Seras.isMaster(player))
			Seras.playerTickEvent(player);
	}
}