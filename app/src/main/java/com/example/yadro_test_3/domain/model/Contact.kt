package com.example.yadro_test_3.domain.model

import android.net.Uri


data class Contact(
    val id: String,
    val displayName: String,
    val phoneNumber: String,
    val photoUri: Uri? = null
)