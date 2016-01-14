package com.super_deathagon.supermod.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.super_deathagon.abilities.Teleportation;
import com.super_deathagon.supermod.network.AbilityMessage;

public class ClientMessageHandler implements IMessageHandler<AbilityMessage, IMessage>{

	@Override
	public IMessage onMessage(final AbilityMessage message, MessageContext ctx) {
		
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			public void run() {
				processMessage(message);
			}
		});
		return null;
	}

	private void processMessage(AbilityMessage message){
		World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer sendingPlayer = world.getPlayerEntityByUUID(message.getSendingPlayerUUID());
		
		switch (message.getAbility()) {
		case TELEPORT: 
			Teleportation.spawnTeleportParticlesHelix(sendingPlayer);
			break;
		default: 
			System.err.println("Invalid ability type in ClientMessageHandler:" + String.valueOf(message.getAbility()));
			break;
		}
	}
}
