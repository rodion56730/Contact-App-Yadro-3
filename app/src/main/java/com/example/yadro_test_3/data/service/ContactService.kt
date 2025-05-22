package com.example.yadro_test_3.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import com.example.yadro_test_3.aidl.IContactService
import com.example.yadro_test_3.aidl.IDeleteCallback

class ContactService : Service() {
    private val binder = object : IContactService.Stub() {
        override fun deleteDuplicateContacts(callback: IDeleteCallback?) {
            Thread {
                try {
                    val resolver = contentResolver
                    val contactMap = mutableMapOf<String, MutableList<Long>>() // hash -> ids

                    val cursor = resolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                        ),
                        null,
                        null,
                        null
                    )

                    cursor?.use {
                        val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                        val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                        while (it.moveToNext()) {
                            val id = it.getLong(idIndex)
                            val name = it.getString(nameIndex)?.trim() ?: ""
                            val number = it.getString(numberIndex)?.replace("\\s".toRegex(), "") ?: ""
                            val key = "$name|$number"
                            contactMap.getOrPut(key) { mutableListOf() }.add(id)
                        }
                    }

                    val duplicates = contactMap.values.filter { it.size > 1 }

                    if (duplicates.isEmpty()) {
                        callback?.onComplete("Повторяющиеся контакты не найдены")
                        return@Thread
                    }

                    duplicates.forEach { group ->
                        group.drop(1).forEach { contactId ->
                            val uri = ContactsContract.RawContacts.CONTENT_URI
                            val deleted = resolver.delete(
                                uri,
                                "${ContactsContract.RawContacts.CONTACT_ID} = ?",
                                arrayOf(contactId.toString())
                            )
                            println(deleted)
                        }
                    }

                    callback?.onComplete("Повторяющиеся контакты удалены успешно")
                } catch (e: Exception) {
                    callback?.onComplete("Произошла ошибка")
                    e.message?.let { Log.e("ERROR", it) }
                }
            }.start()
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder
}
