package com.super_deathagon.phobos;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.super_deathagon.util.MouseOverHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid 	 = 	Phobos.MODID,
	name 	 = 	Phobos.MODNAME,
	 version = 	Phobos.MODVERSION)
@SideOnly(Side.CLIENT)
public class Phobos {
	public static final String MODID = "phobos";
	public static final String MODNAME = "Phobos";
	public static final String MODVERSION = "0.9.0";
	private static KeyBinding[] abilityKeys;
	private PhobosGui gui;
	
	@EventHandler
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event){
		System.out.println("Phobos is preparing for initialization...");
		System.out.println("Pre-initialization complete.");
	} 
	
	@EventHandler
	public void fmlLifeCycleEvent(FMLInitializationEvent event){
		System.out.println("Phobos is initializing...");
		gui = new PhobosGui();
		MinecraftForge.EVENT_BUS.register(gui);
		FMLCommonHandler.instance().bus().register(new PhobosFMLEventHandler());
		abilityKeys = new KeyBinding[4];
		abilityKeys[0] = new KeyBinding("Find ores", Keyboard.KEY_R, "key.ability.catagory");
		abilityKeys[1] = new KeyBinding("Move fast", Keyboard.KEY_F, "key.ability.catagory");
		abilityKeys[2] = new KeyBinding("Toggle Name Tags", Keyboard.KEY_C, "key.ability.catagory");
		abilityKeys[3] = new KeyBinding("Toggle Radar", Keyboard.KEY_V, "key.ability.catagory");
		for(KeyBinding kb : abilityKeys){
			ClientRegistry.registerKeyBinding(kb);
		}
		System.out.println("Initialization complete.");
	}

	@EventHandler
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event){
		System.out.println("Phobos is preparing post initializations...");
		System.out.println("Post-initialization complete.");
	}

	public class PhobosFMLEventHandler{
		//Modifier for sprinting faster than ususal.
		private AttributeModifier sprintMod = new AttributeModifier("generic.movementSpeed", 4.0, 2);
		private PotionEffect jump = new PotionEffect(Potion.jump.id, 2, 2);
		private boolean superspeed = false;
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(KeyInputEvent event){
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			if(abilityKeys[0].isKeyDown()){
				BlockPos bp = MouseOverHelper.getMouseOverDiamond(player, 100, Blocks.gold_ore);
				if(bp != null){
					System.out.println(bp.getX() + "," + bp.getY() + "," + bp.getZ());
				}else{
					System.out.println("No diamond there!");
				}
			}
			
			if(abilityKeys[1].isKeyDown()){
				IAttributeInstance speedAttribute = 
						player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
				
				if(!superspeed){
					speedAttribute.applyModifier(sprintMod);
					player.addPotionEffect(jump);
					player.stepHeight = 2;
					//player.jumpMovementFactor = 100f;//not sure if this does anything
					superspeed = true;
				}else{
					speedAttribute.removeModifier(sprintMod);
					player.removePotionEffect(Potion.jump.id);
					player.stepHeight = 0.6f;
					//player.jumpMovementFactor = 0.02f;
					superspeed = false;
				}
			}
			
			if(abilityKeys[2].isKeyDown()) gui.toggleNameTags();
			/* Auto clicker for skele farming. */
			//if(abilityKeys[2].isKeyDown()){
			//	/* toggle skele farming*/
			//	skelefarm = !skelefarm;
			//	
			//	if(skelefarm){
			//		(new Thread(){
			//			public void run(){
			//				try{
			//					Robot robot = new Robot();
			//					while(skelefarm){
			//						robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			//						Thread.sleep(50);
			//						robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			//						Thread.sleep(500);
			//					}
			//				}catch (AWTException e) {
			//					// TODO Auto-generated catch block
			//					e.printStackTrace();
			//				} catch (InterruptedException e) {
			//					// TODO Auto-generated catch block
			//					e.printStackTrace();
			//				}
			//			}
			//		}).start();
			//	}
			//}
			
			if(abilityKeys[3].isKeyDown()) gui.toggleRadar();
		}

		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(PlayerTickEvent event){
			EntityPlayer player = event.player;
			IAttributeInstance speedAttribute = 
					player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
			if(superspeed && !speedAttribute.func_180374_a(sprintMod)){
				speedAttribute.applyModifier(sprintMod);
			}
		}
	}

	public static class PhobosGui extends GuiIngame{

		/**
		 * # # ### ### # # # # 
		 * ### # # # # ##   #   
		 * # # ### ### # # # #  
		 */
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(RenderGameOverlayEvent.Post event){
	        if (event.type.equals(RenderGameOverlayEvent.ElementType.EXPERIENCE)){
	        	this.renderHealthBar(event);
	        	this.renderCrosshairs(event);
				this.playerRadar(event.partialTicks);
				this.entityRadar(event.partialTicks);
	        } 
		}
		
		//@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		//public void onEvent(RenderGameOverlayEvent.Pre event){}
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onEvent(RenderLivingEvent.Specials.Pre event){
			event.setCanceled(true);
			this.betterNameTags(event.entity);
	    }
		
		/* *****************
		 *     PhobosGui    
		 * *****************/
		private ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
		private ResourceLocation mapIcons = new ResourceLocation("textures/map/map_icons.png");
		private static int toggleNameTags = 0;
		private static int radarVerbose = 0;

		public PhobosGui(){
			super(Minecraft.getMinecraft());
		}
		
		public static void toggleNameTags(){
			toggleNameTags = (toggleNameTags + 1)%4;
		}
		
		public static void toggleRadar(){
			radarVerbose = (radarVerbose + 1)%3;
		}
		
		public void renderHealthBar(RenderGameOverlayEvent.Post event){
			EntityPlayer player = this.mc.thePlayer;
			float health = player.getHealth();
			float maxHealth = player.getMaxHealth();
			int xPos = event.resolution.getScaledWidth() / 2 - 55;
			int yPos = event.resolution.getScaledHeight() - 30;
			
			this.mc.getTextureManager().bindTexture(icons);
			this.func_175179_f().drawStringWithShadow(player.getHealth() + "/" + player.getMaxHealth(), xPos, yPos, 16711680);
			
		}

		public void renderCrosshairs(RenderGameOverlayEvent.Post event) {
			double visionDistance = 100;
			EntityPlayer player = this.mc.thePlayer;
			EntityLivingBase e = MouseOverHelper.getMouseOverEntityLiving(player, visionDistance);
			if(e != null){
				this.func_175179_f().drawString(e.getHealth() + "/" + e.getMaxHealth(), 
						event.resolution.getScaledWidth() / 2 + 3, event.resolution.getScaledHeight() / 2 + 3, 16711680);
				this.mc.getTextureManager().bindTexture(icons);
	            GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR, 1, 0);
	            GlStateManager.enableAlpha();
	            drawTexturedModalRect(event.resolution.getScaledWidth() / 2 - 7, event.resolution.getScaledHeight() / 2 - 7, 0, 0, 16, 16);
	            GlStateManager.tryBlendFuncSeparate(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA, 1, 0);
			}
		}
		
		public void betterNameTags(EntityLivingBase target){
			Class[] list = null;
			
			switch(toggleNameTags){
			/* Everything that extends EntityLivingBase except armor stands. */
			case 0: list = new Class[]{EntityPlayer.class, EntityLiving.class};
				break;
			/* Everything that I consider aggressive. */
			case 1: list = new Class[]{EntityPlayer.class, EntityMob.class, EntityFlying.class, 
									   EntitySlime.class, EntityDragon.class};
				break;
			/* Players, animals and villagers. */
			case 2: list =  new Class[]{EntityPlayer.class, EntityAgeable.class};
				break;
			/* Just players. */
			case 3: list = new Class[]{EntityPlayer.class};
				break;
			default:
				return;
			}
			
			for(Class clazz: list){
				if(clazz.isInstance(target)){
					renderNameTagOriginal(target);
					break;
				}
			}
		}
		
		public void renderNameTagOriginal(EntityLivingBase target){
			double x = target.posX -this.mc.thePlayer.posX;
			double y = target.posY - this.mc.thePlayer.posY;
			double z = target.posZ -this.mc.thePlayer.posZ;
			String s = target.getDisplayName().getFormattedText();
   		 	double d = target.getDistanceToEntity(this.mc.thePlayer);

			if(d > 10){
				x = x/d * 10.0;
             	y = y/d * 10.0;
             	z = z/d * 10.0;
			}
			RenderManager rm = this.mc.getRenderManager();
            FontRenderer fontrenderer = rm.getFontRenderer();
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x + 0.0F, (float)y + target.height + 0.5F, (float)z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(rm.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f1, -f1, f1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            byte b0 = 0;

            GlStateManager.disableTexture2D();
            worldrenderer.startDrawingQuads();
            int j = fontrenderer.getStringWidth(s) / 2;
            worldrenderer.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
            worldrenderer.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
            worldrenderer.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
            worldrenderer.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
            worldrenderer.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
            tessellator.draw();
            GlStateManager.enableTexture2D();
            fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, b0, 553648127);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, b0, -1);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
		}
		
		public void playerRadar(float partialTicks){
			 NetHandlerPlayClient nethandlerplayclient = this.mc.thePlayer.sendQueue;
		     ArrayList<NetworkPlayerInfo> list = new ArrayList<NetworkPlayerInfo>(nethandlerplayclient.func_175106_d());
			 DecimalFormat df = new DecimalFormat("######.#");
    		 RenderManager rm = this.mc.getRenderManager();
    		 FontRenderer fontrenderer = rm.getFontRenderer();
	    	 int playerCount = 0;
	    	 int screenX = 0;
	    	 int screenY = 0;
	    	 //offset to the right
	    	 int xOffset = 2;
	    	 //space between lines
	    	 int yOffset = 10;
	    	 
	    	 /* Sometimes this is null when the event is triggered. */
	    	 if(rm.options == null){
	    		 return;
	    	 }
	    	 /* If the GUI scale is set to small... */
	    	 else if(rm.options.guiScale == 1){
	    		 screenX = this.mc.displayWidth;
	    		 screenY = this.mc.displayHeight;
	    	 }
	    	 /* If GUI scale is set to normal... */
	    	 else if (rm.options.guiScale == 2){
	    		 screenX = this.mc.displayWidth/2;
	    		 screenY = this.mc.displayHeight/2;
	    	 }
	    	 
	    	 for(NetworkPlayerInfo npi: list){
	    		 String name = npi.getGameProfile().getName();
	    		 this.mc.getTextureManager().bindTexture(npi.getLocationSkin());
		    	 EntityPlayer entity = this.mc.theWorld.getPlayerEntityByName(name);

		    	 if(entity != null && !name.equals(this.mc.thePlayer.getName())){
		    		 playerCount++;
		    		 /* x and y values of where the string will be printed onto the screen. */
		    		 int x = xOffset;
		    		 int y = playerCount * yOffset;
		    		 
		    		 /* This is how minecraft calculates an entities position. */
		    		 double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
		    		 double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
		    		 double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
		    		 
		    		 /* This is how I calculate an entities position. This is me. */
		    		 double relX = d0 - this.mc.thePlayer.posX;
		    		 double relY = d1 - this.mc.thePlayer.posY;
		    		 double relZ = d2 - this.mc.thePlayer.posZ;
		    		 double d = entity.getDistanceToEntity(this.mc.thePlayer);
		    		 
		    		 /* The string we will render over the entity. */
		    		 String s = entity.getDisplayName().getFormattedText();
		    		 if(radarVerbose == 1) 
		    			 s = s + ":" + df.format(d);
		    		 else if(radarVerbose == 2) 
		    			 s = s + ":" + df.format(d) + " " 
		    				   + df.format(d0) + "," + df.format(d1) + "," + df.format(d2);

		    		 /* positive z is 0 degrees increasing counter-clockwise */
		    		 /* My absolute x and y rotations in degrees. */
		    		 float myRotY = rm.playerViewY%360;
		    		 if(myRotY < 0) myRotY = 360 + myRotY;
		    		 float myRotX = rm.playerViewX;
		    		 /* Relative degree of the entity from my position. */
		    		 float relDeg = (float)(Math.atan(relX/relZ) * 180/Math.PI);
		    		 /* The degree of the entity relative to my rotation. */
		    		 float finalDeg = 0;
		    		 
		    		 /* Since relDeg will only be between -90 and 90 we must use
		    		  * some other information (entities coordinates)
		    		  * to know what degree the entity is really located at. 
		    		  * Positive z is our 0 degree, increasing counter clockwise. */
		    		 if(relZ > 0 && relX > 0){
		    			 //quadrant I
		    			 finalDeg = 360 - relDeg;
		    			 //System.out.println("Quadrant I");
		    		 }else if(relZ < 0 && relX > 0){
		    			 //quadrant II
		    			 finalDeg = 180 - relDeg;
		    			 //System.out.println("Quadrant II");
		    		 }else if(relZ < 0 && relX < 0){
		    			 //quadrant III
		    			 finalDeg = 180 - relDeg;
		    			 //System.out.println("Quadrant III");
		    		 }else if(relZ > 0 && relX < 0){
		    			 //quadrant IV
		    			 finalDeg = -relDeg;
		    			 //System.out.println("Quadrant IV");
		    		 }else{
		    			 //System.out.println("Centered");
		    		 }
		    		 finalDeg = finalDeg - myRotY;
		    		 
		    		 if(finalDeg < 0) finalDeg = 360 + finalDeg;

		    		 //String s1 = "Relative location of entity from me: " + relX + " " + relZ;
		    		 //String s2 = "Degrees of entity relative to my location: " + relDeg;
		    		 //String s3 = "My rotation in x and y: " + myRotX + " " + myRotY;
		    		 //String s4 = "Degrees of entity relative to my rotation: " + finalDeg;
		    		 
		    		 if(finalDeg < 300 && finalDeg > 180)
		    			 s = "<<<" + s;
		    		 else if(finalDeg > 60 && finalDeg < 180)
		    			 s = s + ">>>";
		    		 
		    		 /* Width of the string on the screen. */
		    		 int stringWidth = fontrenderer.getStringWidth(s);
		    		 x = (int)((Math.sin(finalDeg*Math.PI/180.0) * screenX/2) + (screenX/2) - (stringWidth/2));

		    		 if(x < 0){
		    			 x = 0;
		    		 }else if(x > screenX - fontrenderer.getStringWidth(s)){
		    			 x = screenX - fontrenderer.getStringWidth(s);
		    		 }
		    		 
		    		 if(y < 0){
		    			 y = 0;
		    		 }else if(y > screenY - yOffset){
		    			 y = screenY - yOffset;
		    		 }

		    		 
		    		 fontrenderer.drawString( s, x, 		  y, -1);
	                 Gui.drawScaledCustomSizeModalRect(x, y, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
		    		 //fontrenderer.drawString(s1, 0, yOffset * 2, -1);
		    		 //fontrenderer.drawString(s2, 0, yOffset * 3, -1);
		    		 //fontrenderer.drawString(s3, 0, yOffset * 4, -1);
		    		 //fontrenderer.drawString(s4, 0, yOffset * 5, -1);
		    	}
		    }
		}
		
		public void entityRadar(float partialTicks){

			 DecimalFormat df = new DecimalFormat("######.#");
    		 RenderManager rm = this.mc.getRenderManager();
    		 FontRenderer fontrenderer = rm.getFontRenderer();
	    	 int line = 0;
	    	 int screenX = 0;
	    	 int screenY = 0;
	    	 //offset to the right
	    	 int xOffset = 2;
	    	 //space between lines
	    	 int yOffset = 10;
	    	 
	    	 /* Sometimes this is null wh1en the event is triggered. */
	    	 if(rm.options == null){
	    		 return;
	    	 }
	    	 /* If the GUI scale is set to small... */
	    	 else if(rm.options.guiScale == 1){
	    		 screenX = this.mc.displayWidth;
	    		 screenY = this.mc.displayHeight;
	    	 }
	    	 /* If GUI scale is set to normal... */
	    	 else if (rm.options.guiScale == 2){
	    		 screenX = this.mc.displayWidth/2;
	    		 screenY = this.mc.displayHeight/2;
	    	 }
	    	 EntityPlayer player = this.mc.thePlayer;
	    	 World world = this.mc.theWorld;
	    	 AxisAlignedBB area = player.getEntityBoundingBox().expand(20,20,20);
	    	 List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, area);
	    	 for(Entity entity: list){
	    		 if(entity instanceof EntityMob){
		    		 line++;
		    		 entityRadar(rm, fontrenderer, df, screenX, screenY, line, xOffset, yOffset, partialTicks, (EntityMob)entity);
	    		 }
	    	 }
		}
		
		public void entityRadar(RenderManager rm, FontRenderer fontrenderer, DecimalFormat df,
								int screenX, int screenY, int line, int xOffset, int yOffset, 
								float partialTicks, EntityMob entity){	    	 
    		 int x = xOffset;
    		 int y = line * yOffset;    		 
    		 double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
    		 double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
    		 double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;    		 
    		 double relX = d0 - this.mc.thePlayer.posX;
    		 double relY = d1 - this.mc.thePlayer.posY;
    		 double relZ = d2 - this.mc.thePlayer.posZ;
    		 double d = entity.getDistanceToEntity(this.mc.thePlayer);
    		 
    		 String s = entity.getDisplayName().getFormattedText();
    		 if(radarVerbose == 1) 		s = s + ":" + df.format(d);
    		 else if(radarVerbose == 2) s = s + ":" + df.format(d) 
    		 								  + " " + df.format(d0) 
    		 								  + "," + df.format(d1) 
    		 								  + "," + df.format(d2);

    		 float myRotY = rm.playerViewY%360;
    		 if(myRotY < 0) myRotY = 360 + myRotY;
    		 
    		 float myRotX = rm.playerViewX;
    		 float relDeg = (float)(Math.atan(relX/relZ) * 180/Math.PI);
    		 float finalDeg = 0;

    		 if(relZ > 0 && relX > 0)	   finalDeg = 360 - relDeg;
    		 else if(relZ < 0 && relX > 0) finalDeg = 180 - relDeg;
    		 else if(relZ < 0 && relX < 0) finalDeg = 180 - relDeg;
    		 else if(relZ > 0 && relX < 0) finalDeg = -relDeg;
    		 
    		 finalDeg = finalDeg - myRotY;
    		 if(finalDeg < 0) finalDeg = 360 + finalDeg;

    		 if(finalDeg < 300 && finalDeg > 180)     s = "<<<" + s;
    		 else if(finalDeg > 60 && finalDeg < 180) s = s + ">>>";
    		 //else return;
    		 
    		 int stringWidth = fontrenderer.getStringWidth(s);
    		 x = (int)((Math.sin(finalDeg*Math.PI/180.0) * screenX/2) + (screenX/2) - (stringWidth/2));

    		 if(x < 0) x = 0;
    		 else if(x > screenX - fontrenderer.getStringWidth(s))
    			 x = screenX - fontrenderer.getStringWidth(s);
    		 
    		 
    		 if(y < 0) y = 0;
    		 else if(y > screenY - yOffset)
    			 y = screenY - yOffset;
    	
    		 int color = -1;
    		 EntityLivingBase target = entity.getAttackTarget();
    		 if(target != null && target.getName().equals(this.mc.thePlayer.getName()))
    			 color = 16711680;
    		 
    		 fontrenderer.drawString(s, x, y, color);
		}
	}
}
