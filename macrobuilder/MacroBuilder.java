package com.super_deathagon.macrobuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.super_deathagon.macrobuilder.proxy.CommonProxy;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;


//@Mod(modid = MacroBuilder.MODID, 
//name = MacroBuilder.NAME, 
//version = MacroBuilder.VERSION)
public class MacroBuilder {
	public static final String MODID = "macrobuilder";
	public static final String NAME = "Structure building macro";
	public static final String VERSION = "0.0.1";
	public static String[] ModUserList = new String[]{"Super_Deathagon"};
	
	@SidedProxy(clientSide="com.super_deathagon.macrobuilder.proxy.client.ClientProxy", 
				serverSide="com.super_deathagon.macrobuilder.proxy.server.ServerProxy")
	public static CommonProxy proxy;
	@Instance(MODID)
	public static MacroBuilder instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		proxy.serverLoad(event);
	}
}
