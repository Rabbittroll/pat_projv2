package edu.utap.photolist.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.utap.photolist.MainActivity
import edu.utap.photolist.MainViewModel
import edu.utap.photolist.R
import edu.utap.photolist.SortColumn
import edu.utap.photolist.databinding.FragmentHomeBinding
import java.util.*
import java.util.UUID.randomUUID

class HomeFragment :
    Fragment(R.layout.fragment_home) {
    companion object {
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }
    private val viewModel: MainViewModel by activityViewModels()
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    // https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder#getBindingAdapterPosition()
    // Getting the position of the selected item is unfortunately complicated
    // This always returns a valid index.
    private fun getPos(holder: RecyclerView.ViewHolder) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }

    // Touch helpers provide functionality like detecting swipes or moving
    // entries in a recycler view.  Here we do swipe left to delete
    private fun initTouchHelper(): ItemTouchHelper {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START)
            {
                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    val position = getPos(viewHolder)
                    Log.d(javaClass.simpleName, "Swipe delete $position")
                    viewModel.removePhotoAt(position)
                }
            }
        return ItemTouchHelper(simpleItemTouchCallback)
    }
    // No need for onCreateView because we passed R.layout to Fragment constructor
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        Log.d(javaClass.simpleName, "onViewCreated")

        // Long press to edit.
        val adapter = PhotoMetaAdapter(viewModel)

        val rv = binding.photosRV
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)
        // Swipe left to delete
        initTouchHelper().attachToRecyclerView(rv)

        viewModel.observePhotoMeta().observe(viewLifecycleOwner){
            Log.d(null, "in observe photo")
        }

        viewModel.observeSortInfo().observe(viewLifecycleOwner){
            Log.d(null, "in observe sort")
        }

        binding.cameraButton.setOnClickListener {
            viewModel.takePhoto(binding.inputET.text.toString()){
                viewModel.createPhotoMeta(binding.inputET.text.toString(), randomUUID().toString() , it)
            }
        }

        // XXX Write me, onclick listeners and observers


    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}