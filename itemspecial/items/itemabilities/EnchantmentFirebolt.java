package com.super_deathagon.itemspecial.items.itemabilities;

import java.util.List;

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

import com.super_deathagon.abilities.IAbility;
import com.super_deathagon.itemspecial.network.EnumItemAbility;
import com.super_deathagon.itemspecial.network.server.ServerItemAbilityMessage;
import com.super_deathagon.itemspecial.proxy.CommonProxy;
import com.super_deathagon.itemspecial.util.LangString;
import com.super_deathagon.util.MouseOverHelper;

public class EnchantmentFirebolt extends EnchantmentAbility implements IAbility{
	private static final float maxLevel = 5;
	private static final float maxCharge = maxLevel * 20;
	private static final int maxPower = 500;
	private static final int maxReach = 100;
	
	public EnchantmentFirebolt(int enchID, ResourceLocation enchName, int enchWeight) {
		super(enchID, enchName, enchWeight, EnumEnchantmentType.WEAPON);
		this.name = "firebolt";
	}
	
	public int getMaxLevel(){
		return (int)maxLevel;
	}
	
	@Override
	public String getTranslatedDescription(int level){
		return LangString.enchantmentDescriptionFirebolt + level;
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
		for(int i = 0; i < mag; i++){
			flameFX = flameFXF.getEntityFX(1, world,
											  world.rand.nextGaussian()*mag/loc + x, 
											  world.rand.nextGaussian()*mag/loc + y,
											  world.rand.nextGaussian()*mag/loc + z, 
											  world.rand.nextGaussian()*mag/mov,  
											  world.rand.nextGaussian()*mag/mov, 
											  world.rand.nextGaussian()*mag/mov);	
			
			flameFX.setRBGColorF(1.0f - (redMinus*mag/maxLevel),
								 1.0f - greenMinus*mag/maxLevel,
								        blue*mag/maxLevel);
			rend.addEffect(flameFX);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void spawnFireboltParticles(Vec3 itemLocVec, Vec3 itemLookVec, byte magnitude) {
		World world = Minecraft.getMinecraft().theWorld;
    	int mag = (int) Math.ceil(5.0*magnitude/MAX_MAG);
    	
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
     * and rotated left so it intersects the block or entity the players mouse is hovering over.
     * @param world This should only ever be the server world object, never the player!
     * @param player The player whos item casted the firebolt.
     * @param magnitude The weight used to calculate the distance and damage. Maximum of 127(signed byte).
     */
	public void useAbility(World world, EntityPlayer player, int level, int charge){
		if(level > maxLevel)
			level = (int) maxLevel;
		else if(level <= 0)
			return;
		
		if(charge > maxCharge)
			charge = (int) maxCharge;
		else if(charge <= 0)
			return;
		
		
		int power = (int) (charge/maxCharge * Math.pow(level/maxLevel, 2) * maxPower);
		int reach = (int) (charge/maxCharge * level/maxLevel * maxReach);
		System.out.println("Firebolt=" + power + ":" + reach);
        MovingObjectPosition mop = null;
        double right = 0.5;
		Vec3 mouseOverVec = MouseOverHelper.getMouseOverAll(player, charge).hitVec;
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
        Vec3 piercingVec = itemLocVec.addVector((double)f6 * reach, (double)f5 * reach, (double)f7 * reach);
		BlockPos block = new BlockPos(itemLocVec);

		//the weight used to calculate damage.
    	int burnTime = 3;

		
        List<Entity> entitiesInArea = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, 
						player.getEntityBoundingBox().addCoord(itemLookVec.xCoord * reach, 
																itemLookVec.yCoord * reach, 
																itemLookVec.zCoord * reach).expand(1,1,1));
        
        //Lets be nice and notify everyone in the area that they should spawn particles.
        //This isn't an invisible heat beam.(but it totally is)
        ServerItemAbilityMessage msg = new ServerItemAbilityMessage(EnumItemAbility.FIREBOLT, (byte)reach, itemLocVec, itemLookVec);
        CommonProxy.network.sendToAllAround(msg, new TargetPoint(player.dimension, 
														player.posX, player.posY, player.posZ, reach));
		for(int i = 0; i < reach; i++){
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
				     		entity1.setFire(burnTime * power);
				    		entity1.attackEntityFrom(DamageSource.causePlayerDamage(player), power);
				    		entitiesInArea.remove(j);
				         }
				     }
				}
			}
		}
        world.playSoundAtEntity(player, "mob.ghast.fireball", power/75.0f, 10f / power + 0.5f);
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
