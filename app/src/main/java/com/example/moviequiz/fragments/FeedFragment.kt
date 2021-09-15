package com.example.moviequiz.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentChange
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviequiz.R
import com.example.moviequiz.Uteis.Uteis
import com.example.moviequiz.adapters.FeedAdapter
import com.example.moviequiz.models.Post
import com.example.moviequiz.repository.FirebaseRepository
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_feed.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FeedFragment : Fragment() {

    private lateinit var newList: MutableList<Post>
    private lateinit var firestoreRepository: FirebaseRepository
    private lateinit var rootView: View;
    val TAG = "FRAGMENT FEED"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_feed, container, false)
        firestoreRepository = FirebaseRepository();

        rootView.rvFeedFragment.layoutManager = LinearLayoutManager(rootView.context)
        rootView.rvFeedFragment.setHasFixedSize(true)

        return rootView;
    }

    override fun onStart() {
        super.onStart()
        getListsData()
    }

    private fun getListsData() {
        newList = mutableListOf()

        firestoreRepository
            .getLists()
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(snapshot?.isEmpty == true) {
                    newList.clear()
                    rootView.rvFeedFragment.adapter?.notifyDataSetChanged()
                    rootView.rvFeedFragment.visibility = View.GONE;
                    Log.w(TAG, "Lista Vazia")
                    return@addSnapshotListener
                }

                val recyclerViewState = rootView.rvFeedFragment.layoutManager?.onSaveInstanceState()
                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            dc.document.toObject(Post::class.java).let { entity ->
                                entity.idPost = dc.document.id
                                Log.d(TAG, "New item: ${entity}")
                                newList.add(0, entity)
                                posGetLists()
                                rootView.rvFeedFragment.adapter?.notifyDataSetChanged()
                                rootView.rvFeedFragment.layoutManager?.onRestoreInstanceState(recyclerViewState)
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {
                            dc.document.toObject(Post::class.java).let { entity ->
                                entity.idPost = dc.document.id
                                val idxItem = Uteis.findIndex(newList, dc.document.id)
                                newList[idxItem] = entity
                                Log.d(TAG,
                                    "\nModified item: " +
                                            "\n${newList[idxItem]}")

                                posGetLists()
                                rootView.rvFeedFragment.adapter?.notifyItemChanged(idxItem, null)
                                rootView.rvFeedFragment.layoutManager?.onRestoreInstanceState(recyclerViewState)
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            dc.document.toObject(Post::class.java).let { entity ->
                                val idxItem = Uteis.findIndex(newList, dc.document.id)
                                entity.idPost = dc.document.id
                                Log.d(TAG, "Removed item: $entity")
                                newList.removeAt(idxItem)
                                posGetLists()
                                rootView.rvFeedFragment.adapter?.notifyDataSetChanged()
                                rootView.rvFeedFragment.layoutManager?.onRestoreInstanceState(recyclerViewState)
                            }
                        }
                    }
                }
            }
    }

    private fun posGetLists() {
        //Mandando a Lista Mutavel para o Adapter
        rootView.rvFeedFragment.adapter = context?.let { FeedAdapter(newList, it) }
    }

}