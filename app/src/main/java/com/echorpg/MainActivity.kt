package com.echorpg

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.echorpg.ui.character.CharactersScreen   // ← change to this
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.echorpg.repository.GirlRepository
import com.echorpg.data.AppDatabase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.echorpg.data.Girl
import com.echorpg.data.Persona
import com.echorpg.ui.character.CharacterCreationScreen
import com.echorpg.ui.chat.FreeChatScreen
import com.echorpg.ui.chat.GroupChatScreen
import com.echorpg.ui.chat.StoryChatScreen
import com.echorpg.ui.ending.EndingScreen
import com.echorpg.ui.home.BottomNavBar
import com.echorpg.ui.home.HomeScreen
import com.echorpg.ui.mystories.MyStoriesScreen
import com.echorpg.ui.onboarding.OnboardingScreen
import com.echorpg.ui.profile.ProfileScreen
import com.echorpg.ui.splash.SplashScreen
import com.echorpg.ui.theme.EchoRPGTheme
import com.echorpg.ui.chapterselect.ChapterSelectScreen
import com.echorpg.ui.character.CharactersScreen

class MainActivity : ComponentActivity() {

    private fun isOnboardingCompleted(): Boolean {
        val prefs = getSharedPreferences("echorpg_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("onboarding_completed", false)
    }

    private fun markOnboardingCompleted() {
        val prefs = getSharedPreferences("echorpg_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_completed", true).apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ONE-TIME SEEDING
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@MainActivity)
            GirlRepository(db).seedIfNeeded()
        }

        setContent {
            EchoRPGTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: ""

                val showBottomBar = currentRoute in listOf("home", "my_stories", "characters", "profile")

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController, currentRoute = currentRoute)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (isOnboardingCompleted()) "home" else "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Onboarding flow
                        composable("splash") {
                            SplashScreen(onSplashFinished = {
                                navController.navigate("onboarding") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }

                        composable("onboarding") {
                            OnboardingScreen(onGetStarted = {
                                markOnboardingCompleted()
                                navController.navigate("home") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            })
                        }

                        // Main tabs
                        composable("home") {
                            HomeScreen(
                                onStartStory = { storyTitle: String ->
                                    navController.navigate("chapter_select/$storyTitle")
                                }
                            )
                        }

                        composable("my_stories") {
                            MyStoriesScreen(
                                onStoryResume = { storyId, personaName ->
                                    navController.navigate("story_chat/$storyId?chapter=1")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("characters") {
                            CharactersScreen(
                                repository = GirlRepository(AppDatabase.getDatabase(this@MainActivity)),  // ← fixed
                                onGirlSelected = { girl: Girl ->   // ← your original type (no change needed)
                                    navController.navigate("free_chat/${girl.id}")
                                },
                                onGroupChat = { selectedGirls: List<Girl> ->   // ← your original type
                                    val ids = selectedGirls.joinToString(",") { it.id.toString() }
                                    navController.navigate("group_chat?girls=$ids")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(onBack = { navController.popBackStack() })
                        }

                        // Story flow
                        composable("chapter_select/{storyTitle}") { backStackEntry ->
                            val storyTitle = backStackEntry.arguments?.getString("storyTitle") ?: "Fantasy Kingdom"
                            ChapterSelectScreen(
                                storyTitle = storyTitle,
                                onChapterSelected = { chapterNum ->
                                    if (chapterNum == 0) {
                                        navController.navigate("character_creation/$storyTitle")
                                    } else {
                                        navController.navigate("story_chat/$storyTitle?chapter=$chapterNum")
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "character_creation/{storyTitle}",
                            arguments = listOf(navArgument("storyTitle") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val storyTitle = backStackEntry.arguments?.getString("storyTitle") ?: "Fantasy Kingdom"
                            CharacterCreationScreen(
                                storyTitle = storyTitle,
                                onBeginStory = { persona ->
                                    navController.navigate("story_chat/$storyTitle?chapter=1")
                                }
                            )
                        }

                        composable(
                            route = "story_chat/{storyTitle}?chapter={chapter}",
                            arguments = listOf(
                                navArgument("storyTitle") { type = NavType.StringType },
                                navArgument("chapter") { type = NavType.IntType; defaultValue = 1 }
                            )
                        ) { backStackEntry ->
                            val storyTitle = backStackEntry.arguments?.getString("storyTitle") ?: "Fantasy Kingdom"
                            val chapterToStart = backStackEntry.arguments?.getInt("chapter") ?: 1

                            val persona = Persona(
                                name = "You",
                                title = "Sir",
                                age = "28",
                                appearance = "Tall, muscular, dark hair, intense eyes, commanding presence",
                                vibe = "Dominant",
                                kinks = listOf("Rough", "Dirty Talk", "Dominance"),
                                hardLimits = "No blood, no underage, no scat"
                            )

                            StoryChatScreen(
                                storyTitle = storyTitle,
                                persona = persona,
                                chapterToStart = chapterToStart,
                                onBack = { navController.popBackStack() },
                                onFinishStory = {
                                    navController.navigate("ending/$storyTitle?personaName=You")
                                }
                            )
                        }

                        // Free chat
                        composable("free_chat/{girlId}") { backStackEntry ->
                            val girlId = backStackEntry.arguments?.getString("girlId") ?: "1"
                            val dummyGirl = Girl(
                                id = girlId.toInt(),
                                name = "Lira",
                                fromStory = "Fantasy Kingdom",
                                relationshipLevel = 50
                            )
                            val persona = Persona(name = "You", title = "Sir")
                            FreeChatScreen(girl = dummyGirl, persona = persona)
                        }

                        // Group chat (NEW)
                        composable("group_chat?girls={girls}") { backStackEntry ->
                            val girlsParam = backStackEntry.arguments?.getString("girls") ?: "1"
                            val selectedIds = girlsParam.split(",").mapNotNull { it.toIntOrNull() }

                            // Demo girls - in real app load from Room by IDs
                            val dummyGirls = listOf(
                                Girl(1, "Lira", "Fantasy Kingdom"),
                                Girl(2, "Elara", "Fantasy Kingdom")
                            ).filter { selectedIds.contains(it.id) }

                            val persona = Persona(name = "You", title = "Sir")

                            GroupChatScreen(
                                girls = dummyGirls,
                                persona = persona,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Ending
                        composable(
                            route = "ending/{storyTitle}?personaName={personaName}",
                            arguments = listOf(
                                navArgument("storyTitle") { type = NavType.StringType },
                                navArgument("personaName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val storyTitle = backStackEntry.arguments?.getString("storyTitle") ?: "Fantasy Kingdom"
                            val personaName = backStackEntry.arguments?.getString("personaName") ?: "You"

                            val persona = Persona(
                                name = personaName,
                                title = "Sir",
                                age = "28",
                                appearance = "Tall, muscular, dark hair, intense eyes, commanding presence",
                                vibe = "Dominant",
                                kinks = listOf("Rough", "Dirty Talk", "Dominance"),
                                hardLimits = "No blood, no underage, no scat"
                            )

                            EndingScreen(
                                storyTitle = storyTitle,
                                persona = persona,
                                unlockedGirls = emptyList(),
                                onFreeChat = { navController.navigate("characters") },
                                onReplay = { navController.navigate("character_creation/$storyTitle") },
                                onBackToHome = {
                                    navController.navigate("home") {
                                        popUpTo("ending") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}