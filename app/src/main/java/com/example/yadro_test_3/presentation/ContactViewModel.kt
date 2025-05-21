package com.example.yadro_test_3.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yadro_test_3.domain.model.Contact
import com.example.yadro_test_3.domain.usecase.DeleteDuplicateContactsUseCase
import com.example.yadro_test_3.domain.usecase.GetContactsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ContactViewModel(
    private val getContactsUseCase: GetContactsUseCase,
    private val deleteDuplicatesUseCase: DeleteDuplicateContactsUseCase
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> get() = _contacts
    val groupedContacts = _contacts.map { contacts ->
        contacts.groupBy {
            it.displayName.firstOrNull()?.uppercase() ?: "#"
        }.toSortedMap()
    }
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> get() = _statusMessage

    fun loadContacts() {
        viewModelScope.launch {
            _contacts.value = getContactsUseCase()
        }
    }

    fun deleteDuplicates() {
        deleteDuplicatesUseCase.deleteDuplicates { result ->
            _statusMessage.value = result
            loadContacts() // reload updated list
        }
    }
}
