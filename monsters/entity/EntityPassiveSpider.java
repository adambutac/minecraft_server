package com.super_deathagon.monsters.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.super_deathagon.itemspecial.SpecialItems;
import com.super_deathagon.itemspecial.items.ItemSpecialMeele;
import com.super_deathagon.util.EntityAttributeModifier;
import com.super_deathagon.util.MouseOverHelper;

public class EntityPassiveSpider extends EntitySpider{
    private static final String __OBFID = "CL_00001699";
    private ArrayList<String> dropList;
    
    public EntityPassiveSpider(World world){
    	super(world);
		 dropList = new ArrayList<String>();
		 dropList.add(SpecialItems.MODID + ":itemspecialspear");
		 dropList.add("golden_sword");
		 dropList.add("golden_apple");
		 dropList.add("spider_eye");
		 this.tasks.taskEntries.remove(7);
		 this.tasks.taskEntries.remove(6);
		 this.tasks.taskEntries.remove(4);
		 this.tasks.taskEntries.remove(3);
		 this.tasks.taskEntries.remove(2);
		 this.targetTasks.taskEntries.remove(2);
		 this.targetTasks.taskEntries.remove(1);
		 this.tasks.addTask(4, new EntityPassiveSpider.AIPassiveSpiderAttack(EntityPlayer.class));
		 this.tasks.addTask(2, new EntityPassiveSpider.AIAvoidPlayer());
		 this.targetTasks.addTask(2, new AIPassiveSpiderTarget(EntityPlayer.class));
    }
    
    class AIAvoidPlayer extends EntityAIAvoidEntity{
		public AIAvoidPlayer() {
			super(EntityPassiveSpider.this , new Predicate()
			    {
			        public boolean func_179911_a(Entity e){
			            return e instanceof EntityPlayer && ((EntityPlayer)e).getHealth() > EntityAttributeModifier.getBaseDamage(EntityPassiveSpider.this);
			        }
			        
			        public boolean apply(Object p_apply_1_)
			        {
			            return this.func_179911_a((Entity)p_apply_1_);
			        }
			    }, 20.0F, 1.5D, 2.0D);	
		}
    }
    
    class AIPassiveSpiderAttack extends EntityAIAttackOnCollide{
        public AIPassiveSpiderAttack(Class p_i45819_2_){
            super(EntityPassiveSpider.this, p_i45819_2_, 1.5D, true);
        }

        public boolean shouldExecute(){
        	if(!super.shouldExecute())
        		return false;
        	
        	List<EntityPassiveSpider> friends = ((EntityPassiveSpider)this.attacker).findFriends();
        	double distance = this.attacker.getDistanceSqToEntity(this.attacker.getAttackTarget());
        	if(friends.size() < 10){
        		this.resetTask();
        		return false;
        	}
        	EntityLivingBase target = null;
        	for(int i = 0; i < friends.size(); i++){
        		if(friends.get(i).getAttackTarget() != null)
        			target = friends.get(i).getAttackTarget();
        	}
        	
        	if(target == null){
        		this.resetTask();
        		return false;
        	}
        	
        	for(EntityPassiveSpider spider: friends){
        		spider.setAttackTarget(target);
                System.out.println(spider.getUniqueID() + " targeted " + target.getName());
        	}
        	
        	return true;
        }
        
        public void startExecuting(){
        	super.startExecuting();
        }
        
        public void resetTask(){
        	super.resetTask();
        }
        
        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean continueExecuting(){
        	if(!super.continueExecuting()){
        		resetTask();
        		return false;
        	}
        	
        	double distance = this.attacker.getDistanceSqToEntity(this.attacker.getAttackTarget());
        	if(distance < 5){
        		resetTask();
        		return false;
        	}
        		
        	return true;
        }

        protected double func_179512_a(EntityLivingBase p_179512_1_){
            return (double)(4.0F + p_179512_1_.width);
        }
    }
    
    class AIPassiveSpiderTarget extends EntityAINearestAttackableTarget{
        private static final String __OBFID = "CL_00002196";

        public AIPassiveSpiderTarget(Class target){
        	//this creature, the target class, chance to target, check sight, nearby only, predicate
            super(EntityPassiveSpider.this, target, 10, true, false, new EntityPassiveSpider.SuperSpiderTargetSelector());
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
                if(this.taskOwner.getDistanceToEntity(targetEntity) > 20.0){
                    System.out.println(taskOwner.getUniqueID() + " tracking to " + targetEntity.getName());
                	this.taskOwner.getNavigator().tryMoveToEntityLiving(this.targetEntity, 1.0);
                }
                return true;
            }        
        }
    }
    
    class SuperSpiderTargetSelector implements Predicate{
        public boolean shouldTargetEntity(EntityLivingBase e)
        {
            return !(e instanceof EntitySpider);
        }

        public boolean apply(Object e)
        {
            return this.shouldTargetEntity((EntityLivingBase)e);
        }
    }
    
    public List<EntityPassiveSpider> findFriends(){
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(20, 20, 20));
        List<EntityPassiveSpider> spiders = new ArrayList<EntityPassiveSpider>();
        for(Entity e : list){
        	if(e instanceof EntityPassiveSpider){
    			spiders.add((EntityPassiveSpider) e);
        	}
        }
        return spiders;
    }
    
    @Override
    protected void applyEntityAttributes(){
        super.applyEntityAttributes();
        EntityAttributeModifier.setFollowRange(this, 50.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35);
    }

    @Override
    protected Item getDropItem(){
        return Items.string;
    }
    
    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean drop, int weight)
    {
        super.dropFewItems(drop, weight);
        
        if (drop && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + weight) > 0))
        	this.dropItem(getRandomDrop(),1);
    }
    
    /**
     * Retrieves a random item from the entities drop list.
     * @return the random item
     */
    private Item getRandomDrop(){
    	return Item.getByNameOrId(dropList.get(rand.nextInt(dropList.size())));
    }
    
    @Override
    public EntityItem dropItemWithOffset(Item itemIn, int size, float p_145778_3_){
    	ItemStack stack = new ItemStack(itemIn, size, 0);
    	if(itemIn instanceof ItemSpecialMeele)
    		((ItemSpecialMeele) itemIn).enchant(stack);
    	
        return this.entityDropItem(stack, p_145778_3_);
    }
    
    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
    protected String getLivingSound()
    {
        return "mob.spider.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected String getHurtSound()
    {
        return "mob.spider.say";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
    protected String getDeathSound()
    {
        return "mob.spider.death";
    }
}
