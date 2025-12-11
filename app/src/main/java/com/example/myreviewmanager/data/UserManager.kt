package com.example.myreviewmanager.data// Você pode usar 'util' ou 'data'

object UserManager {
    // VARIÁVEL PARA MANTER O ID DO USUÁRIO LOGADO
    // O tipo deve ser Long, pois presumimos que é o mesmo tipo usado na sua entidade User.
    var currentUserId: Long? = null
        private set // Apenas esta classe pode alterar o ID

    fun setLoggedInUser(userId: Long) {
        currentUserId = userId
    }

    fun logout() {
        currentUserId = null
    }

    // Se o ID do usuário nunca for nulo, a aplicação deve falhar
    val requireUserId: Long
        get() = currentUserId ?: throw IllegalStateException("User ID must be set before accessing reviews.")
}