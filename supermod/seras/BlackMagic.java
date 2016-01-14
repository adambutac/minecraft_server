package com.super_deathagon.supermod.seras;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.Vec3;

import com.super_deathagon.monsters.entity.EntityHiveSpider2;
import com.super_deathagon.util.MouseOverHelper;


//not always facing an entity after teleporting to it
/**
 * 
 * @author Super
 *
 * <br>This is a static "helper" class to hold some functions.
 */
public class BlackMagic{	
	

    /***************************************************/
    /**Methods that may be used Client or Server side.**/
    /***************************************************/	
    
    public static void fireArrow(EntityPlayer player){
    	if(Seras.isMaster(player))
    		fireArrow(player, Seras.teleportDistance);
    }
    
	public static void fireArrow(EntityPlayer player, double reach){
		if(player.worldObj.isRemote)
			return;
		
		float speed = 3f;
		float inaccuracy = 16f;
		Entity e = MouseOverHelper.getMouseOverEntityLiving(player, reach);
		if(e != null){
			double randmult = player.getDistanceToEntity(e);
			double randx = player.worldObj.rand.nextDouble() * randmult - randmult/2.0;
			double randy = player.worldObj.rand.nextDouble() * randmult - randmult/2.0;
			double randz = player.worldObj.rand.nextDouble() * randmult - randmult/2.0;
			EntityArrow arrow = new EntityArrow(player.worldObj, 
												e.posX + randx, 
												e.posY  + randy, 
												e.posZ + randz);
			arrow.setThrowableHeading(-randx, -randy, -randz, speed, 0.0f);
			arrow.canBePickedUp = 0;
			arrow.setFire(5);
	        player.worldObj.playSoundAtEntity(player, "random.bow", 2.0F, (new Random()).nextFloat() * 0.4f + 1.2F);
			player.worldObj.spawnEntityInWorld(arrow);
		}
	}
	
	public static void makeufat(EntityPlayer player){
		Vec3 mouseOver = MouseOverHelper.getMouseOverBlock(player, Seras.teleportDistance).hitVec;
		//EntitySuperSpider spider = new EntitySuperSpider(player.worldObj);
		EntityHiveSpider2 spider = new EntityHiveSpider2(player.worldObj);
		spider.setLocationAndAngles(mouseOver.xCoord, mouseOver.yCoord, mouseOver.zCoord, -player.rotationYaw, -player.rotationPitch);
		player.worldObj.spawnEntityInWorld(spider);
	}
}
