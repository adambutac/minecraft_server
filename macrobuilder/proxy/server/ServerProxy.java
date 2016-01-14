package com.super_deathagon.macrobuilder.proxy.server;

import com.super_deathagon.macrobuilder.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ServerProxy extends CommonProxy{
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
	}
}
