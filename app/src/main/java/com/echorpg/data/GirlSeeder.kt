package com.echorpg.data

import com.echorpg.repository.GirlRepository

object GirlSeeder {

    suspend fun seedIfNeeded(repo: GirlRepository) {
        val girls = listOf(
            // Fantasy Hero
            Triple("lira_fantasy_001", "Lira", "Fantasy Hero"),
            Triple("elara_fantasy_002", "Elara", "Fantasy Hero"),
            Triple("aria_fantasy_003", "Aria", "Fantasy Hero"),
            Triple("selene_fantasy_004", "Selene", "Fantasy Hero"),

            // Mafia Underworld
            Triple("sophia_mafia_001", "Sophia", "Mafia Underworld"),
            Triple("isabella_mafia_002", "Isabella", "Mafia Underworld"),
            Triple("valentina_mafia_003", "Valentina", "Mafia Underworld"),
            Triple("bianca_mafia_004", "Bianca", "Mafia Underworld"),

            // Demon Realm
            Triple("lilith_demon_001", "Lilith", "Demon Realm"),
            Triple("nyx_demon_002", "Nyx", "Demon Realm"),
            Triple("vespera_demon_003", "Vespera", "Demon Realm"),
            Triple("morgana_demon_004", "Morgana", "Demon Realm"),

            // Cyberpunk Megacity
            Triple("nova_cyber_001", "Nova", "Cyberpunk Megacity"),
            Triple("kira_cyber_002", "Kira", "Cyberpunk Megacity"),
            Triple("luna_cyber_003", "Luna", "Cyberpunk Megacity"),
            Triple("raven_cyber_004", "Raven", "Cyberpunk Megacity")
        )

        girls.forEach { (id, name, story) ->
            repo.createOrMeetGirl(id, story, name)
        }
    }
}