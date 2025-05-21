package com.example.yadro_test_3.domain.usecase

import com.example.yadro_test_3.domain.repository.ContactRepository

class DeleteDuplicateContactsUseCase(private val repository: ContactRepository) {
    fun deleteDuplicates(callback: (String) -> Unit) {
        repository.deleteDuplicates(callback)
    }
}

