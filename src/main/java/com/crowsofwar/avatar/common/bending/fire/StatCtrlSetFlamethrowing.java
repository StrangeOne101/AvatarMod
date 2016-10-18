package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlSetFlamethrowing extends StatusControl {
	
	private final boolean setting;
	
	public StatCtrlSetFlamethrowing(boolean setting) {
		super(4, AvatarControl.CONTROL_RIGHT_CLICK_DOWN, CrosshairPosition.RIGHT_OF_CROSSHAIR);
		this.setting = setting;
	}
	
	@Override
	public boolean execute(AbilityContext ctx) {
		
		AvatarPlayerData data = ctx.getData();
		EntityPlayer player = data.getPlayerEntity();
		World world = data.getWorld();
		
		if (data.hasBending(BendingType.FIREBENDING)) {
			FirebendingState state = (FirebendingState) data.getBendingState(BendingType.FIREBENDING);
			state.setFlamethrowing(setting);
			if (setting) ctx.addStatusControl(STOP_FLAMETHROW);
		}
		
		return true;
	}
	
}