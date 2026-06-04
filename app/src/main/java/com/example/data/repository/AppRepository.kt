package com.example.data.repository

import com.example.data.dao.AppDao
import com.example.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val commentsListType = Types.newParameterizedType(List::class.java, Comment::class.java)
    private val commentsAdapter = moshi.adapter<List<Comment>>(commentsListType)

    val membersFlow: Flow<List<Member>> = appDao.getAllMembersFlow()
    val dailyVerseFlow: Flow<DailyVerse?> = appDao.getDailyVerseFlow()
    val postsFlow: Flow<List<DiscussionPost>> = appDao.getAllPostsFlow()
    val transactionsFlow: Flow<List<BudgetTransaction>> = appDao.getAllTransactionsFlow()

    suspend fun initializeDatabaseIfEmpty() {
        val count = appDao.getMembersCount()
        if (count == 0) {
            // Add default admin (Mpitantana) - Exactly as depicted on Photo 3
            val admin = Member(
                phone = "0342994417",
                name = "ADMIN",
                address = "Lot 26 ter mahamasina",
                sampana = "Sampana Tanora Kristiana (STK)",
                status = "APPROVED",
                isAdmin = true,
                photoUrl = "admin_photo",
                projects = "1. Drafitra hanatsarana ny fanamafisam-peo fiangonana.\n2. Fikarakarana ny fivoriamben'ny sampana STK anio hariva.",
                points = 65
            )
            appDao.insertMember(admin)

            // Add default member (Mpikambana APPROVED)
            val member = Member(
                phone = "0341234567",
                name = "Randria Jean",
                address = "Lot III B Toliara",
                sampana = "Sampana Tanora Kristiana (STK)",
                status = "APPROVED",
                isAdmin = false,
                photoUrl = "member_photo",
                projects = "1. Fianarana mitendry gitara ho an'ny amboarampeo\n2. Fanohanana ny kilalaon'ny ankizy",
                points = 65
            )
            appDao.insertMember(member)

            // Add default member (Mpikambana PENDING)
            val pendingMember = Member(
                phone = "0349876543",
                name = "Rakoto Soa",
                address = "Lot IV G Fianarantsoa",
                sampana = "Sampana Vehivavy Lovasoa (SVL)",
                status = "PENDING",
                isAdmin = false,
                photoUrl = "pending_photo",
                projects = "1. Famolavolana fitaizana ara-panahy ny ankizy madinika",
                points = 10
            )
            appDao.insertMember(pendingMember)

            // Add initial daily verse
            val verse = DailyVerse(
                id = 1,
                text = "Mifohaza, ry ilay matory, ary mitsangàna amin'ny maty, fa hampahazava anao i Kristy.",
                reference = "Efesiana 5:14"
            )
            appDao.insertDailyVerse(verse)

            // Add initial Transactions for consultative budget
            appDao.insertTransaction(
                BudgetTransaction(
                    type = "RECETTE",
                    amount = 450000.0,
                    label = "Rakitra Alahady 24 May",
                    dateString = "2026-05-24",
                    authorName = "ADMIN",
                    authorPhone = "0342994417"
                )
            )
            appDao.insertTransaction(
                BudgetTransaction(
                    type = "DEPENSE",
                    amount = 120000.0,
                    label = "Hofan'ny fanamafisam-peo fivoriana",
                    dateString = "2026-05-28",
                    authorName = "ADMIN",
                    authorPhone = "0342994417"
                )
            )
            appDao.insertTransaction(
                BudgetTransaction(
                    type = "RECETTE",
                    amount = 600000.0,
                    label = "Rakitra fahasoavana fankalazana",
                    dateString = "2026-05-31",
                    authorName = "Randria Jean",
                    authorPhone = "0341234567"
                )
            )

            // Add initial discussion posts
            val commentsList = listOf(
                Comment("Randria Jean", "0341234567", "Tena mahafinaritra io fihaonana io!", System.currentTimeMillis() - 7200000)
            )
            val commentsJsonString = commentsAdapter.toJson(commentsList)

            appDao.insertPost(
                DiscussionPost(
                    authorName = "ADMIN",
                    authorPhone = "0342994417",
                    authorSampana = "Sampana Tanora Kristiana (STK)",
                    content = "Miandry antsika rehetra amin'ny alahady hariva amin'ny fivoriana lehibe mampahery ny finoana. Tongava maro!",
                    timestamp = System.currentTimeMillis() - 86400000,
                    likesCount = 2,
                    commentsJson = commentsJsonString
                )
            )
        }
    }

    suspend fun getMemberByPhone(phone: String) = appDao.getMemberByPhone(phone)
    fun getMemberByPhoneFlow(phone: String): Flow<Member?> = appDao.getMemberByPhoneFlow(phone)
    suspend fun insertMember(member: Member) = appDao.insertMember(member)
    suspend fun updateMember(member: Member) = appDao.updateMember(member)

    suspend fun updateDailyVerse(text: String, reference: String) {
        val verse = DailyVerse(id = 1, text = text, reference = reference)
        appDao.insertDailyVerse(verse)
    }

    suspend fun insertPost(authorName: String, authorPhone: String, authorSampana: String, content: String) {
        val post = DiscussionPost(
            authorName = authorName,
            authorPhone = authorPhone,
            authorSampana = authorSampana,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        appDao.insertPost(post)
    }

    suspend fun toggleLike(postId: Int, memberPhone: String) {
        val post = appDao.getPostById(postId) ?: return
        val likedList = post.likedByPhones.split(",").filter { it.isNotEmpty() }.toMutableList()
        val isLiked = likedList.contains(memberPhone)
        if (isLiked) {
            likedList.remove(memberPhone)
        } else {
            likedList.add(memberPhone)
        }
        val updatedLikesCount = likedList.size
        val updatedLikedByPhones = likedList.joinToString(",")

        val updatedPost = post.copy(
            likesCount = updatedLikesCount,
            likedByPhones = updatedLikedByPhones
        )
        appDao.updatePost(updatedPost)
    }

    fun parseComments(commentsJson: String): List<Comment> {
        return try {
            commentsAdapter.fromJson(commentsJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addComment(postId: Int, authorName: String, authorPhone: String, content: String) {
        val post = appDao.getPostById(postId) ?: return
        val currentComments = parseComments(post.commentsJson).toMutableList()
        currentComments.add(Comment(authorName, authorPhone, content, System.currentTimeMillis()))
        val updatedJson = commentsAdapter.toJson(currentComments)

        val updatedPost = post.copy(
            commentsJson = updatedJson
        )
        appDao.updatePost(updatedPost)
    }

    suspend fun insertTransaction(type: String, amount: Double, label: String, date: String, authorName: String, authorPhone: String) {
        val t = BudgetTransaction(
            type = type,
            amount = amount,
            label = label,
            dateString = date,
            authorName = authorName,
            authorPhone = authorPhone
        )
        appDao.insertTransaction(t)
    }

    suspend fun deleteTransaction(transaction: BudgetTransaction) {
        appDao.deleteTransaction(transaction)
    }
}
