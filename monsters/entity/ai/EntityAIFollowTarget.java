package com.super_deathagon.monsters.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAIFollowTarget extends EntityAINearestAttackableTarget{
	private double tolerance = 5.0;
	private double buffer;
	private double minDistance;
	private double maxDistance;
	
	public EntityAIFollowTarget(EntityCreature creature, Class p_i45878_2_, boolean p_i45878_3_) {
		super(creature, p_i45878_2_, p_i45878_3_);
		maxDistance = creature.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
		buffer = maxDistance - tolerance;
		minDistance = buffer - tolerance;
	}

	public void updateTask(){
		super.updateTask();
		double distance = taskOwner.getDistanceToEntity(targetEntity);
		//System.out.println(distance);
		if((distance > maxDistance || distance < minDistance) && taskOwner.getNavigator().noPath())
			moveToFrontOfTarget();
	}
	
	//it appears there is a natural tendency for an entity to stay within its follow range of its target.
	private boolean moveToFrontOfTarget(){
    	double yaw = targetEntity.rotationYawHead + taskOwner.getRNG().nextGaussian()*20.0;
    	double x = targetEntity.posX - Math.sin( (yaw*Math.PI)/180.0 )*buffer;
    	double z = targetEntity.posZ + Math.cos( (yaw*Math.PI)/180.0 )*buffer;
		return taskOwner.getNavigator().tryMoveToXYZ(x, taskOwner.posY, z, 1.0f);
	}
	
	private boolean canTargetSeeMe(){
		return false;
	}
}
