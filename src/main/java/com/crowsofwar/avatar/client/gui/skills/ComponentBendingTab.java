/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.client.gui.skills;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
<<<<<<< HEAD
import com.crowsofwar.avatar.client.uitools.ComponentImage;
=======
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.common.bending.BendingType;
>>>>>>> 1.12
import com.crowsofwar.avatar.common.network.packets.PacketSSkillsMenu;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ComponentBendingTab extends UiComponent {
	
<<<<<<< HEAD
	private final int type;
	
	public ComponentBendingTab(int type, boolean isFullTab, boolean isSelected) {
		super(AvatarUiTextures.skillsGui, !isFullTab && isSelected ? 216 : 236, type * 20, 20,
				isFullTab || isSelected ? 20 : 17);
=======
	private final BendingType type;
	private final boolean fullTab;
	
	private final ResourceLocation bendingIconLocation;
	
	public ComponentBendingTab(BendingType type, boolean fullTab) {
		
		bendingIconLocation = new ResourceLocation(
				"avatarmod:textures/gui/tab/" + type.name().toLowerCase() + ".png");
		
>>>>>>> 1.12
		this.type = type;
		this.fullTab = fullTab;
		
	}
	
	@Override
	protected void click(int button) {
		AvatarMod.network.sendToServer(new PacketSSkillsMenu(type));
	}
	
	@Override
	protected float componentWidth() {
		return 20;
	}
	
	@Override
	protected float componentHeight() {
		return fullTab ? 23 : 20;
	}
	
	@Override
	protected void componentDraw(float partialTicks, boolean mouseHover) {
		
		// Draw tab image
		mc.renderEngine.bindTexture(AvatarUiTextures.skillsGui);
		int tabU = fullTab ? 236 : 216;
		int tabV = mouseHover ? 23 : 0;
		drawTexturedModalRect(0, 0, tabU, tabV, 20, fullTab ? 23 : 20);
		
		// Draw component image
		mc.renderEngine.bindTexture(bendingIconLocation);
		GlStateManager.pushMatrix();
		
		double iconScale = 0.75;
		GlStateManager.translate((20 - 20 * iconScale) / 2, (20 - 20 * iconScale) / 2, 0);
		GlStateManager.scale(20.0 / 256, 20.0 / 256, 1);
		GlStateManager.scale(iconScale, iconScale, 1);
		drawTexturedModalRect(0, fullTab ? -3 : 0, 0, 0, 256, 256);
		GlStateManager.popMatrix();
		
	}
	
}
