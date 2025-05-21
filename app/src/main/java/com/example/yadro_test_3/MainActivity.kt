package com.example.yadro_test_3

import com.example.yadro_test_3.aidl.IContactService
import com.example.yadro_test_3.aidl.IDeleteCallback
import com.example.yadro_test_3.presentation.view.ContactListScreen
import com.example.yadro_test_3.ui.theme.Yadro_test_3Theme


import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import com.example.yadro_test_3.data.repository.ContactRepositoryImpl
import com.example.yadro_test_3.domain.repository.ContactRepository
import com.example.yadro_test_3.domain.usecase.DeleteDuplicateContactsUseCase
import com.example.yadro_test_3.domain.usecase.GetContactsUseCase
import com.example.yadro_test_3.presentation.ContactViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ContactRepositoryImpl(applicationContext)
        val getContactsUseCase = GetContactsUseCase(repository)
        val deleteDuplicatesUseCase = DeleteDuplicateContactsUseCase(repository)
        val viewModel = ContactViewModel(getContactsUseCase, deleteDuplicatesUseCase)

        setContent {
            MaterialTheme {
                ContactListScreen(viewModel = viewModel)
            }
        }
    }
}
