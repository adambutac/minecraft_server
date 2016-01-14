package com.super_deathagon.supermod.seras;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.super_deathagon.util.MouseOverHelper;



@SideOnly(Side.CLIENT)
public class SerasGui extends GuiIngame{
	private static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
	private static final ResourceLocation mapIcons = new ResourceLocation("textures/map/map_icons.png");

	
	public SerasGui(Minecraft mc){
		super(mc);
	}
	
	public void renderHealthBar(RenderGameOverlayEvent.Pre event){
		EntityPlayer player = this.mc.thePlayer;
		float health = player.getHealth();
		float maxHealth = player.getMaxHealth();
		int healthBarMaxWidth = 182;
		int healthBarWidth = (int)(((float)health/maxHealth)*healthBarMaxWidth);
		
		int xPos = event.resolution.getScaledWidth() / 2 - 91;
		int yPos = event.resolution.getScaledHeight() - 35;
		
		//GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		//GL11.glDisable(GL11.GL_LIGHTING);
		
		this.mc.getTextureManager().bindTexture(icons);

		//health empty
		this.drawTexturedModalRect(xPos, yPos, 0, 74, healthBarMaxWidth, 5);
		//health filled
		this.drawTexturedModalRect(xPos, yPos, 0, 79, healthBarWidth, 5);
		this.func_175179_f().drawString(player.getHealth() + "/" + player.getMaxHealth(), xPos + healthBarMaxWidth, yPos, 13107400);
	}

	public void renderCrosshairs(RenderGameOverlayEvent.Pre event) {
		EntityPlayer player = this.mc.thePlayer;
		EntityLivingBase e = MouseOverHelper.getMouseOverEntityLiving(player, Seras.teleportDistance);
		if(e != null){
			event.setCanceled(true);
			this.func_175179_f().drawString(e.getHealth() + "/" + e.getMaxHealth(), 
					event.resolution.getScaledWidth() / 2 + 3, event.resolution.getScaledHeight() / 2 + 3, 16711680);
			this.mc.getTextureManager().bindTexture(icons);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR, 1, 0);
            GlStateManager.enableAlpha();
            drawTexturedModalRect(event.resolution.getScaledWidth() / 2 - 7, event.resolution.getScaledHeight() / 2 - 7, 0, 0, 16, 16);
            GlStateManager.tryBlendFuncSeparate(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA, 1, 0);
            GL11.glDisable(GL11.GL_BLEND);
		}
	}
}
