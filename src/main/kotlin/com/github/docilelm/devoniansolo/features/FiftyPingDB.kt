package com.github.docilelm.devoniansolo.features

import com.github.synnerz.devonian.api.ItemUtils
import com.github.synnerz.devonian.api.Location
import com.github.synnerz.devonian.api.Scheduler
import com.github.synnerz.devonian.api.dungeon.Dungeons
import com.github.synnerz.devonian.config.Categories
import com.github.synnerz.devonian.features.Feature
import kotlinx.atomicfu.atomic
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
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
    private var lastItemStack = atomic(ItemStack.EMPTY)

    fun onBreak(blockPos: BlockPos, blockState: BlockState, block: Block): Boolean {
        if (block in blacklist) return false
        if (!isEnabled() || Location.area != "catacombs" || Dungeons.inBoss.value) return false
        if (lastItemStack.value.item == Items.DIAMOND_PICKAXE && ItemUtils.skyblockId(lastItemStack.value) != "DUNGEONBREAKER") return true
        else if (ItemUtils.skyblockId(lastItemStack.value) != "DUNGEONBREAKER") return false
        val shouldRespawn = block in respawnList && SETTING_FIFTY_PING_CHESTS.get()
        val world = minecraft.level ?: return false
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

        return false
    }

    fun onHeldSlotChange(slot: Int) {
        lastItemStack.value = minecraft.player?.inventory?.getItem(slot) ?: return
    }
}