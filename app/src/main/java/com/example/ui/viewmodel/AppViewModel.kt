package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.bible.BibleData
import com.example.data.bible.SearchResult
import com.example.data.database.AppDatabase
import com.example.data.model.BudgetTransaction
import com.example.data.model.Comment
import com.example.data.model.DailyVerse
import com.example.data.model.DiscussionPost
import com.example.data.model.Member
import com.example.data.model.CachedBibleVerse
import com.example.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Screen {
    Splash, Login, Main
}

enum class Tab {
    Tongasoa, Baiboly, Sampana, Kilalao, Hafa
}

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    // --- State flows ---
    var currentScreen by mutableStateOf(Screen.Splash)
    var currentTab by mutableStateOf(Tab.Tongasoa)

    val members: StateFlow<List<Member>>
    val dailyVerse: StateFlow<DailyVerse?>
    val postsFlow: StateFlow<List<DiscussionPost>>
    val transactions: StateFlow<List<BudgetTransaction>>
    val cachedProtestantChapters: StateFlow<List<String>>
    val cachedCatholicChapters: StateFlow<List<String>>

    private val _currentUser = MutableStateFlow<Member?>(null)
    val currentUser = _currentUser.asStateFlow()

    // --- Auth States ---
    var loginName by mutableStateOf("")
    var loginPhone by mutableStateOf("")
    var authError by mutableStateOf<String?>(null)

    var signupName by mutableStateOf("")
    var signupPhone by mutableStateOf("")
    var signupAddress by mutableStateOf("")
    var signupSampana by mutableStateOf("Sampana Tanora Kristiana (STK)")
    var signupSuccessMessage by mutableStateOf<String?>(null)

    // --- Splash Timer State ---
    var splashProgress by mutableStateOf(0.0f)
    var activeSplashStep by mutableStateOf("Fandefasana ny fampiharana...") // Malagasy loader description

    // --- Bible Tab States ---
    var bibleVersionIsProtestant by mutableStateOf(true) // Protestant vs Catholic
    var bibleTestamentIsNew by mutableStateOf(false)    // Old Testament (False) vs New Testament (True)
    var bibleBookSearchQuery by mutableStateOf("")
    var bibleVerseSearchQuery by mutableStateOf("")
    var bibleTextSize by mutableStateOf(16f) // Slider status

    var selectedBookId by mutableStateOf(1) // Genesisy
    var selectedChapter by mutableStateOf(1)

    var currentChapterVerses by mutableStateOf<List<Pair<Int, String>>>(emptyList())
    var isCurrentChapterCached by mutableStateOf(false)

    // --- Sampana States ---
    var editProjectsText by mutableStateOf("")
    var budgetTypeInput by mutableStateOf("RECETTE") // RECETTE or DEPENSE
    var budgetAmountInput by mutableStateOf("")
    var budgetLabelInput by mutableStateOf("")

    // Discussion Input
    var newPostContent by mutableStateOf("")
    var activeCommentingPostId by mutableStateOf<Int?>(null)
    var newCommentContent by mutableStateOf("")

    // --- Games module (Kilalao) ---
    var selectedDifficulty by mutableStateOf("Mora") // Mora, Salasala, Sarotra
    var quizActiveLevel by mutableStateOf(1) // 1 to 50
    var quizQuestionIndex by mutableStateOf(0) // 0 to 4
    var quizScoreThisLevel by mutableStateOf(0) // 0 to 5
    var quizSelectedAnswer by mutableStateOf<String?>(null)
    var isQuizLevelCompleted by mutableStateOf(false)
    var isQuizAnswerSubmitted by mutableStateOf(false)
    var quizCountdownSeconds by mutableStateOf(30)
    var isQuizGameOver by mutableStateOf(false)
    private var timerJob: Job? = null

    // Bonus Spin State
    val spinItems = listOf("+10 pts", "+20 pts", "Baomba 💣", "Baiboly 📖", "+50 pts", "Fo ❤️", "+10 pts", "Baomba 💣")
    var isSpinning by mutableStateOf(false)
    var spinOutcome by mutableStateOf<String?>(null)
    var spinPointsNotification by mutableStateOf<String?>(null)
    var currentSpinsLeft by mutableStateOf(5)
    var spinCooldownSeconds by mutableStateOf(0) // 15 mins cooldown simulation
    var activeSpinBibleVerse by mutableStateOf<String?>(null)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.appDao())

        members = repository.membersFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        dailyVerse = repository.dailyVerseFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        postsFlow = repository.postsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        transactions = repository.transactionsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        cachedProtestantChapters = repository.getCachedChapterKeysFlow(true).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        cachedCatholicChapters = repository.getCachedChapterKeysFlow(false).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }

        startSplashTimer()
    }

    // --- Splash Animation Logic ---
    private fun startSplashTimer() {
        viewModelScope.launch {
            val steps = listOf(
                "Fandefasana..." to 0.1f,
                "Mampifandray amin'ny Firebase..." to 0.3f,
                "Mitady ny mpikambana..." to 0.5f,
                "Mamaky ny Baiboly Masina..." to 0.65f,
                "Fiangonana Protestanta Fifohazana..." to 0.85f,
                "F.P.Fi - Tonga soa!" to 1.0f
            )

            for (step in steps) {
                activeSplashStep = step.first
                var startVal = splashProgress
                val endVal = step.second
                while (startVal < endVal) {
                    startVal += 0.05f
                    splashProgress = minOf(startVal, 1.0f)
                    delay(400) // approx 10s total (25 intervals * 400ms = 10 seconds)
                }
            }
            currentScreen = Screen.Login
        }
    }

    // --- Authentication Actions ---
    fun login() {
        if (loginName.isBlank() || loginPhone.isBlank()) {
            authError = "Fenoy ny Anarana sy ny Laharan'ny Telefonina"
            return
        }

        viewModelScope.launch {
            val member = repository.getMemberByPhone(loginPhone.trim())
            if (member == null) {
                authError = "Tsy misy io laharana io. Mandehana misoratra anarana aloha."
            } else if (!member.name.equals(loginName.trim(), ignoreCase = true)) {
                authError = "Anarana tsy mifanaraka amin'ny Laharana nomena"
            } else if (member.status == "PENDING") {
                authError = "Miandry fankatoavana avy amin'ny Mpitantana ny kaontinao"
            } else if (member.status == "REJECTED") {
                authError = "Nolavin'ny Mpitantana ny fidiranao. Mifandraisa amin'ny tompon'andraikitra"
            } else {
                _currentUser.value = member
                editProjectsText = member.projects
                currentScreen = Screen.Main
                authError = null
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        currentScreen = Screen.Login
        currentTab = Tab.Tongasoa
    }

    fun signup() {
        if (signupName.isBlank() || signupPhone.isBlank() || signupAddress.isBlank()) {
            signupSuccessMessage = null
            authError = "Tsy maintsy fenoina avokoa ny banga rehetra"
            return
        }

        viewModelScope.launch {
            val existing = repository.getMemberByPhone(signupPhone.trim())
            if (existing != null) {
                authError = "Efa misy mampiasa io laharana io!"
                return@launch
            }

            val newMember = Member(
                phone = signupPhone.trim(),
                name = signupName.trim(),
                address = signupAddress.trim(),
                sampana = signupSampana,
                status = "PENDING",
                isAdmin = false
            )
            repository.insertMember(newMember)
            signupSuccessMessage = "Voasoratra soa aman-tsara! Miandry fankatoavana avy amin'ny Mpitantana ny fidiranao."
            authError = null
            // Clear inputs
            signupName = ""
            signupPhone = ""
            signupAddress = ""
        }
    }

    // --- Admin Validation controls (Fitantanana) ---
    fun validateMember(phone: String, approve: Boolean) {
        viewModelScope.launch {
            val m = repository.getMemberByPhone(phone) ?: return@launch
            val updated = m.copy(status = if (approve) "APPROVED" else "REJECTED")
            repository.insertMember(updated)
        }
    }

    // --- Admin Daily Verse control ---
    fun editDailyVerse(text: String, reference: String) {
        viewModelScope.launch {
            repository.updateDailyVerse(text, reference)
        }
    }

    // --- Cached Bible Cache Actions ---
    fun loadCurrentChapterVerses() {
        viewModelScope.launch {
            val localList = repository.getCachedBibleVerses(bibleVersionIsProtestant, selectedBookId, selectedChapter)
            if (localList.isNotEmpty()) {
                currentChapterVerses = localList.map { it.verseNumber to it.text }
                isCurrentChapterCached = true
            } else {
                // If not cached yet, generate from BibleData fallback
                val generated = BibleData.generateVerses(selectedBookId, selectedChapter)
                currentChapterVerses = generated
                isCurrentChapterCached = false
            }
        }
    }

    fun cacheCurrentChapter() {
        viewModelScope.launch {
            val generated = BibleData.generateVerses(selectedBookId, selectedChapter)
            val bookName = BibleData.books.find { it.id == selectedBookId }?.name ?: "Boky"
            val versesToCache = generated.map { (vNo, text) ->
                CachedBibleVerse(
                    versionIsProtestant = bibleVersionIsProtestant,
                    bookId = selectedBookId,
                    bookName = bookName,
                    chapter = selectedChapter,
                    verseNumber = vNo,
                    text = text
                )
            }
            repository.insertCachedBibleVerses(versesToCache)
            isCurrentChapterCached = true
            loadCurrentChapterVerses()
        }
    }

    fun deleteCurrentChapterCache() {
        viewModelScope.launch {
            repository.deleteCachedChapter(bibleVersionIsProtestant, selectedBookId, selectedChapter)
            isCurrentChapterCached = false
            loadCurrentChapterVerses()
        }
    }

    fun toggleCurrentChapterCache() {
        if (isCurrentChapterCached) {
            deleteCurrentChapterCache()
        } else {
            cacheCurrentChapter()
        }
    }

    fun cacheEntireBook(bookId: Int) {
        viewModelScope.launch {
            val book = BibleData.books.find { it.id == bookId } ?: return@launch
            val versesToCache = mutableListOf<CachedBibleVerse>()
            for (chap in 1..book.chaptersCount) {
                val generated = BibleData.generateVerses(bookId, chap)
                generated.forEach { (vNo, text) ->
                    versesToCache.add(
                        CachedBibleVerse(
                            versionIsProtestant = bibleVersionIsProtestant,
                            bookId = bookId,
                            bookName = book.name,
                            chapter = chap,
                            verseNumber = vNo,
                            text = text
                        )
                    )
                }
            }
            repository.insertCachedBibleVerses(versesToCache)
            loadCurrentChapterVerses()
        }
    }

    // --- Private Member Projects ---
    fun saveMemberProjects(projectsText: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(projects = projectsText)
            repository.insertMember(updated)
            _currentUser.value = updated
        }
    }

    // --- Budget / Balance Audit Sheets ---
    fun addBudgetTransaction() {
        val amount = budgetAmountInput.toDoubleOrNull()
        if (amount == null || amount <= 0 || budgetLabelInput.isBlank()) return
        val user = _currentUser.value ?: return

        // Formatted timestamp
        val simpleDate = "2026-06-04"
        viewModelScope.launch {
            repository.insertTransaction(
                type = budgetTypeInput,
                amount = amount,
                label = budgetLabelInput.trim(),
                date = simpleDate,
                authorName = user.name,
                authorPhone = user.phone
            )
            // Reset input
            budgetAmountInput = ""
            budgetLabelInput = ""
        }
    }

    // --- Forum Discussion actions ---
    fun addPost() {
        if (newPostContent.isBlank()) return
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.insertPost(
                authorName = user.name,
                authorPhone = user.phone,
                authorSampana = user.sampana,
                content = newPostContent.trim()
            )
            newPostContent = ""
        }
    }

    fun likePost(postId: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleLike(postId, user.phone)
        }
    }

    fun parseComments(commentsJson: String): List<Comment> {
        return repository.parseComments(commentsJson)
    }

    fun addComment(postId: Int) {
        if (newCommentContent.isBlank()) return
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.addComment(postId, user.name, user.phone, newCommentContent.trim())
            newCommentContent = ""
        }
    }

    // --- Quiz Bibliotheque & Question Pool ---
    private val quizPool = mapOf(
        "Mora" to listOf(
            QuizQuestion(1, "Hany zanak'i Abrahama tamin'i Saraha?", listOf("Ismaela", "Isaka", "Jakoba", "Seta"), "Isaka"),
            QuizQuestion(2, "Iza no hitan'i Farao tao anaty harona teo amin'ny renirano?", listOf("Noe", "Mosesy", "Davida", "Josoa"), "Mosesy"),
            QuizQuestion(3, "Firy ny mpianatr'i Jesosy voalohany voafidy?", listOf("10", "12", "70", "7"), "12"),
            QuizQuestion(4, "Taiza no nahaterahan'i Jesosy an-tany?", listOf("Betlehema", "Jerosalema", "Nazareta", "Roma"), "Betlehema"),
            QuizQuestion(5, "Iza no nanao ny sambofiara lehibe?", listOf("Solomona", "Mosesy", "Noe", "Davida"), "Noe")
        ),
        "Salasala" to listOf(
            QuizQuestion(1, "Iza no mpanjaka hendry nanoratra ny bokin'ny Ohabolana?", listOf("Davida", "Saoly", "Solomona", "Josia"), "Solomona"),
            QuizQuestion(2, "Firy andro no nianjeran'ny mandan'i Jeriko rehefa nodidinina?", listOf("3 andro", "7 andro", "40 andro", "10 andro"), "7 andro"),
            QuizQuestion(3, "Iza no anaran'ilay bokin'ny vehivavy Moabita nanaraka an'i Naomy?", listOf("Rota", "Estera", "Debora", "Saraha"), "Rota"),
            QuizQuestion(4, "Firy taona i Mosesy no maty teo amin'ny tendrombohitra Nebo?", listOf("80 taona", "120 taona", "100 taona", "150 taona"), "120 taona"),
            QuizQuestion(5, "Taiza no nisy ilay hazo feno voankazo voarara tao Edena?", listOf("Afovoan-tanàna", "Afovoan'ny saha", "Teo amoron'ny renirano", "Teo akaikin'ny varavarana"), "Afovoan'ny saha")
        ),
        "Sarotra" to listOf(
            QuizQuestion(1, "Iza no anaran'ilay mpaminany vavy niara-niadi tamin'i Baraka?", listOf("Debora", "Rota", "Jael", "Miriam"), "Debora"),
            QuizQuestion(2, "Mpanjakan'i Joda iza no nampitomboiny 15 taona ny androm-piainany?", listOf("Josia", "Manase", "Hezekia", "Saoly"), "Hezekia"),
            QuizQuestion(3, "Iza no anaran'ny lohasaha nitsanganan'ny masoandro tamin'ny alalan'i Josoa?", listOf("Gibeona", "Aialona", "Hebrona", "Sekema"), "Gibeona"),
            QuizQuestion(4, "Firy taona no nanjakan'i Davida tany Hebrona irery ihany?", listOf("7 taona sy tapany", "3 taona", "40 taona", "12 taona"), "7 taona sy tapany"),
            QuizQuestion(5, "Iza no anaran'ny rahalahin'i Davida lahimatoa nolavin'ny Tompo tsy hohosorana?", listOf("Eliaba", "Abinadaba", "Sama", "Sola"), "Eliaba")
        )
    )

    fun getActiveQuizQuestion(): QuizQuestion? {
        val pool = quizPool[selectedDifficulty] ?: return null
        val index = (quizActiveLevel + quizQuestionIndex - 1) % pool.size
        return pool[index]
    }

    fun startQuizGame() {
        isQuizGameOver = false
        isQuizLevelCompleted = false
        isQuizAnswerSubmitted = false
        quizQuestionIndex = 0
        quizScoreThisLevel = 0
        quizSelectedAnswer = null
        startQuestionTimer()
    }

    private fun startQuestionTimer() {
        timerJob?.cancel()
        quizCountdownSeconds = 30
        timerJob = viewModelScope.launch {
            while (quizCountdownSeconds > 0) {
                delay(1000)
                quizCountdownSeconds--
            }
            // Timeout -> Restart Level at 1
            triggerQuizTimeout()
        }
    }

    private fun triggerQuizTimeout() {
        isQuizGameOver = true
        quizActiveLevel = 1 // reset to Level 1
        timerJob?.cancel()
    }

    fun submitQuizAnswer(selectedOption: String) {
        if (isQuizAnswerSubmitted || isQuizGameOver) return
        timerJob?.cancel()
        quizSelectedAnswer = selectedOption
        isQuizAnswerSubmitted = true

        val q = getActiveQuizQuestion() ?: return
        if (selectedOption == q.correctAnswer) {
            quizScoreThisLevel++
        }
    }

    fun nextQuizQuestion() {
        quizSelectedAnswer = null
        isQuizAnswerSubmitted = false

        if (quizQuestionIndex < 4) {
            quizQuestionIndex++
            startQuestionTimer()
        } else {
            // Evaluated full level
            if (quizScoreThisLevel == 5) {
                // Perfect, advances points and levels!
                isQuizLevelCompleted = true
                quizActiveLevel++
                awardPoints(30) // +30 points for passing level!
            } else {
                // Failed, resets to level 1 as mandated!
                isQuizGameOver = true
                quizActiveLevel = 1
            }
        }
    }

    // --- Award Points to current user ---
    private fun awardPoints(qty: Int) {
        val user = _currentUser.value ?: return
        val currentPoints = user.points
        val nextPoints = maxOf(0, currentPoints + qty)
        viewModelScope.launch {
            val updated = user.copy(points = nextPoints)
            repository.insertMember(updated)
            _currentUser.value = updated
        }
    }

    // --- Wheel Spin Module ---
    fun spinBonusWheel() {
        if (isSpinning || currentSpinsLeft <= 0) return
        isSpinning = true
        spinOutcome = null
        spinPointsNotification = null
        activeSpinBibleVerse = null

        viewModelScope.launch {
            delay(2500) // Spin delay simulation
            currentSpinsLeft--

            val randomIndex = Random.nextInt(spinItems.size)
            val outcome = spinItems[randomIndex]
            spinOutcome = outcome

            when (outcome) {
                "+10 pts" -> {
                    awardPoints(10)
                    spinPointsNotification = "Azonao ny tombony +10 points!"
                }
                "+20 pts" -> {
                    awardPoints(20)
                    spinPointsNotification = "Azonao ny tombony +20 points!"
                }
                "+50 pts" -> {
                    awardPoints(50)
                    spinPointsNotification = "Azonao ny tombony +50 points!"
                }
                "Fo ❤️" -> {
                    awardPoints(15)
                    spinPointsNotification = "Feno fitiavana ny fonao! Horaisinao ny +15 points!"
                }
                "Baomba 💣" -> {
                    awardPoints(-20)
                    spinPointsNotification = "Oay! Tra-baomba 💣 ianao ka very -20 points."
                }
                "Baiboly 📖" -> {
                    // Pick beautiful randomized scripture
                    val scriptureVerses = listOf(
                        "\"Tompo ô, Ianao no heriko sy heriko ary fialofako amin'ny andro fahoriana.\" - Jeremia 16:19",
                        "\"Tena tsara sy mahafinaritra ny miara-monina ho an'ny mpirahalahy.\" - Salamo 133:1",
                        "\"Koa amin'izany, mifalia foana amin'ny Tompo!\" - Filipiana 4:4",
                        "\"Ny Finoana no antoky ny zavatra antenaina.\" - Hebreo 11:1"
                    )
                    activeSpinBibleVerse = scriptureVerses.random()
                    // Award 50 points as mandated: "Si le joueur obtient Bible, un verset s’affiche et après lecture, reçoive 50pts bonus"
                    awardPoints(50)
                    spinPointsNotification = "Mamaky tenin'Andriamanitra ianao! +50 points bonus azonao."
                }
            }
            isSpinning = false

            if (currentSpinsLeft == 0) {
                // start countdown timer for 15 minute cooldown simulation
                startSpinCooldown()
            }
        }
    }

    private fun startSpinCooldown() {
        viewModelScope.launch {
            spinCooldownSeconds = 15 * 60 // 15 mins
            while (spinCooldownSeconds > 0) {
                delay(1000)
                spinCooldownSeconds--
            }
            currentSpinsLeft = 5 // Reset spin capacity
        }
    }
}
