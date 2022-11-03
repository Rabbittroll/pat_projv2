package edu.utap.photolist.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.photolist.MainViewModel
import edu.utap.photolist.databinding.RowBinding
import edu.utap.photolist.model.PhotoMeta


class PhotoMetaAdapter(private val viewModel: MainViewModel)
    : ListAdapter<PhotoMeta, PhotoMetaAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<PhotoMeta>() {
        override fun areItemsTheSame(oldItem: PhotoMeta, newItem: PhotoMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: PhotoMeta, newItem: PhotoMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.pictureTitle == newItem.pictureTitle
                    && oldItem.ownerUid == newItem.ownerUid
                    && oldItem.ownerName == newItem.ownerName
                    && oldItem.uuid == newItem.uuid
                    && oldItem.byteSize == newItem.byteSize
                    && oldItem.timeStamp == newItem.timeStamp
        }
    }

    inner class VH(private val rowBinding: RowBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(holder: VH, position: Int) {
            val photoMeta = viewModel.getPhotoMeta(position)
            viewModel.glideFetch(photoMeta.uuid, rowBinding.rowImageView)
            holder.rowBinding.rowPictureTitle.text = photoMeta.pictureTitle
            holder.rowBinding.rowSize.text = photoMeta.byteSize.toString()
            // Note to future me: It might be fun to display the date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = RowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder, position)
    }
}