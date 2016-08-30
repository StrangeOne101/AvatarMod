package com.crowsofwar.avatar.client.render;

import java.util.Random;

import com.crowsofwar.avatar.common.entity.EntityRavine;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class RenderRavine extends Render<EntityRavine> {
	
	private final Random random;
	
	/**
	 * @param renderManager
	 */
	public RenderRavine(RenderManager renderManager) {
		super(renderManager);
		this.random = new Random();
	}
	
	@Override
	public void doRender(EntityRavine entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		World world = entity.getEntityWorld();
		IBlockState blockState = world.getBlockState(entity.getPosition().offset(EnumFacing.DOWN));
		Block block = blockState.getBlock();
		world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, entity.posX, entity.posY + 0.3, entity.posZ,
				random.nextGaussian() - 0.5, random.nextGaussian() * 0.4, random.nextGaussian() - 0.5,
				Block.getStateId(blockState));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityRavine entity) {
		return null;
	}
	
}
