package com.super_deathagon.supermod.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.super_deathagon.abilities.Teleportation;
import com.super_deathagon.supermod.network.AbilityMessage;
import com.super_deathagon.supermod.seras.BlackMagic;

public class ServerMessageHandler implements IMessageHandler<AbilityMessage, IMessage>{

	 /**
	* Called when a message is received of the appropriate type.
	* CALLED BY THE NETWORK THREAD
	* @param message The message
	*/
	public IMessage onMessage(final AbilityMessage message, MessageContext ctx) {
		if (ctx.side != Side.SERVER) {
			System.err.println("AbilityMessageToServer received on wrong side:" + ctx.side);
			return null;
		}
		
		if (!message.isMessageValid()) {
			System.err.println("AbilityMessageToServer was invalid" + message.toString());
			return null;
		}
		// we know for sure that this handler is only used on the server side, so it is ok to assume
		// that the ctx handler is a serverhandler, and that WorldServer exists.
		// Packets received on the client side must be handled differently! See MessageHandlerOnClient
		final EntityPlayerMP sendingPlayer = ctx.getServerHandler().playerEntity;
		if (sendingPlayer == null) {
			System.err.println("EntityPlayerMP was null when AbilityMessageToServer was received");
			return null;
		}
		// This code creates a new task which will be executed by the server during the next tick,
		// for example see MinecraftServer.updateTimeLightAndEntities(), just under section
		// this.theProfiler.startSection("jobs");
		// In this case, the task is to call messageHandlerOnServer.processMessage(message, sendingPlayer)
		final WorldServer playerWorldServer = sendingPlayer.getServerForPlayer();
		playerWorldServer.addScheduledTask(new Runnable() {
			public void run() {
				System.out.println("Server: Message received.");
				processMessage(message, sendingPlayer);
			}
		});
		return null;
	}
	// This message is called from the Server thread.
	// It performs the selected ability given by the message.
	void processMessage(AbilityMessage message, EntityPlayerMP sendingPlayer){
		switch (message.getAbility()) {
		case ARROW: 
			BlackMagic.fireArrow(sendingPlayer);
			break;
		case TELEPORT: 
			double [] coords = message.getCoordinates();
			Teleportation.instantTransmission(sendingPlayer.worldObj.getPlayerEntityByUUID(message.getSendingPlayerUUID()), 
											message.getCoordinates()[0], message.getCoordinates()[1], message.getCoordinates()[2]);
			break;
		case FAMILIARS: 
			BlackMagic.makeufat(sendingPlayer);
			break;
		default: 
			System.err.println("Invalid ability type in ServerMessageHandler:" + String.valueOf(message.getAbility()));
			break;
		}
	}
}
