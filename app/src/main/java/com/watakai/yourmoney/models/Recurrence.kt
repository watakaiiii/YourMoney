package com.watakai.yourmoney.models

sealed class Recurrence(val name: String, val target: String) {
  object None : Recurrence("Нет", "Нет")
  object Daily : Recurrence("Каждый день", "День")
  object Weekly : Recurrence("Каждую неделю", "Неделю")
  object Monthly : Recurrence("Каждый месяц", "Месяц")
  object Yearly : Recurrence("Каждый год", "Год")
}

fun String.toRecurrence(): Recurrence {
  return when(this) {
    "None" -> Recurrence.None
    "Daily" -> Recurrence.Daily
    "Weekly" -> Recurrence.Weekly
    "Monthly" -> Recurrence.Monthly
    "Yearly" -> Recurrence.Yearly
    else -> Recurrence.None
  }
}