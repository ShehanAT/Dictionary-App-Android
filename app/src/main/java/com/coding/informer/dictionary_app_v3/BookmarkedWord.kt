package com.coding.informer.dictionary_app_v3

import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
class BookmarkedWord {

    var id: Int? = null;
    var created_at: Timestamp? = null;
    var bookmarked_word: String? = null;
}