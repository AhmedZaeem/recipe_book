package cloud.azaeem.recipe.data

import cloud.azaeem.recipe.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun signOut()
}
