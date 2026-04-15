package com.github.docilelm.devoniansolo

import com.github.synnerz.devonian.Devonian
import com.github.docilelm.devoniansolo.features.FiftyPingDB
import com.github.docilelm.devoniansolo.features.NoRotate
import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory

object DevonianSolo : ClientModInitializer {
    private val logger = LoggerFactory.getLogger("devoniansolo")
//	val keybindCategory by lazy {
//		KeyMapping.Category.register(
//			Identifier.fromNamespaceAndPath(
//				"devoniansolo",
//				"keybinds"
//			)
//		)
//	}

	override fun onInitializeClient() {
		logger.warn("Initialized DocilElm/DevonianSolo")
	}

	fun preInit() {
		Devonian.addFeatureInstance(NoRotate)
		Devonian.addFeatureInstance(FiftyPingDB)
	}
}