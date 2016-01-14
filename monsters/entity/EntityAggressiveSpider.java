package com.super_deathagon.monsters.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.super_deathagon.itemspecial.SpecialItems;
import com.super_deathagon.itemspecial.items.ItemSpecialMeele;
import com.super_deathagon.util.EntityAttributeModifier;

public class EntityAggressiveSpider extends EntitySpider{
    private static final String __OBFID = "CL_00001699";
    private ArrayList<String> dropList;
    
    public EntityAggressiveSpider(World world){
    	super(world);
    	 dropList = new ArrayList<String>();
         dropList.add(SpecialItems.MODID + ":itemspecialspear");
         dropList.add("golden_sword");
         dropList.add("golden_apple");
         dropList.add("spider_eye");
         
         this.tasks.addTask(4, new EntityAggressiveSpider.AISpiderAttack(EntityPlayer.class));
         this.targetTasks.addTask(2, new AIAggressiveSpiderTarget(EntityPlayer.class));
    }
    
    class AISpiderAttack extends EntityAIAttackOnCollide
    {
        private static final String __OBFID = "CL_00002197";

        public AISpiderAttack(Class p_i45819_2_)
        {
            super(EntityAggressiveSpider.this, p_i45819_2_, 1.0D, true);
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean continueExecuting()
        {
            float f = this.attacker.getBrightness(1.0F);

            if (f >= 0.5F && this.attacker.getRNG().nextInt(100) == 0)
            {
                this.attacker.setAttackTarget((EntityLivingBase)null);
                return false;
            }
            else
            {
                return super.continueExecuting();
            }
        }

        protected double func_179512_a(EntityLivingBase p_179512_1_)
        {
            return (double)(4.0F + p_179512_1_.width);
        }
    }
    
    class AIAggressiveSpiderTarget extends EntityAINearestAttackableTarget{
        private static final String __OBFID = "CL_00002196";

        public AIAggressiveSpiderTarget(Class target){
        	//this creature, the target class, chance to target, check sight, nearby only, predicate
            super(EntityAggressiveSpider.this, target, 10, true, false, new EntityAggressiveSpider.SuperSpiderTargetSelector());
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
    
    public EntityAggressiveSpider(World worldIn, ArrayList<String> drops){
        super(worldIn);
       dropList = drops;
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
}