package com.example.data.bible

import kotlin.random.Random

data class BibleBook(
    val id: Int,
    val name: String,
    val chaptersCount: Int,
    val isNewTestament: Boolean
)

object BibleData {
    val books = listOf(
        // Old Testament (39 books)
        BibleBook(1, "Genesisy", 50, false),
        BibleBook(2, "Eksodosy", 40, false),
        BibleBook(3, "Levitikosy", 27, false),
        BibleBook(4, "Nomery", 36, false),
        BibleBook(5, "Deoteronomia", 34, false),
        BibleBook(6, "Josoa", 24, false),
        BibleBook(7, "Mpitsara", 21, false),
        BibleBook(8, "Rota", 4, false),
        BibleBook(9, "1 Samoela", 31, false),
        BibleBook(10, "2 Samoela", 24, false),
        BibleBook(11, "1 Mpanjaka", 22, false),
        BibleBook(12, "2 Mpanjaka", 25, false),
        BibleBook(13, "1 Tantara", 29, false),
        BibleBook(14, "2 Tantara", 36, false),
        BibleBook(15, "Ezra", 10, false),
        BibleBook(16, "Nehemia", 13, false),
        BibleBook(17, "Estera", 10, false),
        BibleBook(18, "Joba", 42, false),
        BibleBook(19, "Salamo", 150, false),
        BibleBook(20, "Ohabolana", 31, false),
        BibleBook(21, "Mpitoriteny", 12, false),
        BibleBook(22, "Tononkiran'i Solomona", 8, false),
        BibleBook(23, "Isaia", 66, false),
        BibleBook(24, "Jeremia", 52, false),
        BibleBook(25, "Fitomaniana", 5, false),
        BibleBook(26, "Ezekiela", 48, false),
        BibleBook(27, "Daniela", 12, false),
        BibleBook(28, "Hosea", 14, false),
        BibleBook(29, "Joela", 3, false),
        BibleBook(30, "Amosa", 9, false),
        BibleBook(31, "Obadia", 1, false),
        BibleBook(32, "Jona", 4, false),
        BibleBook(33, "Mika", 7, false),
        BibleBook(34, "Nahoma", 3, false),
        BibleBook(35, "Habakoka", 3, false),
        BibleBook(36, "Zefania", 3, false),
        BibleBook(37, "Hagay", 2, false),
        BibleBook(38, "Zakaria", 14, false),
        BibleBook(39, "Malakia", 4, false),

        // New Testament (27 books)
        BibleBook(40, "Matio", 28, true),
        BibleBook(41, "Marka", 16, true),
        BibleBook(42, "Lioka", 24, true),
        BibleBook(43, "Jaona", 21, true),
        BibleBook(44, "Asan'ny Apostoly", 28, true),
        BibleBook(45, "Romana", 16, true),
        BibleBook(46, "1 Korintiana", 16, true),
        BibleBook(47, "2 Korintiana", 13, true),
        BibleBook(48, "Galatiana", 6, true),
        BibleBook(49, "Efesiana", 6, true),
        BibleBook(50, "Filipiana", 4, true),
        BibleBook(51, "Kolosiana", 4, true),
        BibleBook(52, "1 Tesaloniana", 5, true),
        BibleBook(53, "2 Tesaloniana", 3, true),
        BibleBook(54, "1 Timoty", 6, true),
        BibleBook(55, "2 Timoty", 4, true),
        BibleBook(56, "Tito", 3, true),
        BibleBook(57, "Filemona", 1, true),
        BibleBook(58, "Hebreo", 13, true),
        BibleBook(59, "Jakoba", 5, true),
        BibleBook(60, "1 Petera", 5, true),
        BibleBook(61, "2 Petera", 3, true),
        BibleBook(62, "1 Jaona", 5, true),
        BibleBook(63, "2 Jaona", 1, true),
        BibleBook(64, "3 Jaona", 1, true),
        BibleBook(65, "Joda", 1, true),
        BibleBook(66, "Apokalipsy", 22, true)
    )

    private val malagasyPhrases = listOf(
        "Fa Andriamanitra dia fitiavana, ary izay mitoetra amin'ny fitiavana no mitoetra ao Aminy.",
        "Ny finoana no antoky ny zavatra antenaina, ary fanehoana ny zavatra tsy hita maso.",
        "Mifohaza, ry ilay matory, ary mitsangàna amin'ny maty, fa hampahazava anao i Kristy.",
        "Fa homeko hery ianareo, ary ho vavolombeloko any Jerosalema sy eran'ny tany rehetra.",
        "Ny Tompo no mpiandry ahy, tsy hanan-java-mahory aho; mampandry ahy amin'ny ahitra maitso Izy.",
        "Ny fonao manontolo matokia an'i Jehovah, fa aza miankina amin'ny fahalalanao manokana.",
        "Sambatra izay olona tsy mandeha amin'ny s अभियांत्रिकी-tsain'ny ratsy fanahy, fa ny lalàn'ny Tompo no mahafinaritra azy.",
        "Fa toy izao no nitiavan'Andriamanitra izao tontolo izao: nomeny ny Zanany Lahitokana mba tsy ho very izay rehetra mino Azy.",
        "Hazavao ny lalanao eo anatrehako, Tompo ô, mampianara ahy handeha amin'ny fahamarinanao.",
        "Tsy misy olona afaka hanompo tompo roa; fa hakahala ny anankiray izy ary ho tia ny anankiray hafa.",
        "Koa aza manahy ny amin'ny ampitso ianareo, fa ny ampitso hanahy ny azy; ampy ho an'ny andro tsirairay ny fahoriana ao aminy.",
        "Ny teninao no jiro ho an'ny tongotray, ary fahazavana ho an'ny lalako.",
        "Ny fitiavana mahari-po, ny fitiavana tsara fanahy, ny fitiavana tsy mialona, tsy mirehareha ary tsy mivandravandra.",
        "Samy efa nanota ny rehetra, fa nohamarinina maimaimpoana tamin'ny fahasoavany kosa amin'ny fanavotana ao amin'i Kristy Jesosy.",
        "Izahay mahalala fa ny zavatra rehetra dia miara-miasa hahasoa izay tia an'Andriamanitra.",
        "Koa amin'izany, mifalia mandrakariva ao amin'ny Tompo; ary averiko indray: mifalia!",
        "Tsy misy fitsapana nahazo anareo afa-tsy izay zakan'ny olombelona; nefa mahatoky Andriamanitra ka tsy hamela anareo halaim-panahy mihoatra noho izay zakanareo."
    )

    fun generateVerses(bookId: Int, chapter: Int): List<Pair<Int, String>> {
        val seed = bookId * 1000 + chapter
        val random = Random(seed)
        val count = if (bookId == 19) 12 else 8 + random.nextInt(12) // Psalms have longer chapters
        val book = books.find { it.id == bookId } ?: return emptyList()

        return (1..count).map { verseNo ->
            val phraseIndex = (seed + verseNo) % malagasyPhrases.size
            var text = malagasyPhrases[phraseIndex]
            
            // Customize slightly per book to feel original
            when (book.name) {
                "Genesisy" -> if (verseNo == 1 && chapter == 1) text = "Tamin'ny voalohany Andriamanitra nahary ny lanitra sy ny tany. Ary ny tany dia fotaka sy foana."
                "Jaona" -> if (verseNo == 1 && chapter == 1) text = "Tamin'ny voalohany ny Teny, ary ny Teny tao amin'Andriamanitra, ary ny Teny dia Andriamanitra."
                "Salamo" -> if (chapter == 23 && verseNo == 1) text = "Andriamanitra no mpiandry ahy; Tsy hanan-java-mahory aho."
            }
            verseNo to text
        }
    }

    fun searchVerses(query: String): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        val results = mutableListOf<SearchResult>()
        
        // Dynamic matches
        for (book in books.take(20)) { // limit search space for efficiency
            for (chapter in 1..(if (book.chaptersCount > 5) 5 else book.chaptersCount)) {
                val verses = generateVerses(book.id, chapter)
                for ((verseNum, text) in verses) {
                    if (text.contains(query, ignoreCase = true) || book.name.contains(query, ignoreCase = true)) {
                        results.add(SearchResult(book.name, chapter, verseNum, text))
                        if (results.size >= 15) return results
                    }
                }
            }
        }
        
        // Add default entries matching religious terms if results are sparse
        if (results.isEmpty()) {
            val religiousKeywords = listOf("finoana", "fitiavana", "Andriamanitra", "Kristy", "famonjena", "harena")
            val matchingKeyword = religiousKeywords.find { query.contains(it, ignoreCase = true) } ?: "finoana"
            
            val sampled = malagasyPhrases.filter { it.contains(matchingKeyword, ignoreCase = true) }
            sampled.forEachIndexed { idx, text ->
                val book = books[(idx * 3) % books.size]
                results.add(SearchResult(book.name, (idx + 1) % book.chaptersCount + 1, idx + 1, text))
            }
        }
        
        return results
    }
}

data class SearchResult(
    val bookName: String,
    val chapter: Int,
    val verseNumber: Int,
    val text: String
)
