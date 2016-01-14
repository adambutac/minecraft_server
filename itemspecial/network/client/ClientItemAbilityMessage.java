package com.super_deathagon.itemspecial.network.client;

import com.super_deathagon.itemspecial.network.EnumItemAbility;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ClientItemAbilityMessage implements IMessage {
	private EnumItemAbility ability;
	private byte magnitude;
	private boolean valid;

	/**
	 * Constructor used by the message handler only.
	 */
	public ClientItemAbilityMessage(){
		valid = false;
	}
	
	/**
	 * Used to trigger an ability from the server side.
	 * @param ia the type of ability requested
	 * @param mag the magnitude of the ability
	 */
	public ClientItemAbilityMessage(EnumItemAbility ia, byte mag){
		ability = ia;
		magnitude = mag;
		valid = true;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		ability = EnumItemAbility.fromBytes(buf);
		magnitude = buf.readByte();
		valid = true;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if(valid){
			ability.toBytes(buf);
			buf.writeByte(magnitude);
		}
	}

	public boolean isMessageValid(){
		return valid;
	}
	
	public EnumItemAbility getAbility(){
		return ability;
	}
	
	public byte getMagnitude() {
		return magnitude;
	}
}
