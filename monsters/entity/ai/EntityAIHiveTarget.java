package com.super_deathagon.monsters.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import org.apache.commons.lang3.StringUtils;

public abstract class EntityAIHiveTarget extends EntityAIHive{
    /** If true, EntityAI targets must be able to be seen (cannot be blocked by walls) to be suitable targets. */
    protected boolean shouldCheckSight;
    /** When true, only entities that can be reached with minimal effort will be targetted. */
    private boolean nearbyOnly;
    /** When nearbyOnly is true: 0 -> No target, but OK to search; 1 -> Nearby target found; 2 -> Target too far. */
    private int targetSearchStatus;
    /** When nearbyOnly is true, this throttles target searching to avoid excessive pathfinding. */
    private int targetSearchDelay;
    /**
     * If  @shouldCheckSight is true, the number of ticks before the interuption of this AITastk when the entity does't
     * see the target
     */
    protected int targetUnseenTicks;

    public EntityAIHiveTarget(EntityCreature creature, double area, boolean checkSight){
        this(creature, area, checkSight, false);
    }

    /**
     * 
     * @param creature the owner of this EntityAI
     * @param area the area to search for other creatures
     * @param checkSight only target what the creature can see
     * @param nearOnly only target nearby entities(less than a few blocks away)
     */
    public EntityAIHiveTarget(EntityCreature creature, double area, boolean checkSight, boolean nearOnly){
    	super(creature, area);
        this.shouldCheckSight = checkSight;
        this.nearbyOnly = nearOnly;
    }
    
    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting(){
        EntityLivingBase entitylivingbase = this.taskOwner.getAttackTarget();

        if (entitylivingbase == null){
            return false;
        }else if (!entitylivingbase.isEntityAlive()){
            return false;
        }else{
            Team team = this.taskOwner.getTeam();
            Team team1 = entitylivingbase.getTeam();

            if (team != null && team1 == team){
                return false;
            }else{
                double d0 = this.getTargetDistance();

                if (this.taskOwner.getDistanceSqToEntity(entitylivingbase) > d0 * d0){
                    return false;
                }else{
                    if (this.shouldCheckSight){
                        if (this.taskOwner.getEntitySenses().canSee(entitylivingbase)){
                            this.targetUnseenTicks = 0;
                        }else if (++this.targetUnseenTicks > 60){
                            return false;
                        }
                    }

                    return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).capabilities.disableDamage;
                }
            }
        }
    }

    protected double getTargetDistance(){
        IAttributeInstance iattributeinstance = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.followRange);
        return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting(){
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.targetUnseenTicks = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask(){
    	System.out.println(this.taskOwner + " target lost.");
        this.taskOwner.setAttackTarget((EntityLivingBase)null);
    }

    public static boolean func_179445_a(EntityLiving p_179445_0_, EntityLivingBase p_179445_1_, boolean p_179445_2_, boolean p_179445_3_){
        if (p_179445_1_ == null){
            return false;
        }else if (p_179445_1_ == p_179445_0_){
            return false;
        }else if (!p_179445_1_.isEntityAlive()){
            return false;
        }else if (!p_179445_0_.canAttackClass(p_179445_1_.getClass())){
            return false;
        }else{
            Team team = p_179445_0_.getTeam();
            Team team1 = p_179445_1_.getTeam();

            if (team != null && team1 == team){
                return false;
            }else{
                if (p_179445_0_ instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)p_179445_0_).getOwnerId())){
                    if (p_179445_1_ instanceof IEntityOwnable && ((IEntityOwnable)p_179445_0_).getOwnerId().equals(((IEntityOwnable)p_179445_1_).getOwnerId())){
                        return false;
                    }

                    if (p_179445_1_ == ((IEntityOwnable)p_179445_0_).getOwner()){
                        return false;
                    }
                }else if (p_179445_1_ instanceof EntityPlayer && !p_179445_2_ && ((EntityPlayer)p_179445_1_).capabilities.disableDamage){
                    return false;
                }

                return !p_179445_3_ || p_179445_0_.getEntitySenses().canSee(p_179445_1_);
            }
        }
    }

    /**
     * A method used to see if an entity is a suitable target through a number of checks. Args : entity,
     * canTargetInvinciblePlayer
     */
    protected boolean isSuitableTarget(EntityLivingBase p_75296_1_, boolean p_75296_2_){
        if (!func_179445_a(this.taskOwner, p_75296_1_, p_75296_2_, this.shouldCheckSight)){
            return false;
        }else if (!this.taskOwner.func_180485_d(new BlockPos(p_75296_1_))){
            return false;
        }else{
            if (this.nearbyOnly){
                if (--this.targetSearchDelay <= 0){
                    this.targetSearchStatus = 0;
                }

                if (this.targetSearchStatus == 0){
                    this.targetSearchStatus = this.canEasilyReach(p_75296_1_) ? 1 : 2;
                }

                if (this.targetSearchStatus == 2){
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Checks to see if this entity can find a short path to the given target.
     */
    private boolean canEasilyReach(EntityLivingBase p_75295_1_){
        this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
        PathEntity pathentity = this.taskOwner.getNavigator().getPathToEntityLiving(p_75295_1_);

        if (pathentity == null){
            return false;
        }else{
            PathPoint pathpoint = pathentity.getFinalPathPoint();

            if (pathpoint == null){
                return false;
            }else{
                int i = pathpoint.xCoord - MathHelper.floor_double(p_75295_1_.posX);
                int j = pathpoint.zCoord - MathHelper.floor_double(p_75295_1_.posZ);
                return (double)(i * i + j * j) <= 2.25D;
            }
        }
    }
}
