package com.example.yadro_test_3.domain.repository

import com.example.yadro_test_3.domain.model.Contact

interface ContactRepository {
    fun getContacts(): List<Contact>
    fun deleteDuplicates(callback: (String) -> Unit)
}