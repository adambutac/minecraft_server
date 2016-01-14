package com.super_deathagon.supermod.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class AbilityMessage implements IMessage{
	public enum Ability{
		ARROW(0,"ARROW"),
		TELEPORT(1,"TELEPORT"),
		FAMILIARS(2,"FAMILIARS");
		private final byte ID;
		private final String NAME;
		
		private Ability(int id, String name){
			ID = (byte)id;
			NAME = name;
		}
		
		public static Ability fromBytes(ByteBuf buf) {		 
			byte id = buf.readByte();
			for (Ability ability : Ability.values()) {
				if (id == ability.ID) return ability;
			}
			return null;	
		}

		public void toBytes(ByteBuf buf) {	
			buf.writeByte(ID);
		}
		
		@Override
		public String toString(){
			return NAME;
		}
	}
	
	/*
	 * Message code starts here!
	 */
	private UUID sendingPlayer;
	private Ability ability;
	private double[] coordinates;
	private boolean valid;
	
	public AbilityMessage(UUID sp, Ability a){
		sendingPlayer = sp;
		ability = a;
		coordinates = new double[]{0,0,0};
		valid = true;
	}
	
	public AbilityMessage(UUID sp, Ability a, double x, double  y, double z){
		sendingPlayer = sp;
		ability = a;
		coordinates = new double[]{x,y,z};
		valid = true;
	}

	public UUID getSendingPlayerUUID(){
		return sendingPlayer;
	}
	
	public Ability getAbility() {
		return ability;
	}
	
	public double[] getCoordinates(){
		return coordinates;
	}
	
	public boolean isMessageValid() {
		return valid;
	}
	
	// for use by the message handler only.
	public AbilityMessage(){
		valid = false;
	}
	
	/**
	* Called by the network code once it has received the message bytes over the network.
	* Used to read the ByteBuf contents into your member variables
	* @param buf
	*/
	@Override
	public void fromBytes(ByteBuf buf){
		try {
			sendingPlayer = new UUID(buf.readLong(), buf.readLong());
			ability = Ability.fromBytes(buf);
			if(ability == Ability.TELEPORT){
				coordinates = new double[]{buf.readDouble(), buf.readDouble(), buf.readDouble()};
			}
			valid = true;
		} catch (IndexOutOfBoundsException ioe) {
			System.err.println("Exception while reading AbilityMessageToServer: " + ioe);
		}
	}
	
	/**
	* Called by the network code.
	* Used to write the contents of your message member variables into the ByteBuf, ready for transmission over the network.
	* @param buf
	*/
	@Override
	public void toBytes(ByteBuf buf){
		if (!valid) return;
		buf.writeLong(sendingPlayer.getMostSignificantBits());
		buf.writeLong(sendingPlayer.getLeastSignificantBits());
		ability.toBytes(buf);
		if(ability == Ability.TELEPORT && coordinates != null){
			buf.writeDouble(coordinates[0]);
			buf.writeDouble(coordinates[1]);
			buf.writeDouble(coordinates[2]);
		}
	}
}
