/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.gmail.filoghost.holographicdisplays.nms.v1_9_R1;

import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;

import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.disk.Configuration;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSArmorStand;
import com.gmail.filoghost.holographicdisplays.util.Utils;
import com.gmail.filoghost.holographicdisplays.util.reflection.ReflectField;
import com.gmail.filoghost.holographicdisplays.util.reflection.ReflectionUtils;

import net.minecraft.server.v1_9_R1.AxisAlignedBB;
import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.EntityArmorStand;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.EnumHand;
import net.minecraft.server.v1_9_R1.EnumInteractionResult;
import net.minecraft.server.v1_9_R1.EnumItemSlot;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_9_R1.SoundEffect;
import net.minecraft.server.v1_9_R1.Vec3D;
import net.minecraft.server.v1_9_R1.World;

public class EntityNMSArmorStand extends EntityArmorStand implements NMSArmorStand {
	
	private static final ReflectField<Integer> DISABLED_SLOTS_FIELD = new ReflectField<>(EntityArmorStand.class, "bz");

	private boolean lockTick;
	private HologramLine parentPiece;
	
	public EntityNMSArmorStand(World world, HologramLine parentPiece) {
		super(world);
		super.setInvisible(true);
		super.setSmall(true);
		super.setArms(false);
		super.setGravity(true);
		super.setBasePlate(true);
		super.setMarker(true);
		this.parentPiece = parentPiece;
		try {
			DISABLED_SLOTS_FIELD.set(this, Integer.MAX_VALUE);
		} catch (Exception e) {
			// There's still the overridden method.
		}
		forceSetBoundingBox(new NullBoundingBox());
	}
	
	
	@Override
	public void b(NBTTagCompound nbttagcompound) {
		// Do not save NBT.
	}
	
	@Override
	public boolean c(NBTTagCompound nbttagcompound) {
		// Do not save NBT.
		return false;
	}

	@Override
	public boolean d(NBTTagCompound nbttagcompound) {
		// Do not save NBT.
		return false;
	}
	
	@Override
	public void e(NBTTagCompound nbttagcompound) {
		// Do not save NBT.
	}
	
	@Override
	public void f(NBTTagCompound nbttagcompound) {
		// Do not load NBT.
	}
	
	@Override
	public void a(NBTTagCompound nbttagcompound) {
		// Do not load NBT.
	}
	
	
	@Override
	public boolean isInvulnerable(DamageSource source) {
		/*
		 * The field Entity.invulnerable is private.
		 * It's only used while saving NBTTags, but since the entity would be killed
		 * on chunk unload, we prefer to override isInvulnerable().
		 */
	    return true;
	}
	
	@Override
	public boolean isCollidable() {
		return false;
	}
	
	@Override
	public void setCustomName(String customName) {
		// Locks the custom name.
	}
	
	@Override
	public void setCustomNameVisible(boolean visible) {
		// Locks the custom name.
	}

	@Override
	public EnumInteractionResult a(EntityHuman human, Vec3D vec3d, ItemStack itemstack, EnumHand enumhand) {
		// Prevent stand being equipped
		return EnumInteractionResult.PASS;
	}

	@Override
	public boolean c(int i, ItemStack item) {
		// Prevent stand being equipped
		return false;
	}

	@Override
	public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
		// Prevent stand being equipped
	}
	
	@Override
	public void a(AxisAlignedBB boundingBox) {
		// Do not change it!
	}
	
	public void forceSetBoundingBox(AxisAlignedBB boundingBox) {
		super.a(boundingBox);
	}
	
	@Override
	public int getId() {
		if (Configuration.preciseHologramMovement) {
			StackTraceElement element = ReflectionUtils.getStackTraceElement(2);
			if (element != null && element.getFileName() != null && element.getFileName().equals("EntityTrackerEntry.java") && element.getLineNumber() > 142 && element.getLineNumber() < 152) {
				// Then this method is being called when creating a new packet, we return a fake ID!
				return -1;
			}
		}
		
		return super.getId();
	}

	@Override
	public void m() {
		if (!lockTick) {
			super.m();
		}
	}
	
	@Override
	public void a(SoundEffect soundeffect, float f, float f1) {
	    // Remove sounds.
	}
	
	@Override
	public void setCustomNameNMS(String name) {
		if (name != null && name.length() > 300) {
			name = name.substring(0, 300);
		}
		super.setCustomName(name);
		super.setCustomNameVisible(name != null && !name.isEmpty());
	}
	
	@Override
	public String getCustomNameNMS() {
		return super.getCustomName();
	}
	
	@Override
	public void setLockTick(boolean lock) {
		lockTick = lock;
	}
	
	@Override
	public void die() {
		// Prevent being killed.
	}
	
	@Override
	public CraftEntity getBukkitEntity() {
		if (super.bukkitEntity == null) {
			super.bukkitEntity = new CraftNMSArmorStand(super.world.getServer(), this);
	    }
		return super.bukkitEntity;
	}
	
	@Override
	public void killEntityNMS() {
		super.dead = true;
	}
	
	@Override
	public void setLocationNMS(double x, double y, double z) {
		super.setPosition(x, y, z);
		
		if (Configuration.preciseHologramMovement) {
			// Send a packet near to update the position.
			PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(this);
	
			for (Object obj : super.world.players) {
				if (obj instanceof EntityPlayer) {
					EntityPlayer nmsPlayer = (EntityPlayer) obj;
	
					double distanceSquared = Utils.square(nmsPlayer.locX - super.locX) + Utils.square(nmsPlayer.locZ - super.locZ);
					if (distanceSquared < 8192 && nmsPlayer.playerConnection != null) {
						nmsPlayer.playerConnection.sendPacket(teleportPacket);
					}
				}
			}
		}
	}

	@Override
	public boolean isDeadNMS() {
		return super.dead;
	}
	
	@Override
	public int getIdNMS() {
		return super.getId(); // Return the real ID without checking the stack trace.
	}

	@Override
	public HologramLine getHologramLine() {
		return parentPiece;
	}
	
	@Override
	public org.bukkit.entity.Entity getBukkitEntityNMS() {
		return getBukkitEntity();
	}
}
