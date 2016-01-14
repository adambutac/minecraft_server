package com.super_deathagon.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAttributeModifier {
	
	public static void modifyMaxHealth(EntityLivingBase e, double amount){
		AttributeModifier maxHealthMod = new AttributeModifier("generic.maxHealth", amount, 0).setSaved(false);
		e.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(maxHealthMod);
		e.setHealth(e.getMaxHealth());
	}
	
	public static void modifyBaseDamage(EntityLivingBase e, double amount){
		AttributeModifier damageMod = new AttributeModifier("generic.attackDamage", amount, 0).setSaved(false);
		e.getEntityAttribute(SharedMonsterAttributes.attackDamage).applyModifier(damageMod);
	}

	public static double getBaseDamage(EntityLivingBase e){
		return e.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
	}
	
	public static void setFollowRange(EntityLivingBase e, double amount){
		e.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(amount);;
	}
	
	public static void modifyMovementSpeed(EntityLivingBase e, AttributeModifier sprintMod){
		IAttributeInstance speedAttribute = e.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
		if(!speedAttribute.func_180374_a(sprintMod)){
			speedAttribute.applyModifier(sprintMod);
		}else if(speedAttribute.func_180374_a(sprintMod)){
			speedAttribute.removeModifier(sprintMod);
		}
	}
	
	public static void phobosBaseDamage(EntityLivingBase e, AttributeModifier mod){
		IAttributeInstance theD = e.getEntityAttribute(SharedMonsterAttributes.attackDamage);
		if(!theD.func_180374_a(mod))
			theD.applyModifier(mod);
	}
}
