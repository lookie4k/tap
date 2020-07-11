/*
 * Copyright (c) $date.year Noonmaru
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.noonmaru.tap.v1_16_R1.protocol

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.github.noonmaru.tap.fake.createFakeEntity
import com.github.noonmaru.tap.protocol.PacketSupport
import com.mojang.datafixers.util.Pair
import net.minecraft.server.v1_16_R1.EnumItemSlot
import net.minecraft.server.v1_16_R1.ItemStack
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityEquipment
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector
import java.util.*

class NMSPacketSupport : PacketSupport {
    override fun spawnEntity(
        entityId: Int,
        uuid: UUID,
        x: Double,
        y: Double,
        z: Double,
        type: EntityType,
        objectId: Int
    ): PacketContainer {
        return PacketContainer(PacketType.Play.Server.SPAWN_ENTITY).apply {
            integers
                .write(0, entityId)
            uuiDs
                .write(0, uuid)
            doubles
                .write(0, x)
                .write(1, y)
                .write(2, z)
            entityTypeModifier
                .write(0, type)
            integers
                .write(6, objectId)
        }
    }

    override fun spawnEntityLiving(
        entityId: Int,
        uuid: UUID,
        typeId: Int,
        loc: Location,
        headPitch: Float,
        velocity: Vector
    ): PacketContainer {
        return PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING).apply {
            integers
                .write(0, entityId)
            uuiDs
                .write(0, uuid)
            integers
                .write(1, typeId)
            doubles
                .write(0, loc.x)
                .write(1, loc.y)
                .write(2, loc.z)
            integers
                .write(2, (velocity.x.coerceIn(-3.9, 3.9) * 8000.0).toInt())
                .write(3, (velocity.y.coerceIn(-3.9, 3.9) * 8000.0).toInt())
                .write(4, (velocity.z.coerceIn(-3.9, 3.9) * 8000.0).toInt())
            bytes
                .write(0, (loc.yaw * 256.0F / 360.0F).toByte())
                .write(0, (loc.pitch * 256.0F / 360.0F).toByte())
                .write(0, (headPitch * 256.0F / 360.0F).toByte())
        }
    }

    override fun entityMetadata(entityId: Int, dataWatcher: WrappedDataWatcher): PacketContainer {
        return PacketContainer(PacketType.Play.Server.ENTITY_METADATA).apply {
            integers
                .write(0, entityId)
            watchableCollectionModifier
                .write(0, dataWatcher.deepClone().watchableObjects)
        }
    }

    override fun entityEquipment(
        entityId: Int,
        slot: EquipmentSlot,
        item: org.bukkit.inventory.ItemStack
    ): PacketContainer {
        return PacketContainer.fromPacket(
            PacketPlayOutEntityEquipment(
                entityId,
                Collections.singletonList(
                    Pair(
                        slot.convertToItemSlot(),
                        CraftItemStack.asNMSCopy(item)
                    )
                )
            )
        )
    }

    override fun entityEquipment(living: LivingEntity): List<PacketContainer> {
        val nmsEntity = (living as CraftLivingEntity).handle

        val list = arrayListOf<Pair<EnumItemSlot, ItemStack>>()

        for (slot in EnumItemSlot.values()) {
            list += Pair(slot, nmsEntity.getEquipment(slot))
        }

        return Collections.singletonList(
            PacketContainer.fromPacket(
                PacketPlayOutEntityEquipment(
                    living.entityId,
                    list
                )
            )
        )
    }

    override fun entityTeleport(
        entityId: Int,
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float,
        onGround: Boolean
    ): PacketContainer {
        return PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT).apply {
            integers
                .write(0, entityId)
            doubles
                .write(0, x)
                .write(1, y)
                .write(2, z)
            bytes
                .write(0, (yaw * 256.0F / 360.0F).toByte())
                .write(0, (pitch * 256.0F / 360.0F).toByte())
            booleans
                .write(0, onGround)
        }
    }

    override fun relEntityMove(
        entityId: Int,
        deltaX: Short,
        deltaY: Short,
        deltaZ: Short,
        onGround: Boolean
    ): PacketContainer {
        return PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE).apply {
            integers
                .write(0, entityId)
            shorts
                .write(0, deltaX)
                .write(1, deltaY)
                .write(2, deltaZ)
            booleans
                .write(0, onGround)
        }
    }

    override fun relEntityMoveLook(
        entityId: Int,
        deltaX: Short,
        deltaY: Short,
        deltaZ: Short,
        yaw: Float,
        pitch: Float,
        onGround: Boolean
    ): PacketContainer {
        return PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK).apply {
            integers
                .write(0, entityId)
            shorts
                .write(0, deltaX)
                .write(1, deltaY)
                .write(2, deltaZ)
            bytes
                .write(0, (yaw * 256.0F / 360.0F).toByte())
                .write(1, (pitch * 256.0F / 360.0F).toByte())
            booleans
                .write(0, onGround)
        }
    }

    override fun mount(entityId: Int, mountEntityIds: IntArray): PacketContainer {
        return PacketContainer(PacketType.Play.Server.MOUNT).apply {
            integers
                .write(0, entityId)
            integerArrays
                .write(0, mountEntityIds)
        }
    }

    override fun entityDestroy(entityIds: IntArray): PacketContainer {
        return PacketContainer(PacketType.Play.Server.ENTITY_DESTROY).apply {
            integerArrays
                .write(0, entityIds)
        }
    }

    override fun spawnFireworkParticles(loc: Location, effect: FireworkEffect): List<PacketContainer> {
        val world = requireNotNull(loc.world) { "World cannot be null" }
        val firework = requireNotNull(Firework::class.java.createFakeEntity(world)) { "Failed to create Firework" }

        return firework.run {
            fireworkMeta = fireworkMeta.apply { addEffect(effect) }

            listOf(
                PacketContainer(PacketType.Play.Server.SPAWN_ENTITY).apply {
                    integers
                        .write(0, entityId)
                    uuiDs
                        .write(0, uniqueId)
                    doubles
                        .write(0, loc.x)
                        .write(1, loc.y)
                        .write(2, loc.z)
                    entityTypeModifier
                        .write(0, EntityType.FIREWORK)
                    integers
                        .write(6, 76)
                },
                entityMetadata(this),
                PacketContainer(PacketType.Play.Server.ENTITY_STATUS).apply {
                    integers
                        .write(0, entityId)
                    bytes
                        .write(0, 17.toByte())
                },
                entityDestroy(intArrayOf(entityId))
            )
        }
    }
}

internal fun EquipmentSlot.convertToItemSlot(): EnumItemSlot {
    return when (this) {
        EquipmentSlot.HAND -> EnumItemSlot.MAINHAND
        EquipmentSlot.OFF_HAND -> EnumItemSlot.OFFHAND
        EquipmentSlot.FEET -> EnumItemSlot.FEET
        EquipmentSlot.LEGS -> EnumItemSlot.LEGS
        EquipmentSlot.CHEST -> EnumItemSlot.CHEST
        EquipmentSlot.HEAD -> EnumItemSlot.HEAD
    }
}