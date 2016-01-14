package com.super_deathagon.itemspecial.items;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Multimap;

public class ItemSpecialSpear extends ItemSpecialMeele{
	
    
    public ItemSpecialSpear(){
    	super();
        //durability of an item
        this.setMaxDamage(100);
        this.attackDamage = 9.0F;
    }
    
    public float getStrVsBlock(ItemStack stack, Block block){
        if (block == Blocks.web){
            return 15.0F;
        }
        else{
            Material material = block.getMaterial();
            return material != Material.plants && 
            		material != Material.vine && 
            		material != Material.coral && 
            		material != Material.leaves && 
            		material != Material.gourd ? 1.0F : 1.5F;
        }
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker){
    	if(attacker.getHealth() == attacker.getMaxHealth()){
	    	AttributeModifier modify = new AttributeModifier("generic.maxHealth", target.getHealth(), 0);
	    	attacker.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(modify);
    	}
	    attacker.setHealth(attacker.getHealth() + target.getHealth());
	    stack.damageItem(1, attacker);
        return true;
    }

    /**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn)
    {
        if ((double)blockIn.getBlockHardness(worldIn, pos) != 0.0D)
        {
            stack.damageItem(2, playerIn);
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D(){
        return true;
    }
    
    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     *  
     * @param timeLeft The amount of ticks left before the using would have been complete
     */
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft){
        int j = (this.getMaxItemUseDuration(stack) - timeLeft);
        int seconds = 4;
        int ticks = 4*20;
        if(j > ticks)
        	j = ticks;
        
		super.useItemAbility(stack, world, player, j);
	    stack.damageItem(1, player);
	    if(stack.getItemDamage() == stack.getMaxDamage()){
	    	destroyItemHeld(world, player);
	    }
    }
    
    @SideOnly(Side.CLIENT)
    private void destroyItemHeld(World world, EntityPlayer player){
    	player.inventory.decrStackSize(player.inventory.currentItem,1);
		EffectRenderer rend = Minecraft.getMinecraft().effectRenderer;
    	EntityFlameFX.Factory flameFXF = new EntityFlameFX.Factory();
    	EntityFlameFX flameFX = (EntityFlameFX) flameFXF.getEntityFX(0, world, 
    												player.posX + player.getLookVec().xCoord,
    												player.posY + player.getLookVec().yCoord + player.getEyeHeight(),
    												player.posZ + player.getLookVec().zCoord, 
    												0, 0.05f, 0);
    	System.out.println(player.getLookVec());
    	flameFX.setRBGColorF(0.0f, 0f, 0.63f);
    	rend.addEffect(flameFX);
    	player.playSound("random.break", 1.0f, 1.0f);
    }
    
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn){
    	return stack;
    }

    public EnumAction getItemUseAction(ItemStack stack){
        return EnumAction.BOW;
    }
    
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn){
        playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        return itemStackIn;
    }
    
    public boolean canHarvestBlock(Block blockIn){
        return false;
    }

    public int getItemEnchantability(){
        return 0;
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair){
        return false;
    }
    
    public int getMaxItemUseDuration(ItemStack stack){
        return 72000;
    }
    
    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    public Multimap getItemAttributeModifiers()
    {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", (double)this.attackDamage, 0));
        return multimap;
    }
}
