package com.watakai.yourmoney.models

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Achievement(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var order: Int = 0
    var name: String = "Название достижения"
    var description: String = "Описание достижения"
    var progress: Float = 0f
    var toEnd: Float = 10f
    var isUnlocked: Boolean = false

    constructor(
        order: Int,
        name: String,
        description: String,
        progress: Float,
        toEnd: Float,
        isUnlocked: Boolean,
    ) : this() {
        this.order = order
        this.name = name
        this.description = description
        this.progress = progress
        this.toEnd = toEnd
        this.isUnlocked = isUnlocked
    }
}
