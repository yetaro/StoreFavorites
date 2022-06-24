package com.example.strorefavorites

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.strorefavorites.databinding.FragmentEditStoreBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {
private lateinit var mBinding: FragmentEditStoreBinding
private var mActivity : MainActivity?=null
    private var mIsEditMode : Boolean = false
    private var mStoreEntity : StoreEntity?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong(getString(R.string.arg_id),0)
        if (id != null && id != 0L){
            //Toast.makeText(activity, "${id}", Toast.LENGTH_SHORT).show()
            mIsEditMode = true
            getStore(id)
        }else{
            //Toast.makeText(activity, "${id}", Toast.LENGTH_SHORT).show()
            mIsEditMode = false
            mStoreEntity = StoreEntity(name="", phone = "", photoURL = "")
        }
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)
        mBinding.etPhotoURL.addTextChangedListener {
            Glide.with(this)
                .load(mBinding.etPhotoURL.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_image_not_supported)
                .centerCrop()
                .into(mBinding.etImgPhoto)
        }
        setHasOptionsMenu(true)
    }

    private fun getStore(id: Long) {
        doAsync {
            mStoreEntity = StoreApplication.dataBase.storeDao().getStoreByI1(id)
            uiThread {
                if (mStoreEntity != null){
                    setUiStore(mStoreEntity!!)
                }
            }
        }
    }

    private fun setUiStore(mStoreEntity: StoreEntity) {
        with(mBinding){
            etName.text = mStoreEntity.name.editTable()
            etPhone.text = mStoreEntity.phone.editTable()
            etWebSite.text = mStoreEntity.webSite.editTable()
            etPhotoURL.text = mStoreEntity.photoURL.editTable()

        }
    }

    private fun String.editTable() : Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home-> {
                hidKeyBoard()
                mActivity?.onBackPressed()
                true

            }
            R.id.action_save->{
                /*val store = StoreEntity(
                    name = mBinding.etName.text.toString().trim(),
                    webSite = mBinding.etWebSite.text.toString().trim(),
                    photoURL = mBinding.etPhotoURL.text.toString().trim(),
                    phone = mBinding.etPhone.text.toString().trim()
                )*/
                if (mStoreEntity != null && validateField()){
                    with(mStoreEntity!!){
                        name = mBinding.etName.text.toString().trim()
                        webSite = mBinding.etWebSite.text.toString().trim()
                        photoURL = mBinding.etPhotoURL.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                    }
                    doAsync {
                        if (mIsEditMode){
                            StoreApplication.dataBase.storeDao().upDateStore(mStoreEntity!!)
                        }else{
                            mStoreEntity!!.id = StoreApplication.dataBase.storeDao().addStore(mStoreEntity!!)
                        }
                        uiThread {
                            hidKeyBoard()
                            if (mIsEditMode){
                                mActivity?.upDateStore(mStoreEntity!!)
                                Toast.makeText(mActivity,
                                "Store Update",
                                Toast.LENGTH_SHORT).show()
                            }else{
                                mActivity?.addStore(mStoreEntity!!)
                                Toast.makeText(mActivity, "Store Saved", Toast.LENGTH_SHORT).show()
                            }
                            mActivity?.onBackPressed()
                        }
                }
            }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateField(): Boolean {
        var isValid = true
        if (mBinding.etPhotoURL.text.toString().trim().isEmpty()){
            mBinding.tilPhotoURL.error = getString(R.string.helper_required)
            mBinding.etPhotoURL.requestFocus()
            isValid=false
        }
        if (mBinding.etPhone.text.toString().trim().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.tilPhone.requestFocus()
            isValid=false
        }
        if (mBinding.etName.text.toString().trim().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.tilName.requestFocus()
            isValid=false
        }

        return isValid
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title=getString(R.string.app_name)
        mActivity?.hidFab(true)
        super.onDestroy()
    }
    private fun hidKeyBoard(){
        val inm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null){
            inm.hideSoftInputFromWindow(requireView()!!.windowToken, 0)
        }
    }
}