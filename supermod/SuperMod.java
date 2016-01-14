package com.super_deathagon.supermod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.super_deathagon.supermod.proxy.CommonProxy;

//@Mod(modid 	 = 	SuperMod.MODID,
//	 name 	 = 	SuperMod.MODNAME,
//	 version = 	SuperMod.MODVERSION,
//	 acceptableRemoteVersions = "*")
public class SuperMod {
	public static final String MODID = "supermod";
	public static final String MODNAME = "Super's Mod";
	public static final String MODVERSION = "0.0.4";
	//public static final String MODDESCRIPTION = "It's totally super!";
	//public static final String MODAUTHOR = "Super_Deathagon";
	//public static final String MODCREDITS = "Dedicated to the police girl.";
    public static String[] ModUserList = new String[]{"Super_Deathagon"};


	@SidedProxy(clientSide="com.super_deathagon.supermod.proxy.client.ClientProxy", 
				serverSide="com.super_deathagon.supermod.proxy.server.ServerProxy")
	public static CommonProxy proxy;
	@Instance(MODID)
	public static SuperMod instance;

	@EventHandler
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event){
		//event.getModMetadata().credits = MODCREDITS;
		//event.getModMetadata().authorList.add(EnumChatFormatting.RED+MODAUTHOR);
		//event.getModMetadata().description = EnumChatFormatting.YELLOW+MODDESCRIPTION;
		proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	public void fmlLifeCycleEvent(FMLInitializationEvent event){
		proxy.fmlLifeCycleEvent(event);
	}
	
	@EventHandler
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event){
		proxy.fmlLifeCycleEvent(event);
	}
}