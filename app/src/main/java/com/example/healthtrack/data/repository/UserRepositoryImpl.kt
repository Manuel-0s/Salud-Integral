package com.example.healthtrack.data.repository

import com.example.healthtrack.domain.model.UserModel
import com.example.healthtrack.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserRepository {

    companion object {
        private const val USERS_COLLECTION = "usuarios"
        private var cachedUser: UserModel? = null
        private var cachedUid: String? = null
    }

    override suspend fun getUserData(): UserModel? {
        val currentUser = auth.currentUser ?: return null
        val uid = currentUser.uid

        if (cachedUser != null && cachedUid == uid) {
            return cachedUser
        }

        return try {
            val document = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            val user = if (document.exists()) {
                mapDocumentToUserModel(document, currentUser.email, currentUser.displayName)
            } else {
                val defaultUser = UserModel(
                    correo = currentUser.email ?: "",
                    nombre = currentUser.displayName ?: ""
                )
                try {
                    firestore.collection(USERS_COLLECTION).document(uid).set(defaultUser).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                defaultUser
            }
            cachedUser = user
            cachedUid = uid
            user
        } catch (e: Exception) {
            e.printStackTrace()
            val fallback = UserModel(
                correo = currentUser.email ?: "",
                nombre = currentUser.displayName ?: ""
            )
            fallback
        }
    }

    private fun mapDocumentToUserModel(
        document: DocumentSnapshot,
        authEmail: String?,
        authName: String?
    ): UserModel {
        try {
            val user = document.toObject(UserModel::class.java)
            if (user != null && user.nombre.isNotEmpty() && user.correo.isNotEmpty()) {
                return user
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val alertEmail = document.getString("alertEmail")
        val correo = document.getString("correo")
            ?: document.getString("email")
            ?: authEmail
            ?: ""
        val doctorAsignadoId = document.getString("doctorAsignadoId")
        val fechaNacimiento = document.getString("fechaNacimiento")
            ?: document.getString("fecha_nacimiento")
            ?: ""

        val height = document.getDouble("height")
            ?: document.getLong("height")?.toDouble()
            ?: document.getString("height")?.toDoubleOrNull()
            ?: document.getDouble("estatura")
            ?: document.getLong("estatura")?.toDouble()
            ?: document.getString("estatura")?.toDoubleOrNull()
            ?: 0.0

        val weight = document.getDouble("weight")
            ?: document.getLong("weight")?.toDouble()
            ?: document.getString("weight")?.toDoubleOrNull()
            ?: document.getDouble("peso")
            ?: document.getLong("peso")?.toDouble()
            ?: document.getString("peso")?.toDoubleOrNull()
            ?: 0.0

        val imc = document.getDouble("imc")
            ?: document.getLong("imc")?.toDouble()
            ?: document.getString("imc")?.toDoubleOrNull()
            ?: 0.0

        val maxGlucose = document.getLong("maxGlucose")?.toInt()
            ?: document.getDouble("maxGlucose")?.toInt()
            ?: document.getString("maxGlucose")?.toIntOrNull()
            ?: 0

        val maxHeartRate = document.getLong("maxHeartRate")?.toInt()
            ?: document.getDouble("maxHeartRate")?.toInt()
            ?: document.getString("maxHeartRate")?.toIntOrNull()
            ?: 0

        val maxPressureDiastolica = document.getLong("maxPressureDiastolica")?.toInt()
            ?: document.getDouble("maxPressureDiastolica")?.toInt()
            ?: document.getString("maxPressureDiastolica")?.toIntOrNull()
            ?: 0

        val maxPressureSistolica = document.getLong("maxPressureSistolica")?.toInt()
            ?: document.getDouble("maxPressureSistolica")?.toInt()
            ?: document.getString("maxPressureSistolica")?.toIntOrNull()
            ?: 0

        val nombre = document.getString("nombre")
            ?: document.getString("fullName")
            ?: document.getString("name")
            ?: authName
            ?: ""
        val password = document.getString("password") ?: ""
        val role = document.getString("role") ?: "PACIENTE"
        val usuario = document.getString("usuario") ?: document.getString("username")

        return UserModel(
            alertEmail = alertEmail,
            correo = correo,
            doctorAsignadoId = doctorAsignadoId,
            fechaNacimiento = fechaNacimiento,
            height = height,
            imc = imc,
            maxGlucose = maxGlucose,
            maxHeartRate = maxHeartRate,
            maxPressureDiastolica = maxPressureDiastolica,
            maxPressureSistolica = maxPressureSistolica,
            nombre = nombre,
            password = password,
            role = role,
            usuario = usuario,
            weight = weight
        )
    }

    override suspend fun updateStats(uid: String, updates: Map<String, Any>) {
        firestore.collection(USERS_COLLECTION).document(uid).set(updates, SetOptions.merge()).await()
        cachedUser = null
        cachedUid = null
    }

    override suspend fun updateAuthEmail(newEmail: String) {
        auth.currentUser?.verifyBeforeUpdateEmail(newEmail)?.await()
        cachedUser = null
        cachedUid = null
    }

    override fun logout() {
        auth.signOut()
        cachedUser = null
        cachedUid = null
    }
}
