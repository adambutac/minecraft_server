package com.super_deathagon.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySpellParticleFX.WitchFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.super_deathagon.supermod.network.AbilityMessage;
import com.super_deathagon.supermod.proxy.CommonProxy;
import com.super_deathagon.util.MouseOverHelper;

public class Teleportation {
	/**
	 * Spawns particles to give the teleport ability some visual effects.
	 * This method should only ever be used by the Client or Integrated Server as
	 * particles should only ever be spawned Client side.
	 * 
	 * @param player The player to spawn particles around.
	 * @see net.minecraft.world.World#spawnParticle(EnumParticleTypes, double, double, double, double, double, double, int)
	 */
	@SideOnly(Side.CLIENT)
    public static void spawnTeleportParticlesRandom(EntityPlayer player){
    	EffectRenderer rend = Minecraft.getMinecraft().effectRenderer;
    	WitchFactory spellFXF = new EntitySpellParticleFX.WitchFactory();
    	EntityFX spellFX = null;
		for(int i = 0; i < 32; i++){
			spellFX = spellFXF.getEntityFX(0, player.worldObj, 
											player.posX + player.worldObj.rand.nextDouble() * 2.0 - 1.0, 
											player.posY + player.worldObj.rand.nextDouble() * 2.0, 
											player.posZ+ player.worldObj.rand.nextDouble() * 2.0 - 1.0,
											player.worldObj.rand.nextGaussian()/2.0, 
											player.worldObj.rand.nextGaussian(), 
											player.worldObj.rand.nextGaussian()/2.0);
			spellFX.setRBGColorF(0.35f, 0f, 0.65359f);
			rend.addEffect(spellFX);
		}
    }
    
	@SideOnly(Side.CLIENT)
    public static void spawnTeleportParticlesHelix(EntityPlayer player){
    	World world = player.worldObj;
    	EffectRenderer rend = Minecraft.getMinecraft().effectRenderer;
    	WitchFactory spellFXF = new EntitySpellParticleFX.WitchFactory();
    	EntityFX spellFX = null;
    	double x = 0;
    	double y = 0;
    	double z = 0;
    	float rand;
    	float red = 0.35f;
    	float green = 0f;
    	float blue = 0.65359f;
    	int numOfHelix = 5;
    	int numOfParticles = 10;
    	float twist = 8.0f;
    	float height = 3.0f;
    	for(int j = 0; j < numOfHelix; j++){
			for(int i = 0; i < numOfParticles; i++){
				y = i/height;
				x = Math.sin(i/twist + 2.0*Math.PI/numOfHelix*j);
				z = Math.cos(i/twist + 2.0*Math.PI/numOfHelix*j);
				
				spellFX = spellFXF.getEntityFX(0, player.worldObj, 
												player.posX + x, 
												player.posY + y, 
												player.posZ + z,
												0,0,0);
				spellFX.setRBGColorF(red*i/numOfParticles,
									green,
									blue*i/numOfParticles);
				rend.addEffect(spellFX);
			}  
    	}
	}
	
    /**
     * Causes a player to teleport to where they are looking, limited by a given maximum distance.
     * If they are looking at a living entity, this causes them to teleport behind that entity 
     * (working on turning the player to face the entity).
     * @param player The player to teleport.
     * @param reach The maximum distance to teleport.
     * @see com.adam.supermod.seras.BlackMagic#teleportToEntity(EntityPlayer, Entity)
     * @see com.adam.supermod.seras.BlackMagic#teleportToBlock(EntityPlayer, MovingObjectPosition)
     */
	public static void teleportToLook(EntityPlayer player, double reach){
		MovingObjectPosition p = MouseOverHelper.getMouseOverAll(player, reach);
		if(p != null){
	    	if(p.entityHit != null && p.entityHit instanceof EntityLivingBase)
	    		teleportToEntity(player, p.entityHit);
			else
				teleportToBlock(player, p);
		}
	}
	
	/**
	 * Causes a player to teleport to the other side of a contiguous mass,
	 * or not at all if there are no voids within the trace to teleport to.
	 * @param player The player to teleport.
	 * @param reach The maximum distance to teleport.
	 * @see com.adam.supermod.seras.BlackMagic#getMouseOverBlock(EntityPlayer, double)
	 * @see com.adam.supermod.seras.BlackMagic#isTeleportSafe(World, BlockPos)
	 * @see com.adam.supermod.seras.BlackMagic#teleportToBlock(EntityPlayer, MovingObjectPosition)
	 */
	public static void teleportThroughBlock(EntityPlayer player, double reach){
		Vec3 lookVec = player.getLookVec();
		Vec3 playerVec = player.getPositionVector();
		Vec3 blockVec = MouseOverHelper.getMouseOverBlock(player, reach).hitVec;
		BlockPos block = new BlockPos(blockVec);
		
		do{
			blockVec = blockVec.add(lookVec);
			block = new BlockPos(blockVec);
		}while(!isTeleportSafe(player.worldObj, block) && blockVec.distanceTo(playerVec) < reach);

		if(blockVec.distanceTo(playerVec) < reach)
			teleportToBlock(player, new MovingObjectPosition(blockVec, EnumFacing.UP, block));
	}
	
	/**
	 * 
	 * @param player
	 * @param e
	 */
	private static void teleportToEntity(EntityPlayer player, Entity e){
		double r = e.rotationYaw*Math.PI/180.0;
		double x = e.posX + Math.sin(r);
		double y = e.posY;
		double z = e.posZ - Math.cos(r);
		
		if(isTeleportSafe(player.worldObj, x, y, z)){
			//player.setRotationYawHead(e.rotationYaw);
			//AbilityMessage amts = new AbilityMessage(player.getUniqueID(), 
			//											AbilityMessage.Ability.TELEPORT,
			//											x,y,z);
			//CommonProxy.simpleNetworkWrapper.sendToServer(amts);
			instantTransmission(player, x, y ,z);
		}
	}
	
	private static void teleportToBlock(EntityPlayer player, MovingObjectPosition p){
		BlockPos look = p.getBlockPos();
		double x = look.getX() + 0.5;
		double y = look.getY();
		double z = look.getZ() + 0.5;
		EnumFacing e = p.sideHit;
		
		if(e == EnumFacing.UP){
			if(player.worldObj.getBlockState(look).getBlock().getMaterial().blocksMovement())
				y += player.worldObj.getBlockState(look).getBlock().getBlockBoundsMaxY();
		}else if(e == EnumFacing.DOWN){
			y -= 2.0;
		}else if(e == EnumFacing.NORTH){
			z--;
		}else if(e == EnumFacing.SOUTH){
			z++;
		}else if(e == EnumFacing.EAST){
			x++;
		}else if(e == EnumFacing.WEST){
			x--;
		}else{
			return;
		}
		
		if(isTeleportSafe(player.worldObj, x, y, z)){
			//AbilityMessage amts = new AbilityMessage(player.getUniqueID(), 
			//											AbilityMessage.Ability.TELEPORT,
			//											x,y,z);
			//CommonProxy.simpleNetworkWrapper.sendToServer(amts);
			instantTransmission(player, x, y, z);
		}
	}
	
	public static boolean isTeleportSafe(World world, double x, double y, double z){
		BlockPos block = new BlockPos(x, y, z);
		
		return !world.getBlockState(block).getBlock().getMaterial().blocksMovement()
			&& !world.getBlockState(block.up()).getBlock().getMaterial().blocksMovement()
			&& world.getChunkFromBlockCoords(block).isLoaded()
			&& y > 0;
	}
	
	public static boolean isTeleportSafe(World world, BlockPos bp){
		return !world.getBlockState(bp).getBlock().getMaterial().blocksMovement()
			&& !world.getBlockState(bp.up()).getBlock().getMaterial().blocksMovement()
			&& world.getChunkFromBlockCoords(bp).isLoaded()
			&& bp.getY() > 0;
	}
    
	
    /***************************************************/
    /**Methods that should only be used by the server.**/
    /***************************************************/
    public static void instantTransmission(EntityPlayer player, Vec3 posVec){
    	//int sendRadius = 100;
		//AbilityMessage amts = new AbilityMessage(player.getUniqueID(), 
		//											AbilityMessage.Ability.TELEPORT);
		
    	//player.worldObj.playSoundToNearExcept(player, "supermod:teleport", 1, 1);
		//CommonProxy.simpleNetworkWrapper.sendToAllAround(amts, new TargetPoint(player.dimension, 
		//													player.posX, player.posY, player.posZ, sendRadius));
		player.setPositionAndUpdate(posVec.xCoord, posVec.yCoord, posVec.zCoord);
		player.fallDistance = 0;
		//player.worldObj.playSoundAtEntity(player, "supermod:teleport", 1, 1);
		//CommonProxy.simpleNetworkWrapper.sendToAllAround(amts, new TargetPoint(player.dimension, 
		//													player.posX, player.posY, player.posZ, sendRadius));
    }
    
    public static void instantTransmission(EntityPlayer player,double x, double y, double z){
    	//int sendRadius = 50;
		//AbilityMessage amts = new AbilityMessage(player.getUniqueID(), 
		//											AbilityMessage.Ability.TELEPORT);
		
    	//player.worldObj.playSoundToNearExcept(player, "supermod:teleport", 1, 1);
		//CommonProxy.simpleNetworkWrapper.sendToAllAround(amts, new TargetPoint(player.dimension, 
		//													player.posX, player.posY, player.posZ, sendRadius));
		player.setPositionAndUpdate(x, y, z);
		player.fallDistance = 0;
		//player.worldObj.playSoundAtEntity(player, "supermod:teleport", 1, 1);
		//CommonProxy.simpleNetworkWrapper.sendToAllAround(amts, new TargetPoint(player.dimension, 
		//													player.posX, player.posY, player.posZ, sendRadius));
    }
}
