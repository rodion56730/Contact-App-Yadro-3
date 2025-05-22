package com.example.yadro_test_3.data.repository


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.provider.ContactsContract
import androidx.core.net.toUri
import com.example.yadro_test_3.aidl.IDeleteCallback
import com.example.yadro_test_3.aidl.IContactService
import com.example.yadro_test_3.data.service.ContactService
import com.example.yadro_test_3.domain.model.Contact
import com.example.yadro_test_3.domain.repository.ContactRepository

class ContactRepositoryImpl(private val context: Context) : ContactRepository {

    override fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val resolver = context.contentResolver

        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.TYPE} = ?",
            arrayOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE.toString()),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )



        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
            val accountTypeIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET)
            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                val photoUri = it.getString(photoIndex)
                val accountType = it.getString(accountTypeIndex)

                if (accountType == null || accountType == "com.android.contacts")
                    contacts.add(Contact(id, name, number, photoUri?.toUri()))
            }
        }
        return contacts
    }

    override fun deleteDuplicates(callback: (String) -> Unit) {
        val intent = Intent(context, ContactService::class.java)

        lateinit var serviceConnection: ServiceConnection

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val contactService = IContactService.Stub.asInterface(service)
                contactService?.deleteDuplicateContacts(object : IDeleteCallback.Stub() {
                    override fun onComplete(status: String?) {
                        callback(status ?: "Unknown result")
                        context.unbindService(serviceConnection) // корректно работает
                    }
                })
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }

        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}

