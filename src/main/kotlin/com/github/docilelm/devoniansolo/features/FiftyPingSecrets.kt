package com.github.docilelm.devoniansolo.features

import com.github.synnerz.devonian.api.dungeon.DungeonEvent
import com.github.synnerz.devonian.api.events.WorldChangeEvent
import com.github.synnerz.devonian.config.Categories
import com.github.synnerz.devonian.features.Feature
import com.github.synnerz.devonian.features.dungeons.clear.DungeonWaypoints.WaypointsDataJSON
import com.google.gson.Gson
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks

// TODO: this is not actually 50 ping, it's 0 ping but i can't really be bothered rn
object FiftyPingSecrets : Feature(
    "fiftyPingSecrets",
    "Spawns fake chests/essences in dungeon rooms that you can click before your client receives the block update" +
            "(NOTE: if a chest takes longer to spawn your click wont be accepted by the server)",
    Categories.DUNGEONS,
    "catacombs",
    subcategory = "QOL",
    cheeto = true
) {
    val waypointsData = Gson().fromJson(
        this::class.java.getResourceAsStream("/assets/devonian/dungeons/DungeonWaypoints.json")
            ?.bufferedReader()
            .use { it?.readText() },
        Array<WaypointsDataJSON>::class.java
    ).toList()
    private val roomsUpdated = mutableListOf<Int>()

    override fun initialize() {
        on<DungeonEvent.RoomEnter> { event ->
            val room = event.room
            val roomID = room.roomID ?: return@on
            if (roomsUpdated.contains(roomID)) return@on

            val world = minecraft.level ?: return@on
            val waypoints = waypointsData.find { it.roomID == roomID } ?: return@on

            // TODO: check if the block already exists
            waypoints.waypoints.forEach { (type, comp) ->
                if (type != "chest" && type != "essence") return@on

                comp.forEach { compPos ->
                    val pos = room.fromComp(compPos.getOrNull(0) ?: return@on, compPos.getOrNull(2) ?: return@on) ?: return@on
                    val bp = BlockPos(pos.first, compPos.getOrNull(1) ?: return@on, pos.second)

                    when (type) {
                        "chest" -> {
                            world.setBlock(bp, Blocks.ENDER_CHEST.defaultBlockState(), 3)
                        }
                        "essence" -> {
                            world.setBlock(bp, Blocks.WITHER_SKELETON_SKULL.defaultBlockState(), 3)
                        }
                        else -> return@forEach
                    }
                }
            }

            roomsUpdated.add(roomID)
        }
    }

    override fun onWorldChange(event: WorldChangeEvent) {
        roomsUpdated.clear()
    }
}