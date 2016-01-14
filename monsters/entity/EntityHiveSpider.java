package com.super_deathagon.monsters.entity;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import scala.reflect.internal.Trees.This;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.super_deathagon.util.EntityAttributeModifier;

public class EntityHiveSpider extends EntitySpider{
	LinkedHashSet<EntityHiveSpider> hive = new LinkedHashSet<EntityHiveSpider>();
	
    public EntityHiveSpider(World worldIn){
        super(worldIn);
		 //this.tasks.taskEntries.remove(7);
		 //this.tasks.taskEntries.remove(6);
		 this.tasks.taskEntries.remove(4);
		 this.tasks.taskEntries.remove(3);
		 this.tasks.taskEntries.remove(2);
		 this.targetTasks.taskEntries.remove(2);
		 this.targetTasks.taskEntries.remove(1);
		 this.tasks.addTask(4, new EntityHiveSpider.AIHiveSpiderAttack(EntityPlayer.class));
		 this.tasks.addTask(4, new EntityHiveSpider.AIAvoidPlayer());
		 this.targetTasks.addTask(2, new AIHiveSpiderTarget(EntityPlayer.class));
    }

    public void onUpdate(){
    	hive = collectNearbySpiders(20.0);
        super.onUpdate();
    }

    @Override
    protected void applyEntityAttributes(){
        super.applyEntityAttributes();
        EntityAttributeModifier.setFollowRange(this, 50.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35);
    }
    
    private LinkedHashSet<EntityHiveSpider> collectNearbySpiders(double area){
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(area, area, area));
        LinkedHashSet<EntityHiveSpider> tempHive = new LinkedHashSet<EntityHiveSpider>();
        EntityHiveSpider spider;
        
        for(Entity e : list){
        	if(e instanceof EntityHiveSpider){
        		spider = (EntityHiveSpider)e;
        		tempHive.add(spider);
        		if(spider.hive != null && !spider.hive.isEmpty())
        			tempHive.addAll(spider.hive);
        	}
        }
        return tempHive;
    }

    @Override
    public IEntityLivingData func_180482_a(DifficultyInstance p_180482_1_, IEntityLivingData p_180482_2_)
    {
        Object p_180482_2_1 = super.func_180482_a(p_180482_1_, p_180482_2_);

        /*if (this.worldObj.rand.nextInt(100) == 0)
        {
            EntitySkeleton entityskeleton = new EntitySkeleton(this.worldObj);
            entityskeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            entityskeleton.func_180482_a(p_180482_1_, (IEntityLivingData)null);
            this.worldObj.spawnEntityInWorld(entityskeleton);
            entityskeleton.mountEntity(this);
        }*/

        if (p_180482_2_1 == null)
        {
            p_180482_2_1 = new EntitySpider.GroupData();

            if (this.worldObj.getDifficulty() == EnumDifficulty.HARD && this.worldObj.rand.nextFloat() < 0.1F * p_180482_1_.getClampedAdditionalDifficulty())
            {
                ((EntitySpider.GroupData)p_180482_2_1).func_111104_a(this.worldObj.rand);
            }
        }

        if (p_180482_2_1 instanceof EntitySpider.GroupData)
        {
            int i = ((EntitySpider.GroupData)p_180482_2_1).field_111105_a;

            if (i > 0 && Potion.potionTypes[i] != null)
            {
                this.addPotionEffect(new PotionEffect(i, Integer.MAX_VALUE));
            }
        }

        return (IEntityLivingData)p_180482_2_1;
    }
    
    class AIAvoidPlayer extends EntityAIAvoidEntity{
		public AIAvoidPlayer() {
			super(EntityHiveSpider.this , new Predicate()
			    {
			        public boolean func_179911_a(Entity e){
			            return e instanceof EntityPlayer && ((EntityPlayer)e).getHealth() > EntityAttributeModifier.getBaseDamage(EntityHiveSpider.this);
			        }
			        
			        public boolean apply(Object p_apply_1_)
			        {
			            return this.func_179911_a((Entity)p_apply_1_);
			        }
			    }, 40.0F, 1.5D, 2.0D);	
		}
    }
    
    class AIHiveSpiderAttack extends EntityAIAttackOnCollide{
        public AIHiveSpiderAttack(Class p_i45819_2_){
            super(EntityHiveSpider.this, p_i45819_2_, 1.5D, true);
        }

        public boolean shouldExecute(){
        	if(super.shouldExecute()){
        	
	        	EntityHiveSpider spider = (EntityHiveSpider) this.attacker;
	        	
	        	if(spider.hive  != null && spider.hive.size() >= 10){     		        	
		        	EntityLivingBase target = this.attacker.getAttackTarget();
		        	for(EntityHiveSpider s: spider.hive){
		        		s.setAttackTarget(target);
		                System.out.println(s.getEntityId() + " targeted " + s.getAttackTarget().getName());
		        	}
	        	
		        	return true;
	        	}
        	}
        	return false;
        }
        
        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean continueExecuting(){
        	if(super.continueExecuting()){
        		EntityHiveSpider spider = (EntityHiveSpider) this.attacker;
        		if(spider.hive != null && spider.hive.size() > 2){
        			return true;
        		}
        	}
        		
        	return false;
        }

        protected double func_179512_a(EntityLivingBase p_179512_1_){
            return (double)(4.0F + p_179512_1_.width);
        }
    }
    
    class AIHiveSpiderTarget extends EntityAINearestAttackableTarget{

        public AIHiveSpiderTarget(Class target){
        	//this creature, the target class, chance to target, check sight, nearby only, predicate
            super(EntityHiveSpider.this, target, true, false);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute(){
            double d0 = this.getTargetDistance();
            List list = this.taskOwner.worldObj.getEntitiesWithinAABB(this.targetClass, this.taskOwner.getEntityBoundingBox().expand(d0, d0, d0), Predicates.and(this.targetEntitySelector, IEntitySelector.NOT_SPECTATING));
            Collections.sort(list, this.theNearestAttackableTargetSorter);

            if (list.isEmpty()){
                return false;
            }else{
                this.targetEntity = (EntityLivingBase)list.get(0);
                return true;
            }        
        }
        
        @Override
        public void startExecuting(){
            System.out.println(this.taskOwner.getEntityId() + " tracking to " + targetEntity.getName());
        	super.startExecuting();
        }
        
        @Override
        public boolean continueExecuting(){
        	if(super.continueExecuting()){
	        	EntityHiveSpider spider = (EntityHiveSpider) this.taskOwner;
	        	if(spider.getDistanceToEntity(this.targetEntity) > 40.0){
	        		spider.getNavigator().tryMoveToEntityLiving(this.targetEntity, 1.0);
	        		return true;
	        	}else if(spider.hive != null && spider.hive.size() < 10){
	        		spider.getNavigator().clearPathEntity();
	        		return true;
	        	}
        	}
        	return false;
        }
    }
}
