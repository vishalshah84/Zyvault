package com.zyvault.app.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FieldValue
import com.zyvault.app.data.model.Document
import com.zyvault.app.data.ocr.OCRProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class VaultViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _documents = MutableStateFlow<List<Document>>(emptyList())
    val documents: StateFlow<List<Document>> = _documents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchDocuments()
    }

    fun fetchDocuments() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("documents")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                
                val docs = snapshot.toObjects(Document::class.java)
                _documents.value = docs
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadDocument(uri: Uri, context: Context) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Perform OCR
                val ocr = OCRProcessor(context)
                val fields = ocr.processImage(uri)
                
                val name = fields["name"] ?: fields["type"] ?: "New Document"
                val category = when (fields["type"]) {
                    "Driver's License" -> "IDs"
                    "Passport" -> "IDs"
                    else -> "Personal"
                }
                val expiry = fields["expiry"] ?: "Jan 2030"

                val fileId = UUID.randomUUID().toString()
                val ref = storage.reference.child("documents/$userId/$fileId")
                
                ref.putFile(uri).await()
                val downloadUrl = ref.downloadUrl.await().toString()

                val newDoc = Document(
                    id = fileId,
                    userId = userId,
                    name = name,
                    category = category,
                    expiryDate = expiry,
                    fileUrl = downloadUrl
                )

                firestore.collection("documents").document(fileId).set(newDoc).await()
                
                // Update user document count
                firestore.collection("users").document(userId)
                    .update("documentCount", FieldValue.increment(1))
                    .await()

                fetchDocuments()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
