package com.example.strorefavorites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.strorefavorites.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {
    //declaracion variables globales
    private lateinit var mBinding: ActivityMainBinding
    //creacion de un adaptador
    private lateinit var mAdapter: StoreAdapter
    //layout manager que se encargará de la creación del recycleview y del grid
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        /*mBinding.btnSave.setOnClickListener{
            val store = StoreEntity(name = mBinding.tvNameAdd.text.toString().trim())
            mAdapter.add(store)
            // creacion de un hilo para guardar info
            Thread{
                StoreApplication.dataBase.storeDao().addStore(store)
            }.start()
        }*/
        mBinding.fab.setOnClickListener{
            launchEditFragment()
        }
        setUpRecycleView()
    }
    private fun launchEditFragment(args : Bundle? = null){
        val fragment = EditStoreFragment()
        if (args != null){
            fragment.arguments = args
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.commit()
        fragmentTransaction.addToBackStack(null)
        hidFab()
    }

    private fun setUpRecycleView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)
        //para guardar info de manera asincrona
        getStore()
        mBinding.recycleView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStore() {
        doAsync {
            val store = StoreApplication.dataBase.storeDao().getAllStore()
            uiThread {
                mAdapter.getStore(store)
            }
        }
    }

    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)
        launchEditFragment(args)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite =! storeEntity.isFavorite
        doAsync {
            StoreApplication.dataBase.storeDao().upDateStore(storeEntity)
            uiThread {
                mAdapter.update(storeEntity)
            }
        }
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        doAsync {
            StoreApplication.dataBase.storeDao().deleteStore(storeEntity)
            uiThread {
                mAdapter.delete(storeEntity)
            }
        }
    }

    override fun hidFab(isVisible: Boolean) {
        if (isVisible){
            mBinding.fab.show()
        }else{
            mBinding.fab.hide()
        }
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun upDateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}