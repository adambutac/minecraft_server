package com.super_deathagon.itemspecial.network.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.super_deathagon.itemspecial.items.itemabilities.EnchantmentFirebolt;
import com.super_deathagon.itemspecial.network.server.ServerItemAbilityMessage;

public class ClientMessageHandler implements IMessageHandler<ServerItemAbilityMessage, IMessage>{

	@Override
	public IMessage onMessage(final ServerItemAbilityMessage message, MessageContext ctx) {
		
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			public void run() {
				System.out.println("Client: Message received.");
				processMessage(message);
			}
		});
		return null;
	}

	private void processMessage(ServerItemAbilityMessage message){		
		switch (message.getAbility()) {
		case FIREBOLT: 
				EnchantmentFirebolt.spawnFireboltParticles(message.getStartVector(), message.getEndVector(), message.getMagnitude());
			break;
		default: 
			System.err.println("Invalid ability type in ClientMessageHandler:" + String.valueOf(message.getAbility()));
			return;
		}
	}

}
