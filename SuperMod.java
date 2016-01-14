package com.super_deathagon;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.base.Predicates;
import com.super_deathagon.SuperMod.MonsterCheats.Deimos;
import com.super_deathagon.SuperMod.MonsterCheats.Hypnosis;
import com.super_deathagon.abilities.Teleportation;
import com.super_deathagon.util.EntityAttributeModifier;
import com.super_deathagon.util.MouseOverHelper;

import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid 	 = 	"meep",
	name 	 = 	"Super's mod",
	version  = 	"0.0.1",
	acceptableRemoteVersions = "*")
@SideOnly(Side.SERVER)
public class SuperMod {
	
	@EventHandler
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event){
		for(BiomeGenBase biome: BiomeGenBase.getBiomeGenArray()){
			/* Set snowing on all biomes because its winter! */
			if(biome != null){
				biome.setEnableSnow().setTemperatureRainfall(0.0F, 0.5F);
			}
		}
	}
	
	@EventHandler
	public void fmlLifeCycleEvent(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new MonsterCheats());
		MinecraftForge.EVENT_BUS.register(new SuperCheats());
		MinecraftForge.EVENT_BUS.register(new DoubleXPWeekends());
		MinecraftForge.EVENT_BUS.register(new MessageSystem());
	}
	
	/**
	 * @return The current moon phase, or 0 if it is daytime.
	 */
	public static float getMoonPhase(){
		World serverWorld = MinecraftServer.getServer().getEntityWorld();
		if(serverWorld.isDaytime())
			return 0f;
		else
			return serverWorld.getCurrentMoonPhaseFactor();
	}
	
	/************************************************************************
	 * 
	 * MessageSystem
	 * 
	 * A simple Messaging action.
	 * 
	 * @author super
	 *
	 ************************************************************************/
	public class MessageSystem{
		private ArrayList<EntityPlayer> receivedNote = new ArrayList<EntityPlayer>();
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(PlayerEvent.PlayerLoggedInEvent event){
			if(!(event.player instanceof EntityPlayer)) return;
			
			EntityPlayer player = (EntityPlayer)event.player;
			String name = player.getName();
			String message = "Welcome " + name + ", ";
			MinecraftServer mcs = MinecraftServer.getServer();
			ServerConfigurationManager scm = mcs.getConfigurationManager();
			
			if(name.equals("Rippumm")){
				message += "protector of the relm!";
			}else if(name.equals("FTK6")){
				PotionEffect resp = new PotionEffect(Potion.waterBreathing.id, 1000000, 10, false, false);
				player.addPotionEffect(resp);
				message += " \"Let me ask you; does a machine like yourself ever experience fear?\"";
			}else if(name.equals("kimbasimba")){
				message += " the shadows tremble in your presence...";
			}else if(name.equals("Super_Deathagon")){
				//PotionEffect regen = new PotionEffect(Potion.regeneration.id, 1000000, 10, false, false);
				//player.addPotionEffect(regen);
				
				message += "the uncertainty!";
				scm.sendPacketToAllPlayers(new S02PacketChat( new ChatComponentText(message)));
				message = "Greetings, master!";
				for(String s:mcs.getAllUsernames())
					if(!s.equals("Super_Deathagon"))
						scm.sendPacketToAllPlayers(new S02PacketChat( 
							new ChatComponentText("<"+s+"> "+message)));
				message = "";
			}
			scm.sendPacketToAllPlayers(new S02PacketChat( new ChatComponentText(message)));
			
			for(EntityPlayer p: receivedNote){
				if(p.getName().equals(player.getName()))
					return;
			}
			
			String notes = "Patch notes version 1.8.1:\n";
			player.addChatMessage(new ChatComponentText(notes));
			receivedNote.add(player);
		}
	}
	
	/************************************************************************
	 * 
	 * DoubleXPWeekends
	 * 
	 * Double that XP!
	 * 
	 * @author super
	 *
	 ************************************************************************/
	public class DoubleXPWeekends{
		/* Double XP on weekends! */		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(PlayerPickupXpEvent event){
			int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			if(day == Calendar.FRIDAY || day == Calendar.SATURDAY || day == Calendar.SUNDAY)
				event.orb.xpValue = event.orb.xpValue * 2;
		}
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(EntityJoinWorldEvent event){
			if(!(event.entity instanceof EntityPlayer)) return;
			
			int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			if(day != Calendar.SATURDAY && day != Calendar.SUNDAY) return;
			
			EntityPlayer player = (EntityPlayer)event.entity;
			MinecraftServer mcs = MinecraftServer.getServer();
			ServerConfigurationManager scm = mcs.getConfigurationManager();
			String message = "It's the weekend! Players will receive double experience for everything!\n"
					+ "    You get double XP, and you get double XP! EVERYONE GETS DOUBLE XP WEEKEND!!\n\n";
			
			player.addChatMessage(new ChatComponentText(message));
		}
	}
	
	/************************************************************************
	 * 
	 * SuperCheats
	 * 
	 * Abilities Cheats and other nonsense.
	 * 
	 * @author super
	 *
	 ************************************************************************/
	public class SuperCheats{
		
		/* Make players scale with their current level. */
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(PlayerPickupXpEvent event){
			EntityPlayer player = event.entityPlayer;
			int xp = event.orb.xpValue;
			int playerLevel = player.experienceLevel;
	        float playerXp = player.experience + (float)xp / (float)player.xpBarCap();
	        
	        if(playerXp >= 1.0f){
	        	playerLevel++;
	        	player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0 + playerLevel/30.0);
	        	player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.1 + (playerLevel/1000.0));
	        	player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(playerLevel/1000.0);
	        	player.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(playerLevel/30.0);
	        }
		}
		
		/* Make sure a player respawns with the correct stats. */
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(PlayerRespawnEvent event){
			EntityPlayer player = event.player;
			int playerLevel = player.experienceLevel;
			
			player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0 + playerLevel/30.0);
        	player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.1 + (playerLevel/1000.0));
        	player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(playerLevel/1000.0);
        	player.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(playerLevel/30.0);
		}
		
		/**
		 * Used to hook into code when a player uses an item.
		 * Selection can be done using player names, item names, and other things.
		 * This only works on items that forge hooks into.
		 * 
		 * Currently:
		 *    ItemBow, ItemBucketMilk, ItemFood, ItemPotion, ItemSword and their children. */
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(PlayerUseItemEvent.Start event){
			EntityPlayer player = event.entityPlayer;
			final String name = player.getName();
			Item item = event.item.getItem();
			
			if(name.equals("kimbasimba") && item.equals(Items.wooden_sword)){
                event.setCanceled(true);
    			World world = player.worldObj;
                double right = 0.5;
        		Vec3 mouseOverVec = MouseOverHelper.getMouseOverAll(player, 50).hitVec;
        		float itemLookAngle = (float) Math.tan(right/player.getPositionVector().distanceTo(mouseOverVec));

                //This is the un-obfuscated code from minecraft used to 
                //calculate the x y and z components of a vector
                float f = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch);
                float f1 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw);
                float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI + itemLookAngle);
                float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI + itemLookAngle);
                float f4 = -MathHelper.cos(-f * 0.017453292F);
                float f5 = MathHelper.sin(-f * 0.017453292F);
                float f6 = f3 * f4;
                float f7 = f2 * f4;
                
                world.playAuxSFXAtEntity((EntityPlayer)null, 1008, new BlockPos(player), 0);
                world.playAuxSFXAtEntity((EntityPlayer)null, 1007, new BlockPos(player), 0);

                EntityLargeFireball entitylargefireball = 
                		new EntityLargeFireball(player.worldObj, player, f6*5, f5*5, f7*5);
                entitylargefireball.accelerationX = f6*0.45;
                entitylargefireball.accelerationY = f5*0.45;
                entitylargefireball.accelerationZ = f7*0.45;
                entitylargefireball.explosionPower = (int)Math.round(Math.random()*3+1);
                entitylargefireball.posX = player.posX + f6;
                entitylargefireball.posY = player.posY + (double)(player.height / 2.0F) + 0.5D;
                entitylargefireball.posZ = player.posZ + f7;
                world.spawnEntityInWorld(entitylargefireball);
                event.item.damageItem(1, player);
			}
			
			if(name.equals("Rippumm") && item.equals(Items.diamond_sword)){
				event.setCanceled(true);
				WorldServer server = MinecraftServer.getServer().worldServerForDimension(player.dimension);
        		MovingObjectPosition mop = MouseOverHelper.getMouseOverAll(player, 20);
                if(mop.entityHit != null && player.getDistanceToEntity(mop.entityHit) >= 2){
                	Entity entity = mop.entityHit;
                	Vec3 hit = mop.hitVec;
            		Vec3 eCenter = new Vec3(entity.posX, entity.posY + entity.height/2.0, entity.posZ);
            		Vec3 pCenter = new Vec3(player.posX, player.posY + player.height/2.0, player.posZ);
            		Vec3 vec = pCenter.subtract(eCenter).add(player.getLookVec());
            		
            		entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 0.5f);
            		entity.moveEntity(vec.xCoord, vec.yCoord, vec.zCoord);
            		
            		if(entity instanceof EntityPlayerMP){
        				((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
            		}
            		server.spawnParticle(EnumParticleTypes.CRIT, false, 
				       					 hit.xCoord, hit.yCoord, hit.zCoord, 25,
				       					 0.25, 0.25, 0.25, 0.25, new int[0]);
                	server.playSoundEffect(player.posX, player.posY, player.posZ, "mob.endermen.scream", 1.0F, 1.0F);
                }else{
                	player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "mob.endermen.hit", 1.0F, 1.0F);
                }
                event.item.damageItem(8, player);
			}
			
			if(item.equals(Items.diamond_sword) && event.item.getDisplayName().equals("meep")){
				if(player.inventory.hasItem(Items.porkchop)){
	        		EntityLivingBase entity = MouseOverHelper.getMouseOverEntityLiving(player, 20);
					if(entity != null){
						for(int i = 0; i < player.experienceLevel/30 + 1; i++){
							EntityPig pig = new EntityPig(player.worldObj);
					        Hypnosis.brainWashPig(pig);
					        
							pig.setPosition(player.posX, player.posY, player.posZ);
							pig.setAttackTarget(entity);
							pig.setFire(500);
							player.worldObj.spawnEntityInWorld(pig);
							player.inventory.consumeInventoryItem(Items.porkchop);
						}
					}
				}
			}
			
			if(item.equals(Items.diamond_sword) 
			&& event.item.getDisplayName().equals("Panty On") 
			&& player.getLookVec().yCoord >= 0.8f){
				
				player.setPositionAndUpdate(player.posX, player.posY + 80, player.posZ);
				player.addVelocity(0, 8, 0);
				((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
				if(!noFall.contains(name))
					noFall.add(name);
			}
			
			if(item.equals(Items.diamond_sword)
			&& name.equals("Super_Deathagon")){
				ArrayList<EntityLiving> skeles = new ArrayList<EntityLiving>(3);
				Deimos deimos = new Deimos();
				for(int i = 0; i < 3; i++){
					EntitySkeleton skele = new EntitySkeleton(player.worldObj);
					skele.setSkeletonType(1);
					skele.setPositionAndRotation(player.posX, player.posY + 1, player.posZ, 
							 player.rotationYaw, player.rotationPitch);
					skeles.add(skele);
				}
				for(EntityLiving skele: skeles){
					deimos.transform((EntitySkeleton)skele, skeles);
					player.worldObj.spawnEntityInWorld(skele);
				}
			}
		}
		
		ArrayList<String> noFall = new ArrayList<String>();
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(LivingHurtEvent event){
			if(event.source.equals(DamageSource.fall) 
			&& event.entityLiving instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer)event.entityLiving;
				for(int i = 0; i < noFall.size(); i++){
					String name = noFall.get(i);
					if(player.getName().equals(name)){
						noFall.remove(i);
						event.ammount = 0;
						player.fallDistance = 0;
						player.worldObj.createExplosion(player, player.posX, player.posY, player.posZ, 5, true);
					}
				}
			}
		}
		
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(PlayerUseItemEvent.Stop event){
			EntityPlayer player = event.entityPlayer;
			String name = player.getName();
			Item item = event.item.getItem();
			
			if(name.equals("FTK6")&& item.equals(Items.bow) 
			&& event.item.getDisplayName().equals("FTK6")){
				event.setCanceled(true);
				int i = (int)((event.item.getMaxItemUseDuration() - event.duration)*1.75);
				if(i > 100) i = 100;
	    		Teleportation.teleportToLook(player, i);
	            event.item.damageItem(8, player);
			}
		}

			/* DANGEROUS CODE!! But still interesting. Caused a null pointer error that I can't get rid of.
			 * Added fake players to the game so more monsters would spawn with vanilla code. 
			 * I think it was trying to send particle updates to them and thats what caused the error. */
			//if(!name.endsWith("_ghost")){
			//	FakePlayer[] fakes = new FakePlayer[diff];
			//	ghosts.put(name, fakes);
			//	for(int i = 0; i < diff; i++){
			//		GameProfile profile = new GameProfile(UUID.randomUUID(), player.getName() + i +"_ghost");
			//		WorldServer server = MinecraftServer.getServer().worldServerForDimension(player.dimension);
			//		FakePlayer ghost = 	FakePlayerFactory.get(server, profile);
					//ghost.setInvisible(true);
					//ghost.preparePlayerToSpawn();
			//		ghost.setPosition(player.posX, player.posY, player.posZ);
					//player.worldObj.spawnEntityInWorld(ghost);
			//		player.worldObj.playerEntities.add(ghost);
			//		fakes[i] = ghost;
			//	}
			//}
			//for(Object pl: player.worldObj.playerEntities)
			//	System.out.println(((EntityPlayer)pl).getName());
		//}
		/*  DANGEROUS CODE!! Fake player code. See the bottom of EntityJoinWorldEvent hook.
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(LivingEvent.LivingUpdateEvent event){
			if(!(event.entityLiving instanceof EntityPlayer)) return;
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			if(!player.getName().endsWith("_ghost"))
			for(int i = 0; i < diff; i++){
				EntityPlayer ghost = ghosts.get(player.getName())[i];
				ghost.setPosition(player.posX, player.posY, player.posZ);
				//System.out.println(ghost.getName() + " " + ghost.getPosition());
			}
		}
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(PlayerChangedDimensionEvent event){
			EntityPlayer player = event.player;
			WorldServer serverFrom = MinecraftServer.getServer().worldServerForDimension(event.fromDim);
			WorldServer serverTo = MinecraftServer.getServer().worldServerForDimension(event.toDim);
			for(int i = 0; i < diff; i++){
				FakePlayer ghost = ghosts.get(player.getName())[i];
				serverFrom.playerEntities.remove(ghost);
				serverTo.playerEntities.add(ghost);
				ghost.dimension = player.dimension;
				ghost.setPosition(player.posX, player.posY, player.posZ);
			}
		}
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(PlayerLoggedOutEvent event){
			EntityPlayer player = event.player;
			for(int i = 0; i < diff; i++){
				FakePlayer ghost = ghosts.get(player.getName())[i];
				WorldServer server = MinecraftServer.getServer().worldServerForDimension(ghost.dimension);
				server.playerEntities.remove(ghost);
			}
		}
		*/
	}
	/************************************************************************
	 * 
	 * IEntityTransformer
	 * 
	 * Declares what an EntityTransformer should have.
	 * 
	 * @author super
	 *
	 ************************************************************************/
	public interface IEntityTransformer{
		public void setName(String s);
		
		public void setShowTag(boolean show);
				
		public void setAttackDamage(double damage);
		
		public void setMaxHealth(double health);
		
		public void setMovementSpeed(double speed);
		
		public void setFollowRange(double range);
		
		public void setKnockbackResistance(double resistance);
		
		public void setBaseAttributes(double damage, double hp, double speed, double range, double resist);
		
		public void setSpawnChance(float chance);
		
		public void setDropChance(float chance);

		public void setDropExperienceAmount(int amount);

		public boolean rollSpawnChance();

		public int getDropExperienceAmount();

		public String getName();

		public void transform(EntityMob mob);
	}
	
	/************************************************************************
	 * 
	 * MonsterCheats
	 * 
	 * All monster mods should be done here.
	 * 
	 * @author super
	 *
	 ************************************************************************/
	public static class MonsterCheats{		
		private Archos archos;
		private Deimos deimos;
		private Toros toros;
		private QuickSpider quickSpider;
		private Meeper meeper;
		
		public MonsterCheats(){
			archos = new Archos();
			deimos = new Deimos();
			toros = new Toros();
			quickSpider = new QuickSpider();
			meeper = new Meeper();
		}
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(LivingPackSizeEvent event){
			EntityLivingBase e = event.entityLiving;
			if     (e instanceof  EntityCreeper)  event.maxPackSize = 1000;
			else if(e instanceof  EntitySkeleton) event.maxPackSize = 1000;
			else if(e instanceof  EntitySpider)   event.maxPackSize = 1000;
			else if(e instanceof  EntityZombie)   event.maxPackSize = 1000;
		}
		
		//@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		//public void onEvent(TickEvent.WorldTickEvent event){
		//	if(event.phase == Phase.START){
		//		
		//	}
		//}
		
		/* This was used to help deimos spawn with his enchanted bow. */
		//@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		//public void onEvent(LivingSpawnEvent.SpecialSpawn event){
		//	if(event.entityLiving.getName().equals("deimos"))
		//		event.setCanceled(true);
		//}
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(LivingSpawnEvent.CheckSpawn event){
			Entity e = event.entity;
			
			//if     (e instanceof  EntityCreeper)
			//	catchEntitySpawn((EntityCreeper) e);
			if(e instanceof  EntitySkeleton)
				catchEntitySpawn((EntitySkeleton)e);
			else if(e instanceof  EntitySpider)
				catchEntitySpawn((EntitySpider)  e);
			else if(e instanceof  EntityZombie)
				catchEntitySpawn((EntityZombie)  e);
		}
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(LivingExperienceDropEvent event){
			String name = event.entityLiving.getCustomNameTag();
			if(name.equals(archos.getName()))
				event.setDroppedExperience(archos.getDropExperienceAmount());
			else if(name.equals(deimos.getName()))
				event.setDroppedExperience(deimos.getDropExperienceAmount());
			else if(name.equals(toros.getName()))
				event.setDroppedExperience(toros.getDropExperienceAmount());
		}		
		
		private void catchEntitySpawn(EntitySpider spider){
			if(archos.rollSpawnChance()){
				archos.transform(spider);
			}else{
				quickSpider.transform(spider);
			}
		}
		
		private void catchEntitySpawn(EntitySkeleton skeleton){
			if(deimos.rollSpawnChance() && skeleton.getCanSpawnHere() && skeleton.handleLavaMovement()){
				ArrayList<EntityLiving> skeles = new ArrayList<EntityLiving>(3);
				for(int i = 0; i < 3; i++){
					EntitySkeleton skele = new EntitySkeleton(skeleton.worldObj);
					skele.setSkeletonType(1);
					skele.setPositionAndRotation(skeleton.posX, skeleton.posY + 1, skeleton.posZ, 
							 skeleton.rotationYaw, skeleton.rotationPitch);
					skeles.add(skele);
				}
				for(EntityLiving skele: skeles){
					deimos.transform((EntitySkeleton)skele, skeles);
					skeleton.worldObj.spawnEntityInWorld(skele);
				}

			}else if(skeleton.getCanSpawnHere()){
				skeleton.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30);
				entitySpawnMultiplier(skeleton, (int) (getMoonPhase()*4));
			}
		}
		
		private void catchEntitySpawn(EntityZombie zombie){
			if(toros.rollSpawnChance()){
				toros.transform(zombie);
			}else if(zombie.getCanSpawnHere()){
				zombie.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30);
				entitySpawnMultiplier(zombie, (int) (getMoonPhase()*8));
			}
		}
		
		private void catchEntitySpawn(EntityCreeper creeper){
			if(meeper.rollSpawnChance()){
				meeper.transform(creeper);
			}
		}
		
		public ArrayList<EntityLiving> entitySpawnMultiplier(EntityLiving entity, int amount){
			ArrayList<EntityLiving> list = new ArrayList<EntityLiving>();
			World world = entity.worldObj;
			IEntityLivingData data = null;
			
			try {
				for(int i = 0; i < amount; i++){
					EntityLiving ditto = entity.getClass().getConstructor(World.class).newInstance(world);
					data = ditto.func_180482_a(world.getDifficultyForLocation(ditto.getPosition()), data);
					ditto.setPositionAndRotation(entity.posX, entity.posY + 1, entity.posZ, 
												 entity.rotationYaw, entity.rotationPitch);
					list.add(ditto);
					entity.worldObj.spawnEntityInWorld(ditto);
				}
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return list;
		}

		public static class EntityTransformer implements IEntityTransformer{
			private String name;
			private double attackDamage;
			private double maxHealth;
			private double movementSpeed;
			private double knockbackResistance;
			private double followRange;
			private float spawnChance;
			private float dropChance;
			private int expDropAmount;
			private boolean showTag = true;
			
			public EntityTransformer(){
				name = null;		
				attackDamage = -1;
				maxHealth = -1;
				movementSpeed = -1;
				knockbackResistance = -1;
				followRange = -1;
				spawnChance = 0;
				dropChance = 0;
				expDropAmount = 0;
				showTag = true;
			}
	
			public void setName(String s){
				if(s != null && !s.isEmpty())name = s;}
					
			public void setShowTag(boolean show){showTag = show;}
			
			public void setAttackDamage(double damage){
				if(damage >= 0) attackDamage = damage;}
			
			public void setMaxHealth(double health){
				if(health >= 0) maxHealth = health;}
			
			public void setMovementSpeed(double speed){ 
				if(speed >= 0) movementSpeed = speed;}
			
			public void setFollowRange(double fRange){
				if(fRange >= 0) followRange = fRange;}
			
			public void setKnockbackResistance(double kResist){
				if(kResist >= 0) knockbackResistance = kResist;}
			
			public void setBaseAttributes(double damage, double health, double speed, double fRange, double kResist){
				if(damage  >= 0) attackDamage        = damage;
				if(health  >= 0) maxHealth           = health;
				if(speed   >= 0) movementSpeed       = speed;
				if(fRange  >= 0) followRange         = fRange;
				if(kResist >= 0) knockbackResistance = kResist;
			}
			
			public boolean rollSpawnChance() {return Math.random() < spawnChance;}
			public void    setSpawnChance (float chance) {spawnChance = chance;}
			public void    setDropChance  (float chance) {dropChance = chance;}
			public int     getDropExperienceAmount() {return expDropAmount;}
			public void    setDropExperienceAmount(int amount) {expDropAmount = amount;}
			public String  getName() {return name;}
	
			public void transform(EntityMob mob) {	
				/* Do name things. */
				if(name != null){
					mob.setCustomNameTag(name);
					mob.setAlwaysRenderNameTag(showTag);
				}
				/* Set the drop chance of items on the entity. */
				if(dropChance >= 0){
					mob.setEquipmentDropChance(0, dropChance);
					mob.setEquipmentDropChance(1, dropChance);
					mob.setEquipmentDropChance(2, dropChance);
					mob.setEquipmentDropChance(3, dropChance);
					mob.setEquipmentDropChance(4, dropChance);
				}
				/* Base attribute things. */
				if(attackDamage >= 0)
					mob.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(attackDamage);
				if(maxHealth >= 0){
					mob.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth);
					mob.heal((float) maxHealth);
				}
				if(movementSpeed >= 0)
					mob.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(movementSpeed);
				if(knockbackResistance >= 0)
					mob.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(knockbackResistance);
				if(followRange >= 0)	
					mob.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(followRange);
			}
		}
		
		public static class Archos extends EntityTransformer{
			public static final String name = "Archos";
			private static final PotionEffect archosRegen = new PotionEffect(Potion.regeneration.id, 1000000, 4, false, false);
			
			public Archos(){
				super();
				this.setName(name);
				this.setAttackDamage(8.0);
				this.setMaxHealth(50.0);
				this.setMovementSpeed(0.6);
				//this.knockbackResistance(knockback);
				this.setFollowRange(30.0);
				this.setSpawnChance(0.05f);
				//this.setSpawnChance(0.01f);
				this.setDropChance(0.01f);
				this.setDropExperienceAmount(1000);
			}
			
			public void transform(EntityMob mob) {
				super.transform(mob);
				mob.addPotionEffect(archosRegen);				
			}
		}
		
		public static class Deimos extends EntityTransformer{
			public static final String name = "deimos";
	
			public Deimos() {
				super();
				this.setName(name);
				//this.setAttackDamage(3.0);
				this.setMaxHealth(150);
				//this.setMovementSpeed(0.2);
				//this.knockbackResistance(knockback);
				this.setFollowRange(100.0);
				this.setSpawnChance(0.01f);
				this.setDropChance(0.25f);
				this.setDropExperienceAmount(500);
			}
	
			public void transform(EntitySkeleton mob, ArrayList<EntityLiving> skeles) {
				super.transform(mob);
				ItemStack bow = new ItemStack(Items.bow);
				bow.addEnchantment(Enchantment.power, 5);
				bow.addEnchantment(Enchantment.flame, 1);
				mob.setCurrentItemOrArmor(0, bow);			
				Hypnosis.brainWashDeimos((EntitySkeleton)mob, skeles);			
			}
	
			public void transform(EntityMob mob){
				super.transform(mob);
				ItemStack bow = new ItemStack(Items.bow);
				bow.addEnchantment(Enchantment.power, 5);
				bow.addEnchantment(Enchantment.flame, 1);
				mob.setCurrentItemOrArmor(0, bow);			
				Hypnosis.brainWashDeimos((EntitySkeleton)mob, null);
			}
		}
			
		public static class Toros extends EntityTransformer{
			public Toros() {
				super();
				this.setName("Toros");
				//this.setAttackDamage(3.0);
				//this.setMaxHealth(health);
				this.setMovementSpeed(0.3);
				this.setKnockbackResistance(0.5);
				this.setFollowRange(100.0);
				this.setSpawnChance(0.01f);
				this.setDropChance(0.15f);
				this.setDropExperienceAmount(500);	
			}
			
			public void transform(EntityMob mob){
				super.transform(mob);
				ItemStack sword = new ItemStack(Items.diamond_sword);
				ItemStack helm = new ItemStack(Items.diamond_helmet);
				ItemStack chest = new ItemStack(Items.diamond_chestplate);
				ItemStack legs = new ItemStack(Items.diamond_leggings);
				ItemStack boots = new ItemStack(Items.diamond_boots);
				ItemSword item = new ItemSword(ToolMaterial.EMERALD);
				sword.addEnchantment(Enchantment.sharpness, 5);
				sword.addEnchantment(Enchantment.fireAspect, 2);
				sword.addEnchantment(Enchantment.knockback, 2);
				helm.addEnchantment(Enchantment.protection, 4);
				helm.addEnchantment(Enchantment.thorns, 2);
				chest.addEnchantment(Enchantment.fireProtection, 4);
				chest.addEnchantment(Enchantment.thorns, 2);
				legs.addEnchantment(Enchantment.projectileProtection, 4);
				legs.addEnchantment(Enchantment.thorns, 2);
				boots.addEnchantment(Enchantment.blastProtection, 4);
				boots.addEnchantment(Enchantment.thorns, 2);
				mob.setCurrentItemOrArmor(0, sword);
				mob.setCurrentItemOrArmor(3, helm);
				mob.setCurrentItemOrArmor(2, chest);
				mob.setCurrentItemOrArmor(1, legs);
				mob.setCurrentItemOrArmor(4, boots);	
				
		        List curTasks = mob.tasks.taskEntries;
		        int ctSize = curTasks.size();
		        mob.tasks.removeTask(((EntityAITaskEntry) curTasks.get(ctSize - 1)).action);
		        mob.tasks.removeTask(((EntityAITaskEntry) curTasks.get(ctSize - 2)).action);
		        mob.tasks.removeTask(((EntityAITaskEntry) curTasks.get(ctSize - 3)).action);
		        List curTargetTasks = mob.targetTasks.taskEntries;
		        int cttSize = curTargetTasks.size();
		        mob.targetTasks.removeTask(((EntityAITaskEntry) curTargetTasks.get(cttSize - 1)).action);
		        mob.targetTasks.removeTask(((EntityAITaskEntry) curTargetTasks.get(cttSize - 2)).action);
			}
		}
		
		public class QuickSpider extends EntityTransformer{
			private AttributeModifier spiderSpeed1 = new AttributeModifier("generic.movementSpeed", .03125, 0);
			private AttributeModifier spiderSpeed2 = new AttributeModifier("generic.movementSpeed", .0625, 0);
			private AttributeModifier spiderSpeed3 = new AttributeModifier("generic.movementSpeed", .09375, 0);
			private AttributeModifier spiderSpeed4 = new AttributeModifier("generic.movementSpeed", .125, 0);
			
			public QuickSpider() {
				super();
				//this.setName("QuickSpider");
				//this.setAttackDamage(3.0);
				//this.setMaxHealth(health);
				this.setMovementSpeed(0.3);
				//this.knockbackResistance(knockback);
				this.setFollowRange(30.0);
				//this.setSpawnChance(1f);
				//this.setDropChance(1.0f);
				//this.setDropExperienceAmount();		
			}
			
			public void transform(EntityMob mob){
				super.transform(mob);
				switch(Math.round(getMoonPhase()*4)){
				case 1:	EntityAttributeModifier.modifyMovementSpeed(mob, spiderSpeed1);
					break;
				case 2: EntityAttributeModifier.modifyMovementSpeed(mob, spiderSpeed2);
					break;
				case 3: EntityAttributeModifier.modifyMovementSpeed(mob, spiderSpeed3);
					break;
				case 4: EntityAttributeModifier.modifyMovementSpeed(mob, spiderSpeed4);
					break;
				default:
					break;
				}
			}
			
		}
		
		public static class Meeper extends EntityTransformer{
			public static final String name = "Meeper";
			public Meeper() {
				super();
				this.setName(name);
				this.setShowTag(false);
				//this.setAttackDamage(3.0);
				//this.setMaxHealth(health);
				//this.setMovementSpeed(0.2);
				//this.knockbackResistance(knockback);
				this.setFollowRange(20.0);
				this.setSpawnChance(0.25f);
				//this.setDropChance(1.0f);
				//this.setDropExperienceAmount(10);
			}
			
			@Override
			public boolean rollSpawnChance(){
				float rand = (float)Math.random();
				return rand < getMoonPhase()*20;
			}
			
			public void transform(EntityMob mob){
				AIMeeperAttack aiMeep = new AIMeeperAttack(mob);
				mob.tasks.addTask(3, aiMeep);
			}
			
			public class AIMeeperAttack extends EntityAIBase{
		        private EntityMob meeper;
		        public int field_179471_a;
		        private static final String __OBFID = "CL_00002215";
		        
		        public AIMeeperAttack(EntityMob creeper){
		        	meeper = creeper;
		        }
	
		        /**
		         * Returns whether the EntityAIBase should begin execution.
		         */
		        public boolean shouldExecute(){
		            return this.meeper.getAttackTarget() != null;
		        }
	
		        /**
		         * Execute a one shot task or start executing a continuous task
		         */
		        public void startExecuting(){
		            this.field_179471_a = 0;
		        }
	
		        /**
		         * Resets the task
		         */
		        public void resetTask(){
		        	meeper.setAttackTarget(null);
		        }
	
		        /**
		         * Updates the task
		         */
		        public void updateTask(){
		            EntityLivingBase entitylivingbase = this.meeper.getAttackTarget();
		            double d0 = 64.0D;
	
		            if (entitylivingbase.getDistanceSqToEntity(this.meeper) < d0 * d0 && this.meeper.canEntityBeSeen(entitylivingbase)){
		                World world = this.meeper.worldObj;
		                ++this.field_179471_a;
	
		                if (this.field_179471_a == 10){
		                    world.playAuxSFXAtEntity((EntityPlayer)null, 1007, new BlockPos(this.meeper), 0);
		                }
	
		                if (this.field_179471_a == 20){
		                    double d1 = 1.25D;
		                    Vec3 vec3 = this.meeper.getLook(1.0F);
		                    double d2 = entitylivingbase.posX - (this.meeper.posX + vec3.xCoord * d1);
		                    double d3 = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 2.0F) - (0.5D + this.meeper.posY + (double)(this.meeper.height / 2.0F));
		                    double d4 = entitylivingbase.posZ - (this.meeper.posZ + vec3.zCoord * d1);
		                    EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this.meeper, d2, d3, d4);
	
		                    world.playAuxSFXAtEntity((EntityPlayer)null, 1008, new BlockPos(this.meeper), 0);
		                    entitylargefireball.explosionPower = Math.round(getMoonPhase()*4);
		                    entitylargefireball.posX = this.meeper.posX + vec3.xCoord * d1;
		                    entitylargefireball.posY = this.meeper.posY + (double)(this.meeper.height / 2.0F) + 0.5D;
		                    entitylargefireball.posZ = this.meeper.posZ + vec3.zCoord * d1;
		                    world.spawnEntityInWorld(entitylargefireball);
		                    this.field_179471_a = -40;
		                }
		            }
		            else if (this.field_179471_a > 0){
		                --this.field_179471_a;
		            }
		        }
		    }
		}
		
		public static class Hypnosis{
			
			public static void brainWash(EntityLiving entity){
				for(int i = 0; i < entity.tasks.taskEntries.size(); i++){
					EntityAIBase task = ((EntityAITaskEntry)entity.tasks.taskEntries.get(i)).action;
					entity.tasks.removeTask(task);
				}
				for(int i = 0; i < entity.targetTasks.taskEntries.size(); i++){
					EntityAIBase task = ((EntityAITaskEntry)entity.targetTasks.taskEntries.get(i)).action;
					entity.targetTasks.removeTask(task);
				}
			}
	
			/*
			 * Remove AI at the given indices from an entity.
			 * */
			public static void brainWash(EntityLiving entity, ArrayList<Integer> taskIndices, ArrayList<Integer> targetIndices){
				
				java.util.Collections.sort(taskIndices);
				java.util.Collections.sort(targetIndices);
				boolean error = false;
				
				if(entity.tasks.taskEntries.size() < taskIndices.size()){
					System.out.println("Warning, more tasks were selected then are available.");
					for(Integer i: taskIndices) System.out.println("Task:" + i);
					error = true;
				}
				
				if(entity.targetTasks.taskEntries.size() < targetIndices.size()){
					System.out.println("Warning, more target tasks were selected then are available.");
					for(Integer i: targetIndices) System.out.println("Target task:" + i);
					error = true;
				}
				
				if(error) return;
				
		        List curTasks = entity.tasks.taskEntries;
		        List curTargetTasks = entity.targetTasks.taskEntries;
	
		        for(int i = taskIndices.size() - 1; i >= 0; i--){
		        	int index = taskIndices.get(i);
		        	EntityAIBase task = ((EntityAITaskEntry)curTasks.get(index)).action;
		        	entity.tasks.removeTask(task);
		        	//System.out.println("Removed " + task.getClass().getName());
		        }
	
		        for(int i = targetIndices.size() - 1; i >= 0; i--){
		        	int index = targetIndices.get(i);
		        	EntityAIBase task = ((EntityAITaskEntry)curTargetTasks.get(index)).action;
		        	entity.targetTasks.removeTask(task);
		        	//System.out.println("Removed " + task.getClass().getName());
		        }
			}
			
			public static void brainWashSpider(EntitySpider spider){
		        ArrayList<Integer> taskIndices = new ArrayList<Integer>(6);
		        ArrayList<Integer> targetIndices = new ArrayList<Integer>(3);
	
		        java.util.Collections.addAll(taskIndices, 1,3,4,5,6,7);
		        java.util.Collections.addAll(targetIndices, 0,1,2);
		        
		        Hypnosis.brainWash(spider, taskIndices, targetIndices);
				spider.tasks.addTask(1, new EntityAISpiderAttack(spider, EntityLivingBase.class, 2f, true));
			}
			
			
			public static void brainWashPig(EntityPig pig) {
		        ArrayList<Integer> taskIndices = new ArrayList<Integer>();
	
		        java.util.Collections.addAll(taskIndices, 1,2,3,4,5,6,7,8,9);
		        Hypnosis.brainWash(pig, taskIndices, new ArrayList<Integer>(0));
		        pig.tasks.addTask(1, new PigAttack(pig, EntityLivingBase.class,1.5f, true));
			}
			
			public static void brainWashDeimos(EntitySkeleton skeleton, ArrayList<EntityLiving> allies){
		        ArrayList<Integer> taskIndices = new ArrayList<Integer>(6);
		        ArrayList<Integer> targetIndices = new ArrayList<Integer>(3);
	
		        java.util.Collections.addAll(taskIndices, 4, 5, 8);
		        java.util.Collections.addAll(targetIndices, 0,1,2);
		        
		        Hypnosis.brainWash(skeleton, taskIndices, targetIndices);
		        /* Only becomes aggressive at players. */
		        skeleton.targetTasks.addTask(1, new EntityAIHurtByPlayer(skeleton, false, null));
		        /* Only target players, and notify other Deimos. */
		        skeleton.targetTasks.addTask(2, new DemiosAITarget(skeleton, EntityPlayer.class, true, allies));
		        /* Shoot rapidly with a larger range and perfect accuracy. */
				skeleton.tasks.addTask(4, new DeimosAIArrowAttack((IRangedAttackMob) skeleton, 1.0D, 10, 10, 30.0F));
			}
			
			public static class DeimosAIArrowAttack extends EntityAIBase
			{
			    /** The entity the AI instance has been applied to */
			    private final EntityLiving entityHost;
			    /** The entity (as a RangedAttackMob) the AI instance has been applied to. */
			    private final IRangedAttackMob rangedAttackEntityHost;
			    private EntityLivingBase attackTarget;
			    /**
			     * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
			     * maxRangedAttackTime.
			     */
			    private int rangedAttackTime;
			    private double entityMoveSpeed;
			    private int field_75318_f;
			    private int field_96561_g;
			    /** The maximum time the AI has to wait before peforming another ranged attack. */
			    private int maxRangedAttackTime;
			    private float field_96562_i;
			    private float maxAttackDistance;
			    private static final String __OBFID = "CL_00001609";

			    public DeimosAIArrowAttack(IRangedAttackMob p_i1649_1_, double p_i1649_2_, int p_i1649_4_, float p_i1649_5_)
			    {
			        this(p_i1649_1_, p_i1649_2_, p_i1649_4_, p_i1649_4_, p_i1649_5_);
			    }

			    public DeimosAIArrowAttack(IRangedAttackMob p_i1650_1_, double p_i1650_2_, int p_i1650_4_, int p_i1650_5_, float p_i1650_6_)
			    {
			        this.rangedAttackTime = -1;

			        if (!(p_i1650_1_ instanceof EntityLivingBase))
			        {
			            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
			        }
			        else
			        {
			            this.rangedAttackEntityHost = p_i1650_1_;
			            this.entityHost = (EntityLiving)p_i1650_1_;
			            this.entityMoveSpeed = p_i1650_2_;
			            this.field_96561_g = p_i1650_4_;
			            this.maxRangedAttackTime = p_i1650_5_;
			            this.field_96562_i = p_i1650_6_;
			            this.maxAttackDistance = p_i1650_6_ * p_i1650_6_;
			            this.setMutexBits(3);
			        }
			    }

			    /**
			     * Returns whether the EntityAIBase should begin execution.
			     */
			    public boolean shouldExecute()
			    {
			        EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();

			        if (entitylivingbase == null)
			        {
			            return false;
			        }
			        else
			        {
			            this.attackTarget = entitylivingbase;
			            return true;
			        }
			    }

			    /**
			     * Returns whether an in-progress EntityAIBase should continue executing
			     */
			    public boolean continueExecuting()
			    {
			        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
			    }

			    /**
			     * Resets the task
			     */
			    public void resetTask()
			    {
			        this.attackTarget = null;
			        this.field_75318_f = 0;
			        this.rangedAttackTime = -1;
			    }

			    /**
			     * Updates the task
			     */
			    public void updateTask()
			    {
			        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
			        boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);

			        if (flag)
			        {
			            ++this.field_75318_f;
			        }
			        else
			        {
			            this.field_75318_f = 0;
			        }
			        			     
			        boolean clearshot = false;
			        EntityLivingBase entity = MouseOverHelper.getMouseOverEntityLiving(this.entityHost, this.maxAttackDistance);
			        
			        if(entity instanceof EntityPlayer){
			        	clearshot = true;
			        }
			        
			        if (d0 <= (double)this.maxAttackDistance && this.field_75318_f >= 20 && clearshot)
			        {
			            this.entityHost.getNavigator().clearPathEntity();
			        }
			        else
			        {
			            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
			        }

			        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
			        float f;

			        if (--this.rangedAttackTime == 0)
			        {
			            if (d0 > (double)this.maxAttackDistance || !flag || !clearshot)
			            {
			                return;
			            }

			            f = MathHelper.sqrt_double(d0) / this.field_96562_i;
			            float f1 = MathHelper.clamp_float(f, 0.1F, 1.0F);
			            demiosRangedAttack(this.attackTarget, f1);
			            this.rangedAttackTime = MathHelper.floor_float(f * (float)(this.maxRangedAttackTime - this.field_96561_g) + (float)this.field_96561_g);
			        }
			        else if (this.rangedAttackTime < 0)
			        {
			            f = MathHelper.sqrt_double(d0) / this.field_96562_i;
			            this.rangedAttackTime = MathHelper.floor_float(f * (float)(this.maxRangedAttackTime - this.field_96561_g) + (float)this.field_96561_g);
			        }
			    }
			    
			    public void demiosRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_){
			    	EntitySkeleton deimos = (EntitySkeleton) this.entityHost;
			    	World world = deimos.worldObj;
			        EntityArrow entityarrow = new EntityArrow(world);
			        
			        entityarrow.renderDistanceWeight = 10.0D;
			        entityarrow.shootingEntity = deimos;

			        entityarrow.posY = deimos.posY + (double)deimos.getEyeHeight() - 0.10000000149011612D;
			        double d0 = p_82196_1_.posX - deimos.posX;
			        double d1 = p_82196_1_.getEntityBoundingBox().minY + (double)(p_82196_1_.height / 3.0F) - entityarrow.posY;
			        double d2 = p_82196_1_.posZ - deimos.posZ;
			        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);

			        if (d3 >= 1.0E-7D)
			        {
			            float f2 = (float)(Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
			            float f3 = (float)(-(Math.atan2(d1, d3) * 180.0D / Math.PI));
			            double d4 = d0 / d3;
			            double d5 = d2 / d3;
			            entityarrow.setLocationAndAngles(deimos.posX + d4, entityarrow.posY, deimos.posZ + d5, f2, f3);
			            float f4 = (float)(d3 * 0.25D);
			            entityarrow.setThrowableHeading(d0, d1 + (double)f4, d2, 1.6f, 0);
			        }
			        
			        int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, deimos.getHeldItem());
			        entityarrow.setDamage((double)(p_82196_2_ * 2.0F) + (new Random()).nextGaussian()  * 0.25D + (double)((float)world.getDifficulty().getDifficultyId() * 0.11F));

			        if (i > 0)
			        {
			            entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
			        }

			        deimos.playSound("random.bow", 1.0F, 1.0F / (deimos.getRNG().nextFloat() * 0.4F + 0.8F));
			        world.spawnEntityInWorld(entityarrow);
			    }
			}
			public static class DemiosAITarget extends EntityAINearestAttackableTarget{
				private ArrayList<EntityLiving> allies;
				
				public DemiosAITarget(EntityCreature p_i45878_1_, Class p_i45878_2_, boolean p_i45878_3_) {
					super(p_i45878_1_, p_i45878_2_, p_i45878_3_);
					allies = new ArrayList<EntityLiving>(0);
				}
				
				public DemiosAITarget(EntityCreature p_i45878_1_, Class p_i45878_2_, boolean p_i45878_3_, ArrayList<EntityLiving> d) {
					super(p_i45878_1_, p_i45878_2_, p_i45878_3_);
					allies = d;
				}
				
				@Override
				public boolean shouldExecute(){
		            double d0 = this.getTargetDistance();
		            List list = this.taskOwner.worldObj.getEntitiesWithinAABB(this.targetClass, this.taskOwner.getEntityBoundingBox().expand(d0, 4.0D, d0), Predicates.and(this.targetEntitySelector, IEntitySelector.NOT_SPECTATING));
		            Collections.sort(list, this.theNearestAttackableTargetSorter);
	
		            if (list.isEmpty()){
		                for(EntityLiving mob: allies){
		                	EntityLivingBase t = mob.getAttackTarget();
		                	if(t != null){
		                		this.targetEntity = t;
		                		return true;
		                	}
		                }
		                return false;
		            }
		            else{
		                this.targetEntity = (EntityLivingBase)list.get(0);
		                for(EntityLiving mob: allies){
		                	mob.setAttackTarget(this.targetEntity);
		                }
		                return true;
		            }
				}
			}
			
			public static class EntityAIHurtByPlayer extends EntityAIHurtByTarget{

				public EntityAIHurtByPlayer(EntityCreature p_i45885_1_, boolean p_i45885_2_, Class[] p_i45885_3_) {
					super(p_i45885_1_, p_i45885_2_, p_i45885_3_);
				}
				
				@Override
				public boolean shouldExecute(){
					return super.shouldExecute() && (this.taskOwner.getAITarget() instanceof EntityPlayer);
				}
			}
			
			public static class EntityAISpiderAttack extends EntityAIAttackOnCollide{
				public EntityAISpiderAttack(EntityCreature p_i1635_1_, Class p_i1635_2_, double p_i1635_3_,
						boolean p_i1635_5_) {
					super(p_i1635_1_, p_i1635_2_, p_i1635_3_, p_i1635_5_);
				}
				
				@Override
				public void updateTask(){
					super.updateTask();
					if(!super.continueExecuting())
						this.attacker.setDead();
				}
				
				@Override
				public void resetTask(){
					//super.resetTask();
					this.attacker.setDead();
				}
			}
			
			public static class PigAttack extends EntityAIBase{
			    World worldObj;
			    protected EntityCreature attacker;
			    /** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
			    int attackTick;
			    /** The speed with which the mob will approach the target */
			    double speedTowardsTarget;
			    /** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
			    boolean longMemory;
			    /** The PathEntity of our entity. */
			    PathEntity entityPathEntity;
			    Class classTarget;
			    private int field_75445_i;
			    private double field_151497_i;
			    private double field_151495_j;
			    private double field_151496_k;
			    private static final String __OBFID = "CL_00001595";
			    private int failedPathFindingPenalty = 0;
			    private boolean canPenalize = false;
	
			    public PigAttack(EntityCreature p_i1635_1_, Class p_i1635_2_, double p_i1635_3_, boolean p_i1635_5_){
			        this(p_i1635_1_, p_i1635_3_, p_i1635_5_);
			        this.classTarget = p_i1635_2_;
			        canPenalize = classTarget == null || !net.minecraft.entity.player.EntityPlayer.class.isAssignableFrom(classTarget); //Only enable delaying when not targeting players.
			    }
	
			    public PigAttack(EntityCreature p_i1636_1_, double p_i1636_2_, boolean p_i1636_4_){
			        this.attacker = p_i1636_1_;
			        this.worldObj = p_i1636_1_.worldObj;
			        this.speedTowardsTarget = p_i1636_2_;
			        this.longMemory = p_i1636_4_;
			        this.setMutexBits(3);
			    }
	
			    /**
			     * Returns whether the EntityAIBase should begin execution.
			     */
			    public boolean shouldExecute(){
			        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
	
			        if (entitylivingbase == null){
			            return false;
			        }else if (!entitylivingbase.isEntityAlive()){
			            return false;
			        }else if (this.classTarget != null && !this.classTarget.isAssignableFrom(entitylivingbase.getClass())){
			            return false;
			        }else{
			            if (canPenalize){
			                if (--this.field_75445_i <= 0){
			                    this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
			                    this.field_151497_i = 4 + this.attacker.getRNG().nextInt(7);
			                    return this.entityPathEntity != null;
			                }else{
			                    return true;
			                }
			            }
			            this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
			            return this.entityPathEntity != null;
			        }
			    }
	
			    /**
			     * Returns whether an in-progress EntityAIBase should continue executing
			     */
			    public boolean continueExecuting(){
			        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
			        if( entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : (!this.longMemory ? !this.attacker.getNavigator().noPath() : this.attacker.func_180485_d(new BlockPos(entitylivingbase)))))
			        	return true;
			        else{
						this.attacker.setDead();
						return false;
			        }
			        	
			    }
	
			    /**
			     * Execute a one shot task or start executing a continuous task
			     */
			    public void startExecuting(){
			        this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
			        this.field_75445_i = 0;
			    }
	
			    /**
			     * Resets the task
			     */
			    public void resetTask(){
			        this.attacker.getNavigator().clearPathEntity();
					this.attacker.setDead();
			    }
	
			    /**
			     * Updates the task
			     */
			    public void updateTask(){
			        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
			        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
			        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
			        double d1 = this.func_179512_a(entitylivingbase);
			        --this.field_75445_i;
	
			        if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.field_75445_i <= 0 && (this.field_151497_i == 0.0D && this.field_151495_j == 0.0D && this.field_151496_k == 0.0D || entitylivingbase.getDistanceSq(this.field_151497_i, this.field_151495_j, this.field_151496_k) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F)){
			            this.field_151497_i = entitylivingbase.posX;
			            this.field_151495_j = entitylivingbase.getEntityBoundingBox().minY;
			            this.field_151496_k = entitylivingbase.posZ;
			            this.field_75445_i = 4 + this.attacker.getRNG().nextInt(7);
	
			            if (this.canPenalize){
			                this.field_151497_i += failedPathFindingPenalty;
			                if (this.attacker.getNavigator().getPath() != null){
			                    net.minecraft.pathfinding.PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
			                    if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1)
			                        failedPathFindingPenalty = 0;
			                    else
			                        failedPathFindingPenalty += 10;
			                }else{
			                    failedPathFindingPenalty += 10;
			                }
			            }
	
			            if (d0 > 1024.0D){
			                this.field_75445_i += 10;
			            }else if (d0 > 256.0D){
			                this.field_75445_i += 5;
			            }
	
			            if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget)){
			                this.field_75445_i += 15;
			            }
			        }
	
			        this.attackTick = Math.max(this.attackTick - 1, 0);
	
			        if (d0 <= d1 && this.attackTick <= 0){
			            this.attackTick = 20;
	
			            if (this.attacker.getHeldItem() != null){
			                this.attacker.swingItem();
			            }
			            this.attackEntityAsPiggy(entitylivingbase);
			        }
			    }
	
			    protected double func_179512_a(EntityLivingBase p_179512_1_){
			        return (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F + p_179512_1_.width);
			    }
				
			    public boolean attackEntityAsPiggy(Entity p_70652_1_){
			        float f = 5.0f;
			        int i = 0;
			        boolean flag = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this.attacker), f);
	
			        if (flag){
			            p_70652_1_.setFire(4);
			        }
			        return flag;
			    }
			}
		}
	}
}