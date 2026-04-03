package com.example.whizzz.domain.model

/**
 * Minimal authenticated user snapshot for the domain layer (Firebase Auth → domain mapping).
 *
 * @property uid Stable user identifier from the identity provider.
 * @property email Primary email when available; may be null for some providers or states.
 *
 * @author udit
 */
data class AuthUser(
    val uid: String,
    val email: String?,
)
