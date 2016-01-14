package com.super_deathagon.itemspecial.util;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFlameFX.Factory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.super_deathagon.itemspecial.network.EnumItemAbility;
import com.super_deathagon.itemspecial.network.server.ServerItemAbilityMessage;
import com.super_deathagon.itemspecial.proxy.CommonProxy;

public class BlackMagic {
	
	public static void print(){
		System.out.println("what the fuck is happening?!");
	}
	/**
	 * This method traces through the players look vector to a maximum distance and returns 
	 * the block or entity the player was looking at. If the trace does not collide with anything but air or liquid
	 * the last block is returned.
	 * 
	 * @param player The player for whom we are extending their look vector.
	 * @param reach The the maximum distance to trace. 
	 * @return The MovingObjectPosition representing the block or entity.
	 */
	public static MovingObjectPosition getMouseOverBlock(EntityPlayer player, double reach){
        float f = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch);
        float f1 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw);
        double d0 = player.prevPosX + (player.posX - player.prevPosX);
        double d1 = player.prevPosY + (player.posY - player.prevPosY) + (double)player.getEyeHeight();
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ);
        Vec3 vec3 = new Vec3(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vec31 = vec3.addVector((double)f6 * reach, (double)f5 * reach, (double)f7 * reach);
        
        //if no collidable blocks are traced this will return null, and not the last block
        //ex. looking through grass into the sky will return null
        MovingObjectPosition p = player.worldObj.rayTraceBlocks(vec3, vec31, false, true, true);
        //if p is null we return the first block hit that is collideable
		return (p != null)? p:player.worldObj.rayTraceBlocks(vec3, vec31, false, false, true);
	}
	
	/**
	 * Code from {@link net.minecraft.client.renderer.EntityRenderer#getMouseOver()} was modified
	 * to return the block or entity the player is looking at for any given distance.
	 * @see net.minecraft.client.renderer.EntityRenderer#getMouseOver(float)
	 * @param player The player for whom we are seeing through.
	 * @param reach The maximum distance to trace.
	 * @return the MovingObjectPosition representing the block or entity.
	 */
    public static MovingObjectPosition getMouseOverAll(EntityPlayer player, double reach){
        if (player != null){
            if (player.worldObj != null){
                Entity pointedEntity = null;
                MovingObjectPosition mouseOver = getMouseOverBlock(player, reach);
                //d1 is the distance from the mouseover to the player
                //but is initialized as the maximum distance to trace over
                double d1 = reach;
                Vec3 vec3 = player.getPositionVector();
                Vec3 playerPosVec = new Vec3(vec3.xCoord, vec3.yCoord + player.eyeHeight, vec3.zCoord);
                Vec3 playerLookVec = player.getLook(1.0f);
                Vec3 vec33 = null;

                if (mouseOver != null){
                    d1 = mouseOver.hitVec.distanceTo(playerPosVec);
                }

                Vec3 mouseOverPosVec = playerPosVec.addVector(playerLookVec.xCoord * d1, 
                									playerLookVec.yCoord * d1, 
                									playerLookVec.zCoord * d1);
                float f1 = 1.0F;
                List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, 
							player.getEntityBoundingBox().addCoord(playerLookVec.xCoord * d1, 
																   playerLookVec.yCoord * d1, 
																   playerLookVec.zCoord * d1).expand((double)f1, 
									 																 (double)f1, 
									 															 	 (double)f1));
                double d2 = d1;
                for (int i = 0; i < list.size(); ++i){
                    Entity entity1 = (Entity)list.get(i);
                    
                    if (entity1.canBeCollidedWith()){
                    	
                    	//i guess the collision box is different than the bounding box
                        float f2 = entity1.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f2, 
                        																	(double)f2, 
                        																	(double)f2);
                        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(playerPosVec, mouseOverPosVec);

                        if (axisalignedbb.isVecInside(playerPosVec)){
                            if (0.0D < d2 || d2 == 0.0D){
                                pointedEntity = entity1;
                                vec33 = movingobjectposition == null ? playerPosVec : movingobjectposition.hitVec;
                                d2 = 0.0D;
                            }
                        }
                        else if (movingobjectposition != null){
                            double d3 = playerPosVec.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D){
                                if (entity1 == player.ridingEntity && !player.canRiderInteract()){
                                    if (d2 == 0.0D){
                                        pointedEntity = entity1;
                                        vec33 = movingobjectposition.hitVec;
                                    }
                                }else{
                                    pointedEntity = entity1;
                                    vec33 = movingobjectposition.hitVec;
                                    d2 = d3;
                                }
                            }
                        }
                    }
                }

                if (pointedEntity != null && (d2 < d1 || mouseOver == null)){
                    mouseOver = new MovingObjectPosition(pointedEntity, vec33);
                }
                return mouseOver;
            }
        }
		return null;
    }
    
    /**
     * A helper method to return only a living entity or null.
     * @see com.adam.supermod.seras.BlackMagic#getMouseOverAll(EntityPlayer, double)
	 * @param player The player for whom we are seeing through.
	 * @param reach The maximum distance to trace.
	 * @return The EntityLivingBase representing the living entity.
     */
    public static EntityLivingBase getMouseOverEntityLiving(EntityPlayer player, double reach){
    	MovingObjectPosition p = getMouseOverAll(player, reach);
    	if(p != null && p.entityHit != null && p.entityHit instanceof EntityLivingBase){
    		return (EntityLivingBase) p.entityHit;
    	}else
    		return null;
    }
	
	@SideOnly(Side.CLIENT)
	public static void spawnFireboltParticles(World world, double x, double y, double z, int mag){
		EffectRenderer rend = Minecraft.getMinecraft().effectRenderer;
		Factory flameFXF = new EntityFlameFX.Factory();
		EntityFX flameFX = null;
		float loc = 16.0f;
		float mov = 100.0f;
		float redMinus = 0.8f;
		float greenMinus = 1.8f;
		float blue = 0.65359f;
		float maxMag = 5.0f;
		for(int i = 0; i < mag; i++){
			flameFX = flameFXF.getEntityFX(1, world,
											  world.rand.nextGaussian()*mag/loc + x, 
											  world.rand.nextGaussian()*mag/loc + y,
											  world.rand.nextGaussian()*mag/loc + z, 
											  world.rand.nextGaussian()*mag/mov,  
											  world.rand.nextGaussian()*mag/mov, 
											  world.rand.nextGaussian()*mag/mov);	
			
			flameFX.setRBGColorF(1.0f - redMinus*mag/maxMag,
								 1.0f - greenMinus*mag/maxMag,
								        blue*mag/maxMag);
			rend.addEffect(flameFX);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void spawnFireboltParticles(Vec3 itemLocVec, Vec3 itemLookVec, byte magnitude) {
		World world = Minecraft.getMinecraft().theWorld;
    	int mag = (int) Math.ceil(5.0*magnitude/Byte.MAX_VALUE);
    	
		for(int i = 0; i < magnitude; i++){
			itemLocVec = itemLocVec.add(itemLookVec);
			spawnFireboltParticles(world, itemLocVec.xCoord, itemLocVec.yCoord, itemLocVec.zCoord, mag);
		}		
	}
	
	
    /**
     * Spawns a firebolt (but not really). This method casts a complicated raytrace through all blocks
     * pierced by the players item's look vector. The item's look vector is extended by the given magnitude.
     * The item's look vector is used as opposed to the players look vector because the item is what casts the
     * ability, not the player. The item's look vector is simply the players look vector, translated to the right,
     * and rotated so it intersects the block or entity the players mouse is hovering over.
     * @param world This should only ever be the server world object, never the player!
     * @param player The player whos item casted the firebolt.
     * @param magnitude The weight used to calculate the distance and damage. Maximum of 127(signed byte).
     */
	public static void firebolt(EntityPlayer player, byte magnitude){
		
		World world = player.worldObj;
        MovingObjectPosition mop = null;
        double right = 0.5;
		Vec3 mouseOverVec = getMouseOverAll(player, magnitude).hitVec;
		float itemLookAngle = (float) Math.tan(right/player.getPositionVector().distanceTo(mouseOverVec));

        //This is the un-obfuscated code from minecraft used to 
        //calculate the x y and z components of a vector
        float f = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch);
        float f1 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI + itemLookAngle);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI + itemLookAngle);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        
        //translate vector up to the players eyes, and to the right
        double d0 = player.prevPosX + (player.posX - player.prevPosX) + f2 * right;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) + (double)player.getEyeHeight();
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) - f3 * right;
        
        //item location vector. not to be confused with item look vector!!!
        Vec3 itemLocVec = new Vec3(d0, d1, d2);
        //this is the vector we use to iterate over the distance
        //its initialized at the items location vector, as this is where we start our raytrace.
        Vec3 iterVec = itemLocVec;
        //heres the items look vector (sounds stupid, I know. you can come up with a better name in your mod.)
		Vec3 itemLookVec = new Vec3((double) f6, (double) f5, (double) f7);
		//the vector that points to the last block we will iterate over
		//this is the vector we want to trace through(to?)
        Vec3 piercingVec = itemLocVec.addVector((double)f6 * magnitude, (double)f5 * magnitude, (double)f7 * magnitude);
		BlockPos block = new BlockPos(itemLocVec);

		//the weight used to calculate damage.
    	int mag = (int) Math.ceil(5.0*magnitude/Byte.MAX_VALUE);
    	int burnTime = 3;

		
        List<Entity> entitiesInArea = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, 
						player.getEntityBoundingBox().addCoord(itemLookVec.xCoord * magnitude, 
																itemLookVec.yCoord * magnitude, 
																itemLookVec.zCoord * magnitude).expand(1,1,1));
        
        //Lets be nice and notify everyone in the area that they should spawn particles.
        //This isn't an invisible heat beam.(but it totally is)
        
        ServerItemAbilityMessage msg = new ServerItemAbilityMessage(EnumItemAbility.FIREBOLT, magnitude, itemLocVec, itemLookVec);
        CommonProxy.network.sendToAllAround(msg, new TargetPoint(player.dimension, 
														player.posX, player.posY, player.posZ, magnitude));
		for(int i = 0; i < magnitude; i++){
			iterVec = iterVec.add(itemLookVec);
			block = new BlockPos(iterVec);
			mop = world.getBlockState(block).getBlock().collisionRayTrace(world, block, itemLocVec, piercingVec);
			
			if(mop != null){
				igniteBlock(world, mop);

				for(int j = 0; j < entitiesInArea.size(); j++){
					Entity entity1 = entitiesInArea.get(j);
				     if (entity1.canBeCollidedWith()){
				         float cbs = entity1.getCollisionBorderSize();
				         AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(cbs, cbs, cbs);
				         MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(itemLocVec, piercingVec);
				         if (movingobjectposition != null){
				     		entity1.setFire(burnTime * mag);
				    		entity1.attackEntityFrom(DamageSource.causePlayerDamage(player), magnitude/mag);
				    		entitiesInArea.remove(j);
				         }
				     }
				}
			}
		}
        world.playSoundAtEntity(player, "mob.ghast.fireball", magnitude/75.0f, 10f / magnitude + 0.5f);
 	}
	

	private static void igniteBlock(World world, MovingObjectPosition mop){
    	IBlockState fire = Blocks.fire.getDefaultState();
		BlockPos pos;
		EnumFacing face;
		if(mop.typeOfHit == MovingObjectType.BLOCK){
	    	pos = mop.getBlockPos();
	    	Block block = world.getBlockState(pos).getBlock();
			face = mop.sideHit;
			switch(face){
			case   UP: 	pos = pos.up();
				break;
			case DOWN:	pos = pos.down();
				break;
			case EAST: 	pos = pos.east();
				break;
			case WEST: 	pos = pos.west();
				break;
			case NORTH: pos = pos.north();
				break;
			case SOUTH: pos = pos.south();
				break;
			default: System.out.println("Unknown EnumFacing.");
				break;
			}
			if(world.isAirBlock(pos) && Blocks.fire.canCatchFire(world, mop.getBlockPos(), face)){
				world.setBlockState(pos, fire);
			}
		}
	}

}
