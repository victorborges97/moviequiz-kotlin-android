package br.borges.moviequiz.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.borges.moviequiz.R
import br.borges.moviequiz.repository.FirebaseRepository
import br.borges.moviequiz.ui.main.adapter.FeedAdapter
import br.borges.moviequiz.ui.main.view_models.ViewModelFeed
import kotlinx.android.synthetic.main.fragment_feed.view.*

class FeedFragment : Fragment() {

    private lateinit var firestoreRepository: FirebaseRepository
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_feed, container, false)
        firestoreRepository = FirebaseRepository()
        val viewModel = ViewModelProvider(this)[ViewModelFeed::class.java]

        rootView.rvFeedFragment.layoutManager = LinearLayoutManager(rootView.context)
        rootView.rvFeedFragment.setHasFixedSize(true)

        viewModel.posts.observe(viewLifecycleOwner, {
            val recyclerViewState = rootView.rvFeedFragment.layoutManager?.onSaveInstanceState()
            rootView.rvFeedFragment.adapter = FeedAdapter(it)
            rootView.rvFeedFragment.adapter?.notifyDataSetChanged()
            rootView.rvFeedFragment.layoutManager?.onRestoreInstanceState(recyclerViewState)
        })

        return rootView
    }

//    private fun getListsData() {
//        newList = mutableListOf()
//
//        firestoreRepository
//            .getLists()
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//                if(snapshot?.isEmpty == true) {
//                    newList.clear()
//                    rootView.rvFeedFragment.adapter?.notifyDataSetChanged()
//                    rootView.rvFeedFragment.visibility = View.GONE;
//                    Log.w(TAG, "Lista Vazia")
//                    return@addSnapshotListener
//                }
//
//                val recyclerViewState = rootView.rvFeedFragment.layoutManager?.onSaveInstanceState()
//
//                for (dc in snapshot!!.documentChanges) {
//                    when (dc.type) {
//                        DocumentChange.Type.ADDED -> {
//                            dc.document.toObject(Post::class.java).let { entity ->
//                                entity.idPost = dc.document.id
//                                Log.d(TAG, "New item: ${entity}")
//                                newList.add(0, entity)
//                                posGetLists()
//                                rootView.rvFeedFragment.adapter?.notifyDataSetChanged()
//                                rootView.rvFeedFragment.layoutManager?.onRestoreInstanceState(recyclerViewState)
//                            }
//                        }
//                        DocumentChange.Type.MODIFIED -> {
//                            dc.document.toObject(Post::class.java).let { entity ->
//                                entity.idPost = dc.document.id
//                                val idxItem = Uteis.findIndex(newList, dc.document.id)
//                                newList[idxItem] = entity
//                                Log.d(TAG,
//                                    "\nModified item: " +
//                                            "\n${newList[idxItem]}")
//
//                                posGetLists()
//                                rootView.rvFeedFragment.adapter?.notifyItemChanged(idxItem, null)
//                                rootView.rvFeedFragment.layoutManager?.onRestoreInstanceState(recyclerViewState)
//                            }
//                        }
//                        DocumentChange.Type.REMOVED -> {
//                            dc.document.toObject(Post::class.java).let { entity ->
//                                val idxItem = Uteis.findIndex(newList, dc.document.id)
//                                entity.idPost = dc.document.id
//                                Log.d(TAG, "Removed item: $entity")
//                                newList.removeAt(idxItem)
//                                posGetLists()
//                                rootView.rvFeedFragment.adapter?.notifyDataSetChanged()
//                                rootView.rvFeedFragment.layoutManager?.onRestoreInstanceState(recyclerViewState)
//                            }
//                        }
//                    }
//                }
//            }
//    }

//    private fun posGetLists() {
//        rootView.rvFeedFragment.adapter = context?.let { FeedAdapter(newList) }
//    }

}