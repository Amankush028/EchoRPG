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
import kotlinx.coroutines.launch
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

    val relationships = mutableStateMapOf<String, Int>().apply { put("Lira", 15) }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    private val systemPrompt = """
You are writing a high-quality story-driven interactive novel.

CORE RULES:
- Story and character development FIRST. Erotic content only when earned.
- 60-70% rich narration.
- Slow-burn.

Current story: $storyTitle
User persona: ${persona.name} (${persona.title}, ${persona.age}, vibe: ${persona.vibe})

DISCOVERY SYSTEM (VERY IMPORTANT):
Whenever the user learns something new about a girl, output it at the END of your reply using this EXACT format:

[DISCOVER:name:Real Name]
[DISCOVER:appearance:Beautiful silver hair and emerald eyes]
[DISCOVER:personality:Playful and teasing]
[DISCOVER:kinks:Slow-burn romance, teasing]
[DISCOVER:likes:Deep conversations, praise]
[DISCOVER:dislikes:Rudeness]
[DISCOVER:secret:She is the lost princess]

Only use DISCOVER when the user genuinely discovers it in this reply. Unlock gradually.
""".trimIndent()

    init {
        viewModelScope.launch {
            storyRepository.getMessages(storyId).collect { entities ->
                messages.clear()
                entities.forEach { e ->
                    val msg = when {
                        e.isNarration -> ChatMessage.Narration(e.content, e.timestamp, e.chapter)
                        e.isUser -> ChatMessage.User(e.content, e.timestamp, e.chapter)
                        else -> ChatMessage.Ai(e.content, e.senderName, e.timestamp, e.chapter)
                    }
                    messages.add(msg)
                }
            }

            storyRepository.getProgress(storyId).collect { p ->
                p?.let {
                    currentChapter = it.currentChapter
                    progress = it.progress
                }
            }
        }
    }

    fun sendMessage(userText: String) {
        val userMsg = ChatMessage.User(userText, chapter = currentChapter)
        messages.add(userMsg)
        isLoading = true

        viewModelScope.launch {
            // SAVE USER MESSAGE - FULL NAMED PARAMETERS
            storyRepository.saveMessage(
                ChatMessageEntity(
                    id = 0,
                    storyId = storyId,
                    isUser = true,
                    senderName = persona.name,
                    content = userText,
                    isNarration = false,
                    chapter = currentChapter
                )
            )

            try {
                val json = JSONObject().apply {
                    put("model", "arcee-ai/trinity-large-preview:free")
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply { put("role", "system"); put("content", systemPrompt) })
                        put(JSONObject().apply { put("role", "user"); put("content", userText) })
                    })
                    put("temperature", 0.85)
                    put("max_tokens", 900)
                }

                val request = Request.Builder()
                    .url("https://openrouter.ai/api/v1/chat/completions")
                    .addHeader("Authorization", "Bearer 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")
                    .addHeader("HTTP-Referer", "https://echorpg.app")
                    .addHeader("X-Title", "EchoRPG")
                    .post(json.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val responseBody = client.newCall(request).execute().body?.string() ?: "{}"
                val reply = JSONObject(responseBody)
                    .getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content")

                val cleanReply = reply.replace(Regex("\\[DISCOVER:.*\\]"), "").trim()

                // === DYNAMIC GIRL DISCOVERY ===
                processGirlDiscovery(reply)

                val chapterMatch = Regex("\\[Chapter (\\d+) Complete\\]").find(reply)
                if (chapterMatch != null) {
                    val newChapter = chapterMatch.groupValues[1].toInt()
                    currentChapter = newChapter
                    progress = (newChapter - 1) / 10f

                    val narration = ChatMessage.Narration("Chapter $newChapter Complete\n\n$cleanReply", chapter = newChapter)
                    messages.add(narration)
                    storyRepository.saveMessage(
                        ChatMessageEntity(
                            id = 0,
                            storyId = storyId,
                            isUser = false,
                            senderName = "Narrator",
                            content = narration.text,
                            isNarration = true,
                            chapter = newChapter
                        )
                    )
                } else {
                    val aiMsg = ChatMessage.Ai(cleanReply, "Lira", chapter = currentChapter)
                    messages.add(aiMsg)
                    storyRepository.saveMessage(
                        ChatMessageEntity(
                            id = 0,
                            storyId = storyId,
                            isUser = false,
                            senderName = "Lira",
                            content = cleanReply,
                            isNarration = false,
                            chapter = currentChapter
                        )
                    )
                }

                if (relationships["Lira"]!! < 95) {
                    relationships["Lira"] = relationships["Lira"]!! + 2
                    girlRepository.updateRelationship("lira_1", relationships["Lira"]!!)
                }

                storyRepository.saveProgress(
                    StoryProgressEntity(
                        storyId = storyId,
                        title = storyTitle,
                        personaName = persona.name,
                        currentChapter = currentChapter,
                        progress = progress
                    )
                )

            } catch (e: Exception) {
                val fallback = ChatMessage.Ai("The night feels heavier... but I'm still here with you.", "Lira", chapter = currentChapter)
                messages.add(fallback)
                storyRepository.saveMessage(
                    ChatMessageEntity(
                        id = 0,
                        storyId = storyId,
                        isUser = false,
                        senderName = "Lira",
                        content = fallback.text,
                        isNarration = false,
                        chapter = currentChapter
                    )
                )
            }
            isLoading = false
        }
    }

    private suspend fun processGirlDiscovery(reply: String) {
        val regex = Regex("\\[DISCOVER:(\\w+):(.+?)\\]")
        regex.findAll(reply).forEach { match ->
            val field = match.groupValues[1].lowercase()
            val value = match.groupValues[2].trim()
            girlRepository.unlockAttribute("lira_1", field, value)
        }
    }

    suspend fun finishStory() {
        girlRepository.unlockAttribute("lira_1", "name", "Lira")
        girlRepository.unlockAttribute("lira_1", "appearance", "Stunning silver-haired beauty with piercing emerald eyes")
        girlRepository.unlockAttribute("lira_1", "personality", "Playful, teasing, deeply loyal")
        girlRepository.unlockAttribute("lira_1", "kinks", "Slow-burn romance, teasing, dominance play")
        girlRepository.unlockAttribute("lira_1", "likes", "Deep conversations, being praised")
        girlRepository.unlockAttribute("lira_1", "dislikes", "Rudeness")
        girlRepository.unlockAttribute("lira_1", "secret", "She is the lost princess of the fallen kingdom")
    }
}