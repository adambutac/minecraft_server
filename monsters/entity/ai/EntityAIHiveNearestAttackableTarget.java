package com.super_deathagon.monsters.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class EntityAIHiveNearestAttackableTarget extends EntityAIHiveTarget{
	protected final Class targetClass;
    private final int targetChance;
    /** Instance of EntityAINearestAttackableTargetSorter. */
    protected final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    /**
     * This filter is applied to the Entity search.  Only matching entities will be targetted.  (null -> no
     * restrictions)
     */
    protected Predicate targetEntitySelector;
    protected EntityLivingBase targetEntity;
    private static final String __OBFID = "CL_00001620";

    /**
     * 
     * @param creature the owner of this AI
     * @param area the area to search for others of the same type of creature
     * @param target the type of entity to target
     * @param checkSight target only what this creature can see
     */
    public EntityAIHiveNearestAttackableTarget(EntityCreature creature, double area, Class target, boolean checkSight){
        this(creature, area, target, checkSight, false);
    }

    /**
     * 
     * @param creature the owner of this AI
     * @param area the area to search for others of the same type of creature
     * @param target the type of entity to target
     * @param checkSight target only what this creature can see
     * @param nearOnly target only what is near this creature
     */
    public EntityAIHiveNearestAttackableTarget(EntityCreature creature, double area, Class target, boolean checkSight, boolean nearOnly){
        this(creature, area, target, 10, checkSight, nearOnly, (Predicate)null);
    }

    /**
     * 
     * @param creature the owner of this AI
     * @param area the area to search for others of the same type of creature
     * @param target the type of entity to target
     * @param chance greater number, smaller chance (chance > 0 && random.nextInt(chance) != 0)
     * @param checkSight target only what this creature can see
     * @param nearOnly target only what is near this creature
     * @param predicate the predicate used to specifically define what to target
     */
    public EntityAIHiveNearestAttackableTarget(EntityCreature creature, double area, Class target, int chance, boolean checkSight, boolean nearOnly, final Predicate predicate){
        super(creature, area, checkSight, nearOnly);
        this.targetClass = target;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(creature);
        this.setMutexBits(1);
        this.targetEntitySelector = new Predicate(){ //ughhh nested functions
            public boolean func_179878_a(EntityLivingBase target){
                if (predicate != null && !predicate.apply(target)){
                    return false;
                }else{
                    if (target instanceof EntityPlayer){
                        double d0 = EntityAIHiveNearestAttackableTarget.this.getTargetDistance();

                        if (target.isSneaking()){
                            d0 *= 0.800000011920929D; //my followRange becomes 20% smaller?
                        }							  

                        if (target.isInvisible()){
                            float f = ((EntityPlayer)target).getArmorVisibility();

                            if (f < 0.1F){
                                f = 0.1F;
                            }

                            d0 *= (double)(0.7F * f); //if armor visibility returns 1 my follow range is reduced by 30%
                        }

                        if ((double)target.getDistanceToEntity(EntityAIHiveNearestAttackableTarget.this.taskOwner) > d0){
                            return false;
                        }
                    }

                    return EntityAIHiveNearestAttackableTarget.this.isSuitableTarget(target, false);
                }
            }
            public boolean apply(Object p_apply_1_){
                return this.func_179878_a((EntityLivingBase)p_apply_1_);
            }
        };
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute(){
    	System.out.println(this.taskOwner + " searching for targets...");
    	super.shouldExecute();
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0){
            return false;
        }else{
            double d0 = this.getTargetDistance();
            List list = this.taskOwner.worldObj.getEntitiesWithinAABB(this.targetClass, this.taskOwner.getEntityBoundingBox().expand(d0, 4.0D, d0), Predicates.and(this.targetEntitySelector, IEntitySelector.NOT_SPECTATING));
            Collections.sort(list, this.theNearestAttackableTargetSorter);

            if (list.isEmpty()){
                return false;
            }else{
                this.targetEntity = (EntityLivingBase)list.get(0);
                return true;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting(){
    	System.out.println(this.taskOwner + " target found." + this.targetEntity);
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }

    public static class Sorter implements Comparator{
        private final Entity theEntity;

        public Sorter(Entity p_i1662_1_){
            this.theEntity = p_i1662_1_;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_){
            double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
            double d1 = this.theEntity.getDistanceSqToEntity(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }

        public int compare(Object p_compare_1_, Object p_compare_2_){
            return this.compare((Entity)p_compare_1_, (Entity)p_compare_2_);
        }
    }
    
    private void targetingMode(){
    	String output = "";
    	for(EntityCreature hiveCreature: this.hive){
    		EntityLivingBase hiveTarget = hiveCreature.getAttackTarget();
    		output += taskOwner + "->" + targetEntity + ":" + " " + hiveCreature + "->" + hiveTarget + "\n";
    		
    		if(hiveTarget == null){
    			if(targetEntity != null){
    				hiveCreature.setAttackTarget(targetEntity);
    			}
    		}else{
    			if(targetEntity == null){
    				this.taskOwner.setAttackTarget(hiveTarget);
    			}
    		}
    		
    		output += this.taskOwner + "->" + targetEntity + ":" + " " + hiveCreature + "->" +  hiveCreature.getAttackTarget() + "\n";
    	}
    	System.out.println(output);
    }
}
