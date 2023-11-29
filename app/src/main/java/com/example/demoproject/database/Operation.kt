package com.example.demoproject.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


data class Item(val name: String, val age: String, val caseNumber: String)
var loading=false
val database: FirebaseDatabase = FirebaseDatabase.getInstance()
val itemsRef: DatabaseReference = database.getReference("Patient Details")

// Create operation
fun newPatient(item: Item) {
    loading=true
    val newItemRef = itemsRef.push()
    newItemRef.setValue(item)
    loading=false
}

// Read operation
fun readItems() {
    itemsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (data in snapshot.children) {
                val item = data.getValue(Item::class.java)
                // Handle retrieved item
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })
}

// Update operation
fun updateItem(itemId: String, newName: String, newDescription: String) {
    val itemUpdates = mapOf<String, Any>(
        "/$itemId/name" to newName,
        "/$itemId/description" to newDescription
    )

    itemsRef.updateChildren(itemUpdates)
}

// Delete operation
fun deleteItem(itemId: String) {
    itemsRef.child(itemId).removeValue()
}
