package com.super_deathagon.monsters.entity;

import java.util.LinkedHashSet;

import com.google.common.base.Predicate;
import com.super_deathagon.monsters.entity.ai.EntityAIFollowTarget;
import com.super_deathagon.util.EntityAttributeModifier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;


public class EntityHiveSpider2 extends EntitySpider{
	LinkedHashSet<EntityHiveSpider2> hive = new LinkedHashSet<EntityHiveSpider2>();
	
    public EntityHiveSpider2(World worldIn){
        super(worldIn);
		 //this.tasks.taskEntries.remove(7);//look idle
		 //this.tasks.taskEntries.remove(6);//watch closest
		 //this.tasks.taskEntries.remove(5);//wander
		 this.tasks.taskEntries.remove(4);//attack golem
		 this.tasks.taskEntries.remove(3);//attack player
		 this.tasks.taskEntries.remove(2);//leap
		 this.tasks.taskEntries.remove(1);//avoid exploding creeper
		 this.targetTasks.taskEntries.remove(2);//target golem
		 this.targetTasks.taskEntries.remove(1);//target player
		 //this.tasks.addTask(2,new EntityHiveSpider2.AIAvoidPlayer());
		// this.tasks.addTask(2,new EntityAIAttackWithGroup(this, EntityPlayer.class, 1.0, false));
		 this.targetTasks.addTask(3, new EntityAIFollowTarget(this, EntityPlayer.class, false));
    }

    @Override
    protected void applyEntityAttributes(){
        super.applyEntityAttributes();
        EntityAttributeModifier.setFollowRange(this, 20.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35);
    }

    /**
     * Changed so this spider cannot spawn with a skeleton jockey.
     */
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
			super(EntityHiveSpider2.this , new Predicate(){
		        public boolean func_179911_a(Entity e){
		            return e instanceof EntityPlayer && ((EntityPlayer)e).getHealth() > EntityAttributeModifier.getBaseDamage(EntityHiveSpider2.this);
		        }
		        
		        public boolean apply(Object p_apply_1_)
		        {
		            return this.func_179911_a((Entity)p_apply_1_);
		        }
		    }, 30.0F, 1.5D, 2.0D);	
		}
    }
}
