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
        Blocks.LEVER,
        Blocks.PLAYER_HEAD,
    )

    fun onBreak(blockPos: BlockPos, blockState: BlockState, block: Block) {
        if (block in blacklist) return
        if (!isEnabled() || Location.area != "catacombs" || Dungeons.inBoss.value) return
        val itemStack = minecraft.player?.mainHandItem ?: return
        if (ItemUtils.skyblockId(itemStack) != "DUNGEONBREAKER") return
        val shouldRespawn = block in respawnList && SETTING_FIFTY_PING_CHESTS.get()
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
}