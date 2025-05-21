package com.example.yadro_test_3.aidl;


import com.example.yadro_test_3.aidl.IDeleteCallback;

interface IContactService {
    void deleteDuplicateContacts(IDeleteCallback callback);
}
