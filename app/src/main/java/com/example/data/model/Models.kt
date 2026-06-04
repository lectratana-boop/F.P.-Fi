package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey val phone: String, // Phone serving as unique identifier/Primary Key
    val name: String,
    val address: String,
    val sampana: String, // E.g., "Sampana Tanora Kristiana (STK)"
    val status: String,  // "PENDING", "APPROVED", "REJECTED"
    val isAdmin: Boolean,
    val photoUrl: String = "",
    val projects: String = "", // Personal projects notes
    val points: Int = 65 // Starting or active score
)

@Entity(tableName = "daily_verse")
data class DailyVerse(
    @PrimaryKey val id: Int = 1,
    val text: String,
    val reference: String, // E.g., "Isaia 60:1"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "discussions")
data class DiscussionPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorPhone: String,
    val authorSampana: String,
    val content: String,
    val timestamp: Long,
    val likesCount: Int = 0,
    val likedByPhones: String = "", // Comma-separated list of phone numbers that liked this
    val commentsJson: String = "[]" // JSON representation of list of comments
)

data class Comment(
    val authorName: String,
    val authorPhone: String,
    val content: String,
    val timestamp: Long
)

@Entity(tableName = "transactions")
data class BudgetTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "RECETTE" or "DEPENSE"
    val amount: Double,
    val label: String,
    val dateString: String,
    val authorName: String,
    val authorPhone: String
)

@Entity(
    tableName = "cached_bible_verses",
    primaryKeys = ["versionIsProtestant", "bookId", "chapter", "verseNumber"]
)
data class CachedBibleVerse(
    val versionIsProtestant: Boolean,
    val bookId: Int,
    val bookName: String,
    val chapter: Int,
    val verseNumber: Int,
    val text: String
)
