package com.echorpg.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echorpg.repository.StoryRepository
import com.echorpg.repository.GirlRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ChatViewModel(
    private val storyId: String,
    private val persona: Persona,
    private val storyTitle: String,
    private val storyRepository: StoryRepository,
    private val girlRepository: GirlRepository
) : ViewModel() {

    val messages = mutableStateListOf<ChatMessage>()
    var isLoading by mutableStateOf(false)
    var currentChapter by mutableStateOf(1)
    var progress by mutableStateOf(0f)

    val relationships = mutableStateMapOf<String, Int>()
    var moralAlignment by mutableStateOf("Neutral")
    private var userMessagesInCurrentChapter = 0

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    // ==================== EXACT 4 GIRLS FROM YOUR SEEDER ====================
    private val mainGirls = when {
        storyTitle.contains("Fantasy Hero", ignoreCase = true) -> "Lira, Elara, Aria, Selene"
        storyTitle.contains("Mafia Underworld", ignoreCase = true) -> "Sophia, Isabella, Valentina, Bianca"
        storyTitle.contains("Demon Realm", ignoreCase = true) -> "Lilith, Nyx, Vespera, Morgana"
        storyTitle.contains("Cyberpunk Megacity", ignoreCase = true) -> "Nova, Kira, Luna, Raven"
        else -> "the four main girls"
    }

    private val systemPrompt = """
You are writing a high-quality, story-driven interactive novel in real-time called "$storyTitle".

=== CORE RULES (NEVER BREAK THESE) ===
- This is FIRST AND FOREMOST a rich story with plot, world-building, character development, conflict, mystery and emotions.
- Erotic content is allowed but MUST be slow-burn and earned. Never rush into sex.
- Focus heavily on narration (60-70% of every reply): describe environments, emotions, body language, atmosphere, internal thoughts (in italics), consequences of actions.
- Advance the plot in every reply.

=== CRITICAL PLAYER ACTION RULE (MUST OBEY 100%) ===
- Player messages with *asterisks* (*ignores*, *goes to room*, *walks away*, etc.) are real actions. Show realistic consequences — never ignore them or force conversation.

=== 4 MAIN GIRLS (EXACT NAMES) ===
The four main girls are: $mainGirls
Only use these exact names. Never invent new main girls.

=== RESPONSE FORMAT (STRICT) ===
**Narration**
[60-70% rich narration in second person ("you"). No dialogue here.]

**GirlName**
"Exact spoken words here."

**Consequences**
Short reaction + relationship update ONLY if earned.

Current state: Chapter 1/10, Moral Alignment: Neutral.

Begin immediately and respect every player action.
""".trimIndent()

    fun initializeStory() {
        if (messages.isNotEmpty()) return
        messages.add(ChatMessage.Narration("Chapter $currentChapter/10 • $storyTitle\n\nThe story begins...", chapter = currentChapter))
        viewModelScope.launch { sendMessage("Begin the story now.") }
    }

    fun sendMessage(userText: String) {
        userMessagesInCurrentChapter++
        val userMsg = ChatMessage.User(text = userText, chapter = currentChapter)
        messages.add(userMsg)
        isLoading = true

        viewModelScope.launch {
            storyRepository.saveMessage(
                ChatMessageEntity(
                    id = 0L,
                    storyId = storyId,
                    isUser = true,
                    senderName = persona.name,
                    content = userText,
                    isNarration = false,
                    chapter = currentChapter
                )
            )

            try {
                val reply = withContext(Dispatchers.IO) {
                    val history = JSONArray().apply {
                        put(JSONObject().apply { put("role", "system"); put("content", systemPrompt) })
                        val recent = if (messages.size > 12) messages.takeLast(10) else messages
                        recent.forEach { msg ->
                            when (msg) {
                                is ChatMessage.User -> put(JSONObject().apply { put("role", "user"); put("content", msg.text) })
                                is ChatMessage.Ai -> put(JSONObject().apply { put("role", "assistant"); put("content", msg.text) })
                                is ChatMessage.Narration -> {}
                            }
                        }
                    }

                    val json = JSONObject().apply {
                        put("model", "arcee-ai/trinity-large-preview:free")
                        put("messages", history)
                        put("temperature", 0.72)
                        put("max_tokens", 1000)
                    }

                    val request = Request.Builder()
                        .url("https://openrouter.ai/api/v1/chat/completions")
                        .addHeader("Authorization", "Bearer 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")
                        .addHeader("HTTP-Referer", "https://echorpg.app")
                        .addHeader("X-Title", "EchoRPG")
                        .post(json.toString().toRequestBody("application/json".toMediaType()))
                        .build()

                    client.newCall(request).execute().body?.string() ?: "{}"
                }

                val content = JSONObject(reply).getJSONArray("choices")
                    .getJSONObject(0).getJSONObject("message").getString("content")

                parseAndAddReply(content)

            } catch (e: Exception) {
                messages.add(ChatMessage.Ai("The world continues around you...", name = "Narrator", chapter = currentChapter))
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun parseAndAddReply(reply: String) {
        val narrationRegex = Regex("\\*\\*Narration\\*\\*(.*?)\\*\\*", RegexOption.DOT_MATCHES_ALL)
        val dialogueRegex = Regex("\\*\\*([^*]+)\\*\\*\\s*\"(.*?)\"", RegexOption.DOT_MATCHES_ALL)
        val chapterCompleteRegex = Regex("\\[Chapter (\\d+) Complete\\]")

        narrationRegex.find(reply)?.let {
            val text = it.groupValues[1].trim()
            messages.add(ChatMessage.Narration(text = text, chapter = currentChapter))
            storyRepository.saveMessage(
                ChatMessageEntity(
                    id = 0L,
                    storyId = storyId,
                    isUser = false,
                    senderName = "Narrator",
                    content = text,
                    isNarration = true,
                    chapter = currentChapter
                )
            )
        }

        dialogueRegex.findAll(reply).forEach { match ->
            val name = match.groupValues[1].trim()
            val text = match.groupValues[2].trim()
            if (!relationships.containsKey(name)) relationships[name] = 15
            messages.add(ChatMessage.Ai(text = text, name = name, chapter = currentChapter))
            storyRepository.saveMessage(
                ChatMessageEntity(
                    id = 0L,
                    storyId = storyId,
                    isUser = false,
                    senderName = name,
                    content = text,
                    isNarration = false,
                    chapter = currentChapter
                )
            )
        }

        chapterCompleteRegex.find(reply)?.let {
            val completed = it.groupValues[1].toInt()
            if (userMessagesInCurrentChapter >= 8) {
                currentChapter = completed + 1
                progress = (currentChapter - 1) / 10f
                userMessagesInCurrentChapter = 0
                messages.add(ChatMessage.Narration(text = "🌟 Chapter $completed Complete!\nYour bonds have deepened...", chapter = currentChapter))
            }
        }

        if (reply.contains("bond", ignoreCase = true) || reply.contains("smile", ignoreCase = true)) {
            relationships.forEach { (name, level) ->
                relationships[name] = (level + 3).coerceAtMost(100)
            }
        }
    }

    fun resetCurrentChapter() {
        viewModelScope.launch {
            messages.clear()
            relationships.clear()
            moralAlignment = "Neutral"
            userMessagesInCurrentChapter = 0
            progress = (currentChapter - 1) / 10f
            initializeStory()
        }
    }
}