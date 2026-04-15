package com.github.docilelm.devoniansolo.features

import com.github.synnerz.devonian.api.ItemUtils
import com.github.synnerz.devonian.api.Location
import com.github.synnerz.devonian.api.Scheduler
import com.github.synnerz.devonian.api.dungeon.Dungeons
import com.github.synnerz.devonian.config.Categories
import com.github.synnerz.devonian.features.Feature
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.SkullBlockEntity
import net.minecraft.world.level.block.state.BlockState

object FiftyPingDB : Feature(
    "fiftyPingDB",
    "Makes dungeon breaker 50 ping",
    Categories.DUNGEONS,
    "catacombs",
    subcategory = "QOL",
    cheeto = true
) {
    private val SETTING_FIFTY_PING_CHESTS = addSwitch(
        "fiftyPingRespawn",
        true,
        "Makes the chests/levers mined respawn as if it were 50ping",
        "50ping Respawn",
        cheeto = true,
    )
    private val blacklist = setOf(
        Blocks.TRAPPED_CHEST,
        Blocks.COMMAND_BLOCK,
        Blocks.STONE_BUTTON,
        Blocks.PLAYER_HEAD,
        Blocks.BEDROCK,
        Blocks.OBSIDIAN
    )
    private val respawnList = setOf(
        Blocks.CHEST,
        Blocks.LEVER
    )

    fun onBreak(blockPos: BlockPos, blockState: BlockState, block: Block) {
        if (block in blacklist) return
        if (!isEnabled() || Location.area != "catacombs" || Dungeons.inBoss.value) return
        val itemStack = minecraft.player?.mainHandItem ?: return
        if (ItemUtils.skyblockId(itemStack) != "DUNGEONBREAKER") return
        val shouldRespawn = (checkSkull(block, blockState, blockPos) || block in respawnList) && SETTING_FIFTY_PING_CHESTS.get()
        val world = minecraft.level ?: return
        val soundType = blockState.soundType

        Scheduler.scheduleTask {
            world.removeBlock(blockPos, false)
            // not accurate but idc
            world.playLocalSound(
                blockPos,
                soundType.hitSound,
                SoundSource.BLOCKS,
                soundType.volume,
                soundType.pitch,
                false
            )

            if (shouldRespawn) Scheduler.scheduleTask {
                world.setBlock(blockPos, blockState, 3)
            }
        }
    }

    private fun checkSkull(block: Block, blockState: BlockState, blockPos: BlockPos): Boolean {
        if (block == Blocks.PLAYER_HEAD && blockState.hasBlockEntity()) {
            val entityBlock = minecraft.level?.getBlockEntity(blockPos) ?: return false
            if (entityBlock.type != BlockEntityType.SKULL) return false
            val skullBlock = entityBlock as SkullBlockEntity
            val owner = skullBlock.ownerProfile ?: return false
            val id = owner.partialProfile().id ?: return false

            return "$id" == "e0f3e929-869e-3dca-9504-54c666ee6f23"
        }

        return false
    }
}