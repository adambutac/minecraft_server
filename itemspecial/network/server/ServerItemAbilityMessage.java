package com.super_deathagon.itemspecial.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import com.super_deathagon.itemspecial.network.EnumItemAbility;

public class ServerItemAbilityMessage implements IMessage{
	private EnumItemAbility ability;
	private Vec3 startVec;
	private Vec3 endVec;
	private byte magnitude;
	private boolean valid;

	/**
	 * Constructor used by the message handler only.
	 */
	public ServerItemAbilityMessage(){
		valid = false;
	}
	
	/**
	 * ClientItemAbilityMessage is used by the client to send an ItemAbilityMessage
	 * to the server requesting to use that ability.
	 * @param ia The ability being requested.
	 * @param mag The magnitude the ability should be performed at.
	 * @param x The x coordinate for which the ability should be used.
	 * @param y The y coordinate for which the ability should be used.
	 * @param z The z coordinate for which the ability should be used.
	 */
	public ServerItemAbilityMessage(EnumItemAbility ia, byte mag, Vec3 start, Vec3 end){
		ability = ia;
		magnitude = mag;
		startVec = start;
		endVec = end;
		valid = true;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		ability = EnumItemAbility.fromBytes(buf);
		magnitude = buf.readByte();
		startVec = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
		endVec = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
		valid = true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if(valid){
			ability.toBytes(buf);
			buf.writeByte(magnitude);
			buf.writeDouble(startVec.xCoord);
			buf.writeDouble(startVec.yCoord);
			buf.writeDouble(startVec.zCoord);
			buf.writeDouble(endVec.xCoord);
			buf.writeDouble(endVec.yCoord);
			buf.writeDouble(endVec.zCoord);
		}
	}

	public boolean isMessageValid(){
		return valid;
	}
	
	public EnumItemAbility getAbility(){
		return ability;
	}
	
	public Vec3 getStartVector(){
		return startVec;
	}
	
	public Vec3 getEndVector(){
		return endVec;
	}
	
	public byte getMagnitude() {
		return magnitude;
	}
}
