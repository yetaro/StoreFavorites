package com.example.strorefavorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.strorefavorites.databinding.ItemStoreBinding

class StoreAdapter (private var stores: MutableList<StoreEntity>, private var listener: OnClickListener):
        RecyclerView.Adapter<StoreAdapter.ViewHolder>(){
    private lateinit var mContext : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //conexion
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores.get(position)
        //foreach
        with(holder){
            setListener(store)
            binding.tvName.text = store.name
            binding.cbFavorite.isChecked=store.isFavorite
            Glide.with(mContext)
                .load(store.photoURL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_image_not_supported)
                .centerCrop()
                .into(binding.tvImage)
        }
    }

    override fun getItemCount(): Int = stores.size
    fun add(storeEntity: StoreEntity) {
        //agregando al arreglo principal
        stores.add(storeEntity)
        notifyDataSetChanged()

    }

    fun getStore(store: MutableList<StoreEntity>) {
        this.stores = store
        notifyDataSetChanged()
    }

    fun update(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)
        if (index != -1){
            stores.set(index, storeEntity)
            notifyItemChanged(index)
        }
    }
    fun delete(storeEntity: StoreEntity){
        val index = stores.indexOf(storeEntity)
        if (index != -1){
            stores.removeAt(index)
            notifyItemRemoved(index)
        }
    }//delete
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view)

        fun setListener(storeEntity: StoreEntity) {
            binding.root.setOnClickListener{ listener.onClick(storeEntity.id)}
            //eliminar
            binding.root.setOnLongClickListener {
                listener.onDeleteStore(storeEntity)
                true
            }
            binding.cbFavorite.setOnClickListener{
                listener.onFavoriteStore(storeEntity)
            }

        }
    }
        }