package com.super_deathagon.itemspecial.items;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Multimap;
import com.super_deathagon.itemspecial.SpecialItems;

public class ItemSpecialGreatSword extends ItemSpecialMeele{
		
    public ItemSpecialGreatSword(){
    	super();
        //durability of an item
        this.setMaxDamage(100);
        this.attackDamage = 20.0F;
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
    	/*if(attacker.getHealth() == attacker.getMaxHealth()){
	    	AttributeModifier modify = new AttributeModifier("generic.maxHealth", target.getHealth(), 0);
	    	attacker.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(modify);
    	}
	    attacker.setHealth(attacker.getHealth() + target.getHealth());
	    stack.damageItem(1, attacker);*/
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
