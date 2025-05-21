package com.example.yadro_test_3.domain.usecase

import com.example.yadro_test_3.domain.model.Contact
import com.example.yadro_test_3.domain.repository.ContactRepository

class GetContactsUseCase(private val repository: ContactRepository) {
    operator fun invoke(): List<Contact> = repository.getContacts()
}
