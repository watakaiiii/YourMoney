package com.watakai.yourmoney.models

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var username: String = "Пользователь"
    var level: Int = 1
    var progress: Float = 0f
    var toNextLevel: Float = 5f
    var achievements: Int = 0
    var lastAchievement: Achievement? = null
    var expenses: List<Expense> = listOf()

    constructor(
        username: String,
        level: Int,
        progress: Float,
        toNextLevel: Float,
        achievements: Int,
        lastAchievement: Achievement?,
        expenses: List<Expense>
    ) : this() {
        this.username = username
        this.level = level
        this.progress = progress
        this.toNextLevel = toNextLevel
        this.achievements = achievements
        this.lastAchievement = lastAchievement
        this.expenses = expenses
    }
}
