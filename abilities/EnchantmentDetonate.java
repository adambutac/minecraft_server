package com.super_deathagon.abilities;

import java.util.List;

import com.super_deathagon.itemspecial.items.itemabilities.EnchantmentAbility;
import com.super_deathagon.itemspecial.items.itemabilities.EnchantmentFirebolt;
import com.super_deathagon.itemspecial.network.EnumItemAbility;
import com.super_deathagon.itemspecial.network.server.ServerItemAbilityMessage;
import com.super_deathagon.itemspecial.proxy.CommonProxy;
import com.super_deathagon.itemspecial.util.LangString;
import com.super_deathagon.util.MouseOverHelper;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFlameFX.Factory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnchantmentDetonate extends EnchantmentAbility implements IAbility{
	private static final float maxLevel = 5;
	private static final float maxCharge = maxLevel * 20;
	private static final float maxPower = 20;
	private static final int maxReach = 50;
	
	public EnchantmentDetonate(int enchID, ResourceLocation enchName, int enchWeight) {
		super(enchID, enchName, enchWeight, EnumEnchantmentType.WEAPON);
		this.name = "detonate";
	}
	
	public int getMaxLevel(){
		return (int)maxLevel;
	}
	
	@Override
	public String getTranslatedDescription(int level){
		return LangString.enchantmentDescriptionFirebolt + level;
	}

	public void useAbility(World world, EntityPlayer player, int level, int charge){
		if(level > maxLevel)
			level = (int) maxLevel;
		else if(level <= 0)
			return;
		
		if(charge > maxCharge)
			charge = (int) maxCharge;
		else if(charge <= 0)
			return;
		
		float power = (float) (charge/maxCharge * Math.pow(level/maxLevel, 2) * maxPower);
		float reach = charge/maxCharge * level/maxLevel * maxReach + 5;
		System.out.println("Firebolt=" + power + ":" + reach);
		Vec3 mouseOverVec = MouseOverHelper.getMouseOverAll(player, reach).hitVec;

		world.createExplosion(player, mouseOverVec.xCoord, mouseOverVec.yCoord, mouseOverVec.zCoord, power, true);	
 	}
}
