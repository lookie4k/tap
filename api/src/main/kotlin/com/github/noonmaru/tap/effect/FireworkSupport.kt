/*
 * Copyright (c) 2020 Noonmaru
 *
 * Licensed under the General Public License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-2.0.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.noonmaru.tap.effect

import com.github.noonmaru.tap.loader.LibraryLoader
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

abstract class FireworkSupport {
    abstract fun playFirework(player: Player, loc: Location, effect: FireworkEffect)

    abstract fun playFirework(world: World, loc: Location, effect: FireworkEffect, distance: Double)
}

private val SUPPORT: FireworkSupport = LibraryLoader.load(FireworkSupport::class.java)

fun Player.playFirework(loc: Location, effect: FireworkEffect) {
    SUPPORT.playFirework(this, loc, effect)
}

fun World.playFirework(loc: Location, effect: FireworkEffect, distance: Double = 128.0) {
    SUPPORT.playFirework(this, loc, effect, distance)
}