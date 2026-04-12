package com.zyvault.app.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class BillItem(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val amount: String = "",
    val dueText: String = "",
    val bank: String = "",
    val status: String = "Pending",
    val statusColorHex: String = "#FF9800",
    val urgencyColorHex: String = "#FF9800",
    val category: String = "General"
)

class BillsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val _bills = MutableStateFlow<List<BillItem>>(emptyList())
    val bills: StateFlow<List<BillItem>> = _bills

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchBills()
    }

    fun fetchBills() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("bills")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                val billsList = snapshot.documents.map { doc ->
                    doc.toObject(BillItem::class.java)!!.copy(id = doc.id)
                }
                _bills.value = billsList
                
                // Update home stats
                firestore.collection("users").document(userId).update("billsDueCount", billsList.size)
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun processBillImage(context: Context, uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val image = InputImage.fromFilePath(context, uri)
                val result = recognizer.process(image).await()
                val fullText = result.text

                // Simple extraction logic
                var extractedAmount = ""
                var extractedName = "New Bill"
                var extractedDate = "Pending"

                val lines = fullText.split("\n")
                
                // Look for Currency patterns
                val amountRegex = Regex("""\$?\d{1,3}(,\d{3})*(\.\d{2})?""")
                extractedAmount = amountRegex.find(fullText)?.value ?: ""

                // Look for Date patterns
                val dateRegex = Regex("""\d{1,2}/\d{1,2}/\d{2,4}""")
                extractedDate = dateRegex.find(fullText)?.value ?: "Due soon"

                // First line is often the vendor
                if (lines.isNotEmpty()) {
                    extractedName = lines[0].take(20)
                }

                val newBill = BillItem(
                    userId = userId,
                    name = extractedName,
                    amount = extractedAmount,
                    dueText = "Due $extractedDate",
                    status = "Detected",
                    statusColorHex = "#1A73E8"
                )

                firestore.collection("bills").add(newBill).await()
                fetchBills()
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBill(billId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("bills").document(billId).delete().await()
                fetchBills()
            } catch (e: Exception) {}
        }
    }

    fun resetBills() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("bills").whereEqualTo("userId", userId).get().await()
                snapshot.documents.forEach { it.reference.delete() }
                fetchBills()
            } catch (e: Exception) {}
        }
    }
}
