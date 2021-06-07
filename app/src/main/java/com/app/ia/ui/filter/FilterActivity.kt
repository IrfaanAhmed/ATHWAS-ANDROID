package com.app.ia.ui.filter

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityFilterBinding
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.CustomizationSubTypeResponse
import com.app.ia.model.CustomizationTypeResponse
import com.app.ia.model.ProductCategoryResponse
import com.app.ia.ui.filter.adapter.FilterCustomizationAdapter
import com.app.ia.ui.filter.adapter.FilterSubCustomizationAdapter
import com.app.ia.utils.setOnApplyWindowInset1
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : BaseActivity<ActivityFilterBinding, FilterViewModel>() {

    private var mActivityBinding: ActivityFilterBinding? = null
    private var mViewModel: FilterViewModel? = null
    private var customizationTypeId = ""

    var customizationSelectedFilters = HashMap<String, MutableList<CustomizationSubTypeResponse.CustomizationSubType>>()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_filter
    }

    override fun getViewModel(): FilterViewModel {
        return mViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mActivityBinding = getViewDataBinding()
        mActivityBinding?.lifecycleOwner = this
        mViewModel?.setActivityNavigator(this)
        mViewModel?.setVariable(mActivityBinding!!, intent)

        customizationSelectedFilters = intent?.getSerializableExtra("filters") as HashMap<String, MutableList<CustomizationSubTypeResponse.CustomizationSubType>>

        setOnApplyWindowInset1(toolbar, content_container)

        val customizationTypeAdapter = FilterCustomizationAdapter()
        customizationTypeRecyclerView.adapter = customizationTypeAdapter

        mViewModel?.customizationList?.observe(this, {
            customizationTypeAdapter.submitList(it)
            customizationTypeId = it[0].Id
            showSubCustomization(customizationTypeId)
        })

        customizationTypeAdapter.setOnSimilarProductClickListener(object : FilterCustomizationAdapter.OnCustomizationTypeClickListener {
            override fun onSimilarProductClick(customizationType: CustomizationTypeResponse.Docs, position: Int) {
               // mViewModel?.customizationSubTypeObserver(customizationType.Id)
                customizationTypeId = customizationType.Id
                showSubCustomization(customizationType.Id)
            }
        })

        val customizationSubTypeAdapter = FilterSubCustomizationAdapter()
        customizationSubTypeRecyclerView.adapter = customizationSubTypeAdapter
        customizationSubTypeAdapter.setOnSubCustomizationClickListener(object : FilterSubCustomizationAdapter.OnSubCustomizationClickListener {
            override fun onSubCustomizationClick(customization: CustomizationSubTypeResponse.CustomizationSubType, position: Int) {
                mViewModel?.customizationSubTypeList?.value!![position] = customization
                customizationSubTypeAdapter.notifyDataSetChanged()
                customizationSelectedFilters[customizationTypeId] = mViewModel?.customizationSubTypeList?.value!!
            }
        })

        mViewModel?.customizationSubTypeList?.observe(this, {
            customizationSubTypeAdapter.submitList(it)
        })

        applyFilter.setOnClickListener {
            val intent = Intent()
            intent.putExtra("filters", customizationSelectedFilters)
            setResult(RESULT_OK, intent)
            finish()
        }

        clearFilter.setOnClickListener {
            customizationSelectedFilters = HashMap()
            val intent = Intent()
            intent.putExtra("filters", customizationSelectedFilters)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(FilterViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(FilterViewModel::class.java)
    }

    fun showSubCustomization(customizationTypeId: String) {


        val value = customizationSelectedFilters[customizationTypeId]

        if(value != null && value.size > 0) {
            mViewModel?.customizationSubTypeList?.value = value
        } else {
            mViewModel?.customizationSubTypeObserver(customizationTypeId)
        }
    }
}