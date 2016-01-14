package com.super_deathagon.supermod;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class SuperModFMLEventHandler{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(KeyInputEvent event){
		SuperMod.proxy.keyInputEvent(event);
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerTickEvent event){
		SuperMod.proxy.playerTickEvent(event.player);
	}
}