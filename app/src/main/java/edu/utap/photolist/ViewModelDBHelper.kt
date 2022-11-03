package edu.utap.photolist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.photolist.model.PhotoMeta

class ViewModelDBHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "allPhotos"

    fun fetchPhotoMeta(sortInfo: SortInfo,
                       notesList: MutableLiveData<List<PhotoMeta>>) {
        dbFetchPhotoMeta(sortInfo, notesList)
    }
    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    // But be careful about how listener updates live data
    // and noteListener?.remove() in onCleared
    private fun limitAndGet(query: Query,
                            notesList: MutableLiveData<List<PhotoMeta>>) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allNotes fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                notesList.postValue(result.documents.mapNotNull {
                    it.toObject(PhotoMeta::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allNotes fetch FAILED ", it)
            }
    }
    /////////////////////////////////////////////////////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/order-limit-data
    private fun dbFetchPhotoMeta(sortInfo: SortInfo,
                                 notesList: MutableLiveData<List<PhotoMeta>>) {
        // XXX Write me and use limitAndGet
    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    fun createPhotoMeta(
        sortInfo: SortInfo,
        photoMeta: PhotoMeta,
        notesList: MutableLiveData<List<PhotoMeta>>
    ) {
        // You can get a document id if you need it.
        //photoMeta.firestoreID = db.collection(rootCollection).document().id
        // XXX Write me: add photoMeta
    }

    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
    fun removePhotoMeta(
        sortInfo: SortInfo,
        photoMeta: PhotoMeta,
        photoMetaList: MutableLiveData<List<PhotoMeta>>
    ) {
        // XXX Write me.  Make sure you delete the correct entry
    }
}