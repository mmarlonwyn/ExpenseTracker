package com.example.expensetracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.expensetracker.model.Expense

class ExpenseViewModel : ViewModel() {

    val expenses = mutableStateListOf<Expense>()

    var name by mutableStateOf("")
        private set

    var amount by mutableStateOf("")
        private set

    var amountError by mutableStateOf<String?>(null)
        private set

    var editingId by mutableStateOf<Int?>(null)
        private set

    private var nextId = 1

    val total: Long
        get() = expenses.sumOf { it.amount }

    val canSubmit: Boolean
        get() = name.isNotBlank() && amount.isNotBlank() && amountError == null

    val isEditing: Boolean
        get() = editingId != null

    fun onNameChange(value: String) {
        name = value
    }

    fun onAmountChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(12)
        amount = digits
        amountError = validateAmount(digits)
    }

    private fun validateAmount(value: String): String? {
        if (value.isEmpty()) return null
        val number = value.toLongOrNull() ?: return "Invalid amount"
        if (number <= 0L) return "Amount must be greater than 0"
        return null
    }

    fun submit() {
        val error = validateAmount(amount)
        if (error != null) {
            amountError = error
            return
        }
        if (name.isBlank() || amount.isBlank()) return

        val value = amount.toLong()
        val currentEditId = editingId

        if (currentEditId == null) {
            expenses.add(Expense(nextId++, name.trim(), value))
        } else {
            val index = expenses.indexOfFirst { it.id == currentEditId }
            if (index >= 0) {
                expenses[index] = expenses[index].copy(name = name.trim(), amount = value)
            }
        }
        clearForm()
    }

    fun startEdit(expense: Expense) {
        editingId = expense.id
        name = expense.name
        amount = expense.amount.toString()
        amountError = null
    }

    fun cancelEdit() {
        clearForm()
    }

    fun delete(expense: Expense) {
        expenses.removeAll { it.id == expense.id }
        if (editingId == expense.id) clearForm()
    }

    private fun clearForm() {
        name = ""
        amount = ""
        amountError = null
        editingId = null
    }
}