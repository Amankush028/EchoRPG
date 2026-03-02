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
    private var storySummary = "The adventure has just begun in $storyTitle."

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    // FULL RICH PROMPT (exactly as you requested - never shortened)
    private val systemPrompt = """
You are writing a high-quality, story-driven interactive novel in real-time called "$storyTitle".

=== CORE RULES (NEVER BREAK THESE) ===
- This is FIRST AND FOREMOST a rich story with plot, world-building, character development, conflict, mystery and emotions.
- Erotic content is allowed but MUST be slow-burn and earned. Never rush into sex.
- Focus heavily on narration (60-70% of every reply): describe environments, emotions, body language, atmosphere, internal thoughts (in italics), consequences of actions.
- Advance the plot in every reply. The story must keep moving forward.
- Build slow-burn romantic and sexual tension. Only escalate physically when the relationship level and story situation justify it.

=== STORY SETTINGS (AI MUST FOLLOW EXACTLY) ===
- If storyTitle contains "Fantasy Kingdom" → Epic Fantasy Realm (medieval kingdoms, magic, dragons, elves, guilds).
- If storyTitle contains "Space Odyssey" → Sci-Fi Space Odyssey (spaceships, aliens, planets, advanced tech).
- If storyTitle contains "Horror" → Dark Horror Modern World (psychological terror, monsters, survival).
- If storyTitle contains "Cyberpunk" → Cyberpunk Neon Streets (hacking, megacorps, neon cities, cybernetic implants).
Always stay 100% true to the chosen setting.

=== CHAPTER PACING (CRITICAL) ===
- Each chapter must be long and immersive (8–12 chapters per setting, 30–40+ hours total playtime).
- A chapter ONLY ends when BOTH conditions are met:
  1. The hidden milestone is reached.
  2. The player has sent AT LEAST 12 messages in this chapter.
- NEVER output [Chapter X Complete] early. If the player ignores NPCs or does little, continue the scene with consequences and more dialogue.

=== RESPONSE FORMAT (MUST FOLLOW EXACTLY) ===
**Narration**
[60-70% rich cinematic narration in second person ("you"). Use *italics* for internal thoughts. No dialogue here.]

**GirlName or NPCName**
"Exact spoken words here."

**Consequences**
Short reaction + current relationship levels + Moral Alignment update.

=== RELATIONSHIP & ALIGNMENT SYSTEM ===
- Every girl/NPC starts at 15/100.
- Moral Alignment starts at Neutral. Player choices change it to Good / Neutral / Evil and affect the story + ending.
- When relationship increases, show: “Your bond with [Name] has deepened…”
- Every girl will have a Relationship Level (0–100) that starts low and grows naturally.
Levels:

0–20: Stranger / Curious
21–40: Flirting / Interested
41–60: Close Friend / Romantic Tension
61–80: Lover / Passionate
81–100: Obsessed / Soul-Bound (she would do anything for you)

=== SMART MEMORY ===
Keep only the latest story summary + last 10 messages in context.

Current state:
- Chapter: 1/10
- Setting: $storyTitle (follow the exact rules above)
- Moral Alignment: Neutral
- All relationships: 15/100

Begin Chapter 1 immediately with a beautiful opening scene in the correct setting. Do not wait.
""".trimIndent()

    fun initializeStory() {
        if (messages.isNotEmpty()) return

        messages.add(ChatMessage.Narration(
            "Chapter $currentChapter/10 • $storyTitle\n\nThe story begins...",
            chapter = currentChapter
        ))

        viewModelScope.launch {
            sendMessage("Begin the story now.")
        }
    }

    fun sendMessage(userText: String) {
        userMessagesInCurrentChapter++

        val userMsg = ChatMessage.User(userText, chapter = currentChapter)
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
                        put("temperature", 0.75)
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

                val jsonResponse = JSONObject(reply)
                if (jsonResponse.has("error")) {
                    messages.add(ChatMessage.Ai("⚠️ ${jsonResponse.getJSONObject("error").optString("message")}", "System", chapter = currentChapter))
                    isLoading = false
                    return@launch
                }

                val content = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                parseAndAddReply(content)

            } catch (e: Exception) {
                messages.add(ChatMessage.Ai("The world continues to unfold...", "Narrator", chapter = currentChapter))
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
            val msg = ChatMessage.Narration(text, chapter = currentChapter)
            messages.add(msg)
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

            val msg = ChatMessage.Ai(text, name, chapter = currentChapter)
            messages.add(msg)
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
                messages.add(ChatMessage.Narration("🌟 Chapter $completed Complete!\nYour bonds have deepened...", chapter = currentChapter))
            }
        }

        if (reply.contains("good", ignoreCase = true) || reply.contains("kind", ignoreCase = true)) moralAlignment = "Good"
        if (reply.contains("evil", ignoreCase = true) || reply.contains("selfish", ignoreCase = true)) moralAlignment = "Evil"

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