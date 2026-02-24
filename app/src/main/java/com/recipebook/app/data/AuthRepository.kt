package com.recipebook.app.data

import com.recipebook.app.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun signOut()
}
