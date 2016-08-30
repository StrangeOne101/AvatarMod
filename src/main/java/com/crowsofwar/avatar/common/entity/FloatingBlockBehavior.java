package com.crowsofwar.avatar.common.entity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.earth.EarthbendingEvent;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class FloatingBlockBehavior {
	
	private static final DataSerializer<FloatingBlockBehavior> SERIALIZER_BEHAVIOR = new DataSerializer<FloatingBlockBehavior>() {
		
		@Override
		public void write(PacketBuffer buf, FloatingBlockBehavior value) {
			buf.writeInt(value.getId());
			value.toBytes(buf);
		}
		
		@Override
		public FloatingBlockBehavior read(PacketBuffer buf) throws IOException {
			try {
				
				FloatingBlockBehavior behavior = behaviorIdToClass.get(buf).newInstance();
				behavior.fromBytes(buf);
				return behavior;
				
			} catch (Exception e) {
				
				AvatarLog.error("Error reading FloatingBlockBehavior from bytes");
				e.printStackTrace();
				return null;
				
			}
		}
		
		@Override
		public DataParameter<FloatingBlockBehavior> createKey(int id) {
			return new DataParameter<>(id, this);
		}
	};
	
	private static int nextId = 1;
	private static final Map<Integer, Class<? extends FloatingBlockBehavior>> behaviorIdToClass;
	static {
		behaviorIdToClass = new HashMap<>();
	}
	
	/**
	 * The floating block that this block belongs to.
	 * <p>
	 * NOTE: Is null during client-side construction from packet buffer.
	 */
	protected EntityFloatingBlock floating;
	private final int id;
	
	public FloatingBlockBehavior() {
		this.id = nextId;
		this.behaviorIdToClass.put(nextId, getClass());
		nextId++;
	}
	
	public FloatingBlockBehavior(EntityFloatingBlock floating) {
		this();
		this.floating = floating;
	}
	
	public void setFloatingBlock(EntityFloatingBlock floating) {
		this.floating = floating;
	}
	
	/**
	 * Called every update tick.
	 * 
	 * @return Next FloatingBlockBehavior. Return <code>this</code> to continue the
	 *         FloatingBlockBehavior.
	 */
	public abstract FloatingBlockBehavior onUpdate();
	
	public abstract void fromBytes(PacketBuffer buf);
	
	public abstract void toBytes(PacketBuffer buf);
	
	public final int getId() {
		return id;
	}
	
	public class Place extends FloatingBlockBehavior {
		
		private BlockPos placeAt;
		
		public Place() {}
		
		public Place(BlockPos placeAt) {
			this.placeAt = placeAt;
		}
		
		@Override
		public FloatingBlockBehavior onUpdate() {
			BlockPos target = floating.getMovingToBlock();
			Vector targetVec = new Vector(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
			Vector thisPos = new Vector(floating);
			Vector force = targetVec.minus(thisPos);
			force.normalize();
			force.mul(3);
			floating.setVelocity(force);
			if (!floating.worldObj.isRemote && targetVec.sqrDist(thisPos) < 0.01) {
				
				floating.setDead();
				floating.worldObj.setBlockState(new BlockPos(floating), floating.getBlockState());
				
				// TODO move BlockPlacedReached sound into EarthSoundHandler
				SoundType sound = floating.getBlock().getSoundType();
				if (sound != null) {
					floating.worldObj.playSound(null, target, sound.getBreakSound(), SoundCategory.PLAYERS,
							sound.getVolume(), sound.getPitch());
				}
				
				BendingManager.getBending(BendingManager.BENDINGID_EARTHBENDING)
						.notifyObservers(new EarthbendingEvent.BlockPlacedReached(floating));
				
			}
			
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {
			placeAt = buf.readBlockPos();
		}
		
		@Override
		public void toBytes(PacketBuffer buf) {
			buf.writeBlockPos(placeAt);
		}
		
	}
	
	public class Thrown extends FloatingBlockBehavior {
		
		public Thrown() {}
		
		/**
		 * @param floating
		 */
		public Thrown(EntityFloatingBlock floating) {
			super(floating);
		}
		
		@Override
		public FloatingBlockBehavior onUpdate() {
			World world = floating.worldObj;
			if (!floating.isDead) {
				List<Entity> collidedList = world.getEntitiesWithinAABBExcludingEntity(floating,
						floating.getEntityBoundingBox());
				if (!collidedList.isEmpty()) {
					Entity collided = collidedList.get(0);
					if (collided instanceof EntityLivingBase && collided != floating.getOwner()) {
						double speed = floating.getVelocity().magnitude();
						double multiplier = 0.25;
						collided.attackEntityFrom(
								AvatarDamageSource.causeFloatingBlockDamage(floating, collided),
								(float) (speed * multiplier));
						
						Vector motion = new Vector(collided).minus(new Vector(floating));
						
						motion.setY(0.08);
						collided.addVelocity(motion.x(), motion.y(), motion.z());
						if (!world.isRemote) floating.setDead();
						floating.onCollision();
					} else if (collided != floating.getOwner()) {
						Vector motion = new Vector(collided).minus(new Vector(floating));
						motion.mul(0.3);
						motion.setY(0.08);
						collided.addVelocity(motion.x(), motion.y(), motion.z());
					}
				}
			}
			
			if (floating.isCollided) {
				if (!world.isRemote) floating.setDead();
				floating.onCollision();
				BendingManager.getBending(BendingManager.BENDINGID_EARTHBENDING)
						.notifyObservers(new EarthbendingEvent.BlockThrownReached(floating));
			}
			
			return this;
			
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
	public class PickUp extends FloatingBlockBehavior {
		
		public PickUp() {}
		
		/**
		 * @param floating
		 */
		public PickUp(EntityFloatingBlock floating) {
			super(floating);
		}
		
		@Override
		public FloatingBlockBehavior onUpdate() {
			if (floating.ticksExisted > 20) {
				return new PlayerControlled(floating, floating.getOwner());
			}
			
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
	public class PlayerControlled extends FloatingBlockBehavior {
		
		private String playerName;
		private EntityPlayer player;
		
		public PlayerControlled() {}
		
		public PlayerControlled(EntityFloatingBlock floating, EntityPlayer player) {
			super(floating);
			this.player = player;
		}
		
		private EntityPlayer getControllingPlayer() {
			if (player != null) {
				return player;
			} else {
				return player = floating.worldObj.getPlayerEntityByName(playerName);
			}
		}
		
		@Override
		public FloatingBlockBehavior onUpdate() {
			EntityPlayer controller = getControllingPlayer();
			
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(controller,
					"Could not get player data to update PlayerControlled Floating Block");
			
			if (floating.isGravityEnabled()) {
				floating.setGravityEnabled(false);
			}
			
			double yaw = Math.toRadians(controller.rotationYaw);
			double pitch = Math.toRadians(controller.rotationPitch);
			Vector forward = Vector.fromYawPitch(yaw, pitch);
			Vector eye = Vector.getEyePos(controller);
			Vector target = forward.times(2).plus(eye);
			Vector motion = target.minus(new Vector(floating));
			motion.mul(5);
			floating.setVelocity(motion);
			
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {
			playerName = buf.readStringFromBuffer(16);
		}
		
		@Override
		public void toBytes(PacketBuffer buf) {
			buf.writeString(player.getName());
		}
		
	}
	
}
