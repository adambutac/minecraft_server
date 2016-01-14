package com.super_deathagon.monsters.entity.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIHive extends EntityAIBase{
	protected HashSet<EntityCreature> hive;
	protected final EntityCreature taskOwner;
	protected double searchArea;
	
	public EntityAIHive(EntityCreature creature, double area){
		hive = new HashSet<EntityCreature>();
		taskOwner = creature;
		searchArea = area;
	}
	
	@Override
	public boolean shouldExecute() {
		updateHive();
		return true;
	}
	
    protected void updateHive(){
		System.out.println(this.taskOwner + " searching for hive members...");
        List<Entity> list = taskOwner.worldObj.getEntitiesWithinAABBExcludingEntity(taskOwner, taskOwner.getEntityBoundingBox().expand(searchArea, searchArea, searchArea));
        HashSet<EntityCreature> tempHive = new HashSet<EntityCreature>();
        EntityCreature entity;
        
        for(Entity e : list){
        	if(e.getClass().equals(taskOwner.getClass())){
        		if(e != null){
            		tempHive.add((EntityCreature) e);
        		}
        	}
        }
        
        hive = tempHive;
    }
    
    public EntityLivingBase getHiveTarget(){
    	List<EntityLivingBase> targets = getTargets();
    	return getPopularTarget(targets);
    }
    
    public EntityLivingBase getPopularTarget(List<EntityLivingBase> targets){
    	EntityLivingBase best = targets.get(0);
    	int count = 0;
    	int bestCount = 0;
    	for(EntityLivingBase t:targets){
    		for(EntityLivingBase r:targets){
    			if(t == r){
    				count++;
    			}
    		}
    		if(count > bestCount){
    			best = t;
    			bestCount = count;
    		}
    	}
    	
    	return best;
    }
    
    public List<EntityLivingBase> getTargets(){
    	List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
    	HashMap<Integer, EntityLivingBase> map = new HashMap<Integer, EntityLivingBase>();
    	EntityLivingBase target;
    	double distance;
    	for(EntityCreature creature: this.hive){
    		target = creature.getAttackTarget();
    		if(creature.getAttackTarget() != null){
    			targets.add(target);
    		}
    	}
    	return targets;
    }
}
