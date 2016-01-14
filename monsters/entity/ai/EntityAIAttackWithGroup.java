package com.super_deathagon.monsters.entity.ai;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.pathfinding.PathEntity;

public class EntityAIAttackWithGroup extends EntityAIAttackOnCollide{

	public EntityAIAttackWithGroup(EntityCreature p_i1635_1_, Class p_i1635_2_, double p_i1635_3_, boolean p_i1635_5_) {
		super(p_i1635_1_, p_i1635_2_, p_i1635_3_, p_i1635_5_);
	}

	@Override
	public boolean shouldExecute(){
		return super.shouldExecute() && checkGroupSize();
	}
	
	protected double getTargetDistance(){
		IAttributeInstance iattributeinstance = attacker.getEntityAttribute(SharedMonsterAttributes.followRange);
		return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
	}
	
	private boolean checkGroupSize(){
		double searchArea = getTargetDistance();
		List<EntityCreature> list = attacker.worldObj.getEntitiesWithinAABB(attacker.getClass(), attacker.getEntityBoundingBox().expand(searchArea, searchArea, searchArea));
		int count = 0;
		int tolerance = 4;
		int creaturePathLength = -1;
		EntityLivingBase myTarget = attacker.getAttackTarget();
		int myPathDistance = attacker.getNavigator().getPathToEntityLiving(myTarget).getCurrentPathLength();
		PathEntity creaturePath = null;
		
		for(EntityCreature entity:list){
			EntityLivingBase creatureTarget = entity.getAttackTarget();
			if(creatureTarget != null && creatureTarget.equals(myTarget)){
				creaturePath = entity.getNavigator().getPathToEntityLiving(myTarget);
				if(creaturePath != null){
					creaturePathLength = creaturePath.getCurrentPathLength();
					if(creaturePathLength >= myPathDistance - tolerance && creaturePathLength <= myPathDistance + tolerance){
						count++;
					}
				}
			}
	  	}
	        	
	  	if(count > 3)
	  		return true;
	  	else
	  		return false;
   }
}
