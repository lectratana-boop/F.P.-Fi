package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Member Operations ---
    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembersFlow(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE phone = :phone LIMIT 1")
    suspend fun getMemberByPhone(phone: String): Member?

    @Query("SELECT * FROM members WHERE phone = :phone LIMIT 1")
    fun getMemberByPhoneFlow(phone: String): Flow<Member?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member)

    @Update
    suspend fun updateMember(member: Member)

    @Query("SELECT COUNT(*) FROM members")
    suspend fun getMembersCount(): Int

    // --- Daily Verse Operations ---
    @Query("SELECT * FROM daily_verse WHERE id = 1 LIMIT 1")
    fun getDailyVerseFlow(): Flow<DailyVerse?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyVerse(verse: DailyVerse)

    // --- Discussions Operations ---
    @Query("SELECT * FROM discussions ORDER BY timestamp DESC")
    fun getAllPostsFlow(): Flow<List<DiscussionPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: DiscussionPost)

    @Update
    suspend fun updatePost(post: DiscussionPost)

    @Query("SELECT * FROM discussions WHERE id = :id LIMIT 1")
    suspend fun getPostById(id: Int): DiscussionPost?

    // --- Transactions Operations ---
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAllTransactionsFlow(): Flow<List<BudgetTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: BudgetTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: BudgetTransaction)

    // --- Cached Bible Verses Operations ---
    @Query("SELECT * FROM cached_bible_verses WHERE versionIsProtestant = :isProtestant AND bookId = :bookId AND chapter = :chapter ORDER BY verseNumber ASC")
    suspend fun getCachedBibleVerses(isProtestant: Boolean, bookId: Int, chapter: Int): List<CachedBibleVerse>

    @Query("SELECT * FROM cached_bible_verses WHERE versionIsProtestant = :isProtestant AND bookId = :bookId AND chapter = :chapter ORDER BY verseNumber ASC")
    fun getCachedBibleVersesFlow(isProtestant: Boolean, bookId: Int, chapter: Int): Flow<List<CachedBibleVerse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedBibleVerses(verses: List<CachedBibleVerse>)

    @Query("DELETE FROM cached_bible_verses WHERE versionIsProtestant = :isProtestant AND bookId = :bookId AND chapter = :chapter")
    suspend fun deleteCachedChapter(isProtestant: Boolean, bookId: Int, chapter: Int)

    @Query("SELECT COUNT(*) FROM cached_bible_verses WHERE versionIsProtestant = :isProtestant AND bookId = :bookId AND chapter = :chapter")
    suspend fun getCachedVersesCount(isProtestant: Boolean, bookId: Int, chapter: Int): Int

    @Query("SELECT DISTINCT (bookId || '-' || chapter) FROM cached_bible_verses WHERE versionIsProtestant = :isProtestant")
    fun getCachedChapterKeysFlow(isProtestant: Boolean): Flow<List<String>>
}
