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

package com.crowsofwar.avatar.common.entity.data;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class FireballBehavior extends Behavior<EntityFireball> {
	
	public static final DataSerializer<FireballBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();
	
	public static int ID_NOTHING, ID_FALL, ID_PICKUP, ID_PLAYER_CONTROL, ID_THROWN;
	
	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		ID_NOTHING = registerBehavior(Idle.class);
		ID_PLAYER_CONTROL = registerBehavior(PlayerControlled.class);
		ID_THROWN = registerBehavior(Thrown.class);
	}
	
	public static class Idle extends FireballBehavior {
		
		@Override
		public FireballBehavior onUpdate(EntityFireball entity) {
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
	public static class Thrown extends FireballBehavior {
		
		@Override
		public FireballBehavior onUpdate(EntityFireball entity) {
			
			if (entity.isCollided) {
				entity.setDead();
				entity.onCollision();
			}
			
			entity.velocity().add(0, -9.81 / 40, 0);
			
			World world = entity.worldObj;
			if (!entity.isDead) {
				List<Entity> collidedList = world.getEntitiesWithinAABBExcludingEntity(entity,
						entity.getExpandedHitbox());
				if (!collidedList.isEmpty()) {
					Entity collided = collidedList.get(0);
					if (collided instanceof EntityLivingBase && collided != entity.getOwner()) {
						collision((EntityLivingBase) collided, entity);
					} else if (collided != entity.getOwner()) {
						Vector motion = new Vector(collided).minus(new Vector(entity));
						motion.mul(0.3);
						motion.setY(0.08);
						collided.addVelocity(motion.x(), motion.y(), motion.z());
					}
					
				}
			}
			
			return this;
			
		}
		
		private void collision(EntityLivingBase collided, EntityFireball entity) {
			// Add damage TODO use different config value
			double speed = entity.velocity().magnitude();
			collided.attackEntityFrom(
					AvatarDamageSource.causeFloatingBlockDamage(collided, entity.getOwner()), 6);
			collided.setFire(6);
			
			// TODO Push entity
			/*
			 * Vector motion = new Vector(collided).minus(new Vector(entity));
			 * motion.mul(STATS_CONFIG.floatingBlockSettings.push);
			 * motion.setY(0.08); collided.addVelocity(motion.x(), motion.y(),
			 * motion.z());
			 */
			
			// TODO Add XP
			/*
			 * AvatarPlayerData data =
			 * AvatarPlayerData.fetcher().fetch(entity.getOwner()); if
			 * (!collided.worldObj.isRemote && data != null) { float xp =
			 * SKILLS_CONFIG.blockThrowHit; if (collided.getHealth() <= 0) { xp
			 * = SKILLS_CONFIG.blockKill; }
			 * data.getAbilityData(BendingAbility.ABILITY_PICK_UP_BLOCK).addXp(
			 * xp); }
			 */
			// Remove the floating block & spawn particles
			if (!entity.worldObj.isRemote) entity.setDead();
			entity.onCollision();
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
	public static class PlayerControlled extends FireballBehavior {
		
		public PlayerControlled() {}
		
		@Override
		public FireballBehavior onUpdate(EntityFireball entity) {
			EntityPlayer player = entity.getOwner();
			
			if (player == null) return this;
			
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
			
			double yaw = Math.toRadians(player.rotationYaw);
			double pitch = Math.toRadians(player.rotationPitch);
			Vector forward = Vector.toRectangular(yaw, pitch);
			Vector eye = Vector.getEyePos(player);
			Vector target = forward.times(2).plus(eye);
			Vector motion = target.minus(new Vector(entity));
			motion.mul(5);
			entity.velocity().set(motion);
			
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
}
