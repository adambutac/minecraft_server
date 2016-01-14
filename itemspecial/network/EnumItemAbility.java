package com.super_deathagon.itemspecial.network;

import io.netty.buffer.ByteBuf;


public enum EnumItemAbility{
	TELEPORT(0,"TELEPORT"),
	FLY(1,"FLY"),
	FIREBOLT(2,"FIREBOLT"),
	LIGHTNING(3,"LIGHTNING"),
	FLOOD(4,"FLOOD"),
	FREEZE(5,"FREEZE"),
	EXPLOSION(6,"EXPLOSION"),
	FAMILIARS(7,"FAMILIARS");
	private final byte ID;
	private final String NAME;
	
	private EnumItemAbility(int id, String name){
		ID = (byte)id;
		NAME = name;
	}
	
	@Override
	public String toString(){
		return "ItemAbility Name:  " + NAME + " ID: " + ID;
	}
	
	public String getName(){
		return NAME;
	}
	
	public static EnumItemAbility fromBytes(ByteBuf buf) {		 
		byte id = buf.readByte();
		for (EnumItemAbility ability : EnumItemAbility.values()) {
			if (id == ability.ID) return ability;
		}
		return null;	
	}

	public void toBytes(ByteBuf buf) {	
		buf.writeByte(ID);
	}
	
	public byte getID(){
		return ID;
	}		
} 

