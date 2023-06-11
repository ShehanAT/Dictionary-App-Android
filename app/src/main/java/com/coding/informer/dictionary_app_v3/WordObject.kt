package com.coding.informer.dictionary_app_v3

internal class WordObject(word: String, dateCreated: String) {

    var word: String = word
        get() = field
        set(value) { field = value }

    var dateCreated: String = dateCreated
        get() = field
        set(value) { field = value }
}