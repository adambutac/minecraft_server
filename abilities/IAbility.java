package com.super_deathagon.abilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IAbility{
	public static final int MAX_MAG = Byte.MAX_VALUE;
	public void useAbility(World world, EntityPlayer player, int level, int charge);
}
