package com.example.myreviewmanager.data

object UserManager {
    // VARIÁVEL PARA MANTER O ID DO USUÁRIO LOGADO
    var currentUserId: Long? = null
        private set

    // VARIÁVEL PARA MANTER O NOME/E-MAIL DO USUÁRIO LOGADO
    var currentUserName: String? = null
        private set

    /**
     * Define o usuário logado com seu ID e Nome/E-mail.
     */
    fun setLoggedInUser(userId: Long, userName: String) {
        currentUserId = userId
        currentUserName = userName
    }

    fun logout() {
        currentUserId = null
        currentUserName = null
    }

    // Retorna o ID do usuário (com exceção se for nulo)
    val requireUserId: Long
        get() = currentUserId ?: throw IllegalStateException("User ID must be set before accessing reviews.")

    // Retorna o nome/e-mail do usuário (ou "Usuário" como fallback seguro)
    val requireUserName: String
        get() = currentUserName ?: "Usuário"
}