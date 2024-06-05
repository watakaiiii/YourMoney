package com.watakai.yourmoney

import com.watakai.yourmoney.models.Achievement
import com.watakai.yourmoney.models.Category
import com.watakai.yourmoney.models.Expense
import com.watakai.yourmoney.models.User
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

val config = RealmConfiguration.create(schema = setOf(Expense::class, Category::class, User::class, Achievement::class))
val db: Realm = Realm.open(config)