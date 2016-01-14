package com.super_deathagon.supermod;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class SuperModEventHandler{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingHealEvent event){
		SuperMod.proxy.livingHealEvent(event);
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(NameFormat event){	
		for(String name: SuperMod.ModUserList)
			if(event.username.equals(name))
				event.displayname = "Superultramegadeathagon";
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(AttackEntityEvent event){
		SuperMod.proxy.attackEntityEvent(event);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(RenderGameOverlayEvent.Pre event){
		SuperMod.proxy.renderGameOverlayEventPre(event);
	}
}