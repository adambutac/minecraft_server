package com.super_deathagon.macrobuilder.proxy;

import com.super_deathagon.macrobuilder.CommandCopy;
import com.super_deathagon.macrobuilder.CommandPaste;

import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

	public void fmlLifeCycleEvent(FMLInitializationEvent event) {

	}

	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) {

	}

	public void fmlLifeCycleEvent(FMLPostInitializationEvent event) {

	}

	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandCopy());
		event.registerServerCommand(new CommandPaste());
	}

}
