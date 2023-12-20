package com.example.foxbook

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var data = MutableLiveData<ArrayList<Any>>()
}