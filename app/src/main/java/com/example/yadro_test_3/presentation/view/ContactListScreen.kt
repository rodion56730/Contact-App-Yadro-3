package com.example.yadro_test_3.presentation.view

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.yadro_test_3.domain.model.Contact
import com.example.yadro_test_3.presentation.ContactViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(viewModel: ContactViewModel, lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current) {

    val groupedContacts by viewModel.groupedContacts.collectAsState(emptyMap())
    val status by viewModel.statusMessage.collectAsState()
    val context = LocalContext.current
    var visibleContacts by remember { mutableStateOf(emptyList<Contact>()) }
    val permission = Manifest.permission.READ_CONTACTS

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadContacts()
        } else {
            Toast.makeText(context, "Разрешение на доступ к контактам не предоставлено", Toast.LENGTH_SHORT).show()
        }
    }
    DisposableEffect(lifecycleOwner) {
        viewModel.registerLifecycle(lifecycleOwner.lifecycle)
        onDispose {
            viewModel.unregisterLifecycle(lifecycleOwner.lifecycle)
        }
    }



    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            viewModel.loadContacts()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    LaunchedEffect(groupedContacts) {
        if (groupedContacts.values.flatten().size < visibleContacts.size || groupedContacts.values.flatten().size > visibleContacts.size) {
            delay(300)
        }
        visibleContacts = groupedContacts.values.flatten()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            groupedContacts.forEach { (letter, contactsForLetter) ->
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp)
                    ) {
                        Text(
                            text = letter,
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 20.sp
                            ),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                items(
                    items = contactsForLetter,
                    key = { it.id }
                ) { contact ->
                    AnimatedVisibility(
                        visible = visibleContacts.contains(contact),
                        exit = fadeOut(animationSpec = tween(300)) +
                                shrinkVertically(animationSpec = tween(300)),
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                    ) {
                        ContactItem(contact = contact)
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.deleteDuplicates() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Delete duplicate contacts")
        }

        status?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearStatusMessage()
            }
        }
    }
}