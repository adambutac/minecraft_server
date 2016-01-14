package com.super_deathagon.supermod.proxy.server;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.super_deathagon.supermod.proxy.CommonProxy;

public class ServerProxy extends CommonProxy{	
	@Override
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event){
		super.fmlLifeCycleEvent(event);
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLInitializationEvent event){
		super.fmlLifeCycleEvent(event);
	}
	
	@Override
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event){
		super.fmlLifeCycleEvent(event);
	}
}
