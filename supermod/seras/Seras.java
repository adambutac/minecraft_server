package com.super_deathagon.supermod.seras;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;

import com.super_deathagon.abilities.Teleportation;
import com.super_deathagon.supermod.SuperMod;
import com.super_deathagon.supermod.network.AbilityMessage;
import com.super_deathagon.supermod.proxy.CommonProxy;
import com.super_deathagon.util.EntityAttributeModifier;


//Shift+tele to go through walls, floors, ceilings
public class Seras {
	private static AttributeModifier sprintingSpeedBoostModifier = new AttributeModifier("generic.movementSpeed", 3.0, 2);
	private static KeyBinding[] abilityKeys;
	private static boolean healing = false;
	public static double teleportDistance = 500;
	private static SerasGui gui;
	
	public static boolean isMaster(EntityPlayer player){
		for(String name: SuperMod.ModUserList){
			if(player != null && player.getName().equals(name))
				return true;
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient(){
		gui = new SerasGui(Minecraft.getMinecraft());
		initKeyBindings();
	}
	
	@SideOnly(Side.CLIENT)
	private static void initKeyBindings(){
		abilityKeys = new KeyBinding[3];
		abilityKeys[0] = new KeyBinding("key.R.desc", Keyboard.KEY_R, "key.ability.catagory");
		abilityKeys[1] = new KeyBinding("key.F.desc", Keyboard.KEY_F, "key.ability.catagory");
		abilityKeys[2] = new KeyBinding("key.C.desc", Keyboard.KEY_C, "key.ability.catagory");
		for(KeyBinding kb : abilityKeys){
			ClientRegistry.registerKeyBinding(kb);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void renderGameOverlayEventPre(RenderGameOverlayEvent.Pre event){
		//We don't need to see the vanilla health bar 
		//we will render our own health bar
        if (event.type.equals(RenderGameOverlayEvent.ElementType.HEALTH)){
        	event.setCanceled(true);
        	gui.renderHealthBar(event);
        }
        //We don't need to see the food bar either
        if(event.type.equals(RenderGameOverlayEvent.ElementType.FOOD))
        	event.setCanceled(true);
        
        if(event.type.equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS)){
        	gui.renderCrosshairs(event);
        }
	}
	
	@SideOnly(Side.CLIENT)
	public static void keyInputEvent(){
		if(abilityKeys[0].isKeyDown()){
			(new Thread(){
				@Override
				public void run(){
					while(abilityKeys[0].isKeyDown()){
						AbilityMessage amts = new AbilityMessage(Minecraft.getMinecraft().thePlayer.getUniqueID(), 
																	AbilityMessage.Ability.ARROW);
						CommonProxy.simpleNetworkWrapper.sendToServer(amts);
						try{
							Thread.sleep(20);
						}catch(InterruptedException e){
							
						}
					}
				}
			}).start();
		}
		
		if(abilityKeys[1].isKeyDown()){
			System.out.println("Client: sending teleport message to server.");
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
				Teleportation.teleportThroughBlock(Minecraft.getMinecraft().thePlayer, teleportDistance);	
			}else{	
				Teleportation.teleportToLook(Minecraft.getMinecraft().thePlayer, teleportDistance);
			}
		}
				
		if(abilityKeys[2].isKeyDown()){
			AbilityMessage amts = new AbilityMessage(Minecraft.getMinecraft().thePlayer.getUniqueID(), 
														AbilityMessage.Ability.FAMILIARS);
			CommonProxy.simpleNetworkWrapper.sendToServer(amts);
		}
	}
	
	public static void playerTickEvent(EntityPlayer player){
		if(player.getFoodStats().getFoodLevel() != 19)
			player.getFoodStats().setFoodLevel(19);
		if(player.isSprinting())
			EntityAttributeModifier.modifyMovementSpeed(player, sprintingSpeedBoostModifier);
	}
	
	public static void livingHealEvent(LivingHealEvent event) {
		if(healing){
			healing = false;
			System.out.println("Healed " + event.amount + " hp.");
		}
		else{
			event.setCanceled(true);
		}		
	}

	public static void attackEntityEvent(AttackEntityEvent event) {
		if(event.entityPlayer.getHeldItem() == null && event.target instanceof EntityLiving){
			healing = true;
			float healAmount = ((EntityLiving)event.target).getHealth();
			DamageSource dSource = DamageSource.causePlayerDamage(event.entityPlayer);
			((EntityLiving)event.target).attackEntityFrom(dSource, healAmount);
			
			if(event.entityPlayer.getHealth() == event.entityPlayer.getMaxHealth()){
				EntityAttributeModifier.modifyMaxHealth(event.entityPlayer, healAmount);
			}
			event.entityPlayer.heal(healAmount);
		}		
	}
}
