package com.app.ia.dialog.bottom_sheet_dialog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseBottomSheetDialogFragment
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.DialogProductFilterBinding
import com.app.ia.dialog.bottom_sheet_dialog.adapter.CarAdapter
import com.app.ia.dialog.bottom_sheet_dialog.adapter.RatingAdapter
import com.app.ia.dialog.bottom_sheet_dialog.viewmodel.ProductFilterDialogViewModel
import com.app.ia.local.AppPreferencesHelper
import com.app.ia.model.*
import com.app.ia.ui.filter.FilterActivity
import com.app.ia.utils.AppConstants
import com.app.ia.utils.mStartActivityForResult
import com.app.ia.utils.toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_product_filter.*

class ProductFilterDialogFragment(val businessCategoryId: String, selectedFilterData: FilterData) : BaseBottomSheetDialogFragment<DialogProductFilterBinding, ProductFilterDialogViewModel>() {

    private lateinit var mBinding: DialogProductFilterBinding
    private lateinit var mViewModel: ProductFilterDialogViewModel
    private var onClickListener: OnProductFilterClickListener? = null
    private var filterData = selectedFilterData

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.dialog_product_filter

    override val viewModel: ProductFilterDialogViewModel
        get() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
    }

    private fun setViewModel() {
        val factory = ViewModelFactory(ProductFilterDialogViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(ProductFilterDialogViewModel::class.java)
    }

    fun setOnItemClickListener(onClickListener: OnProductFilterClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        mBinding.lifecycleOwner = this
        mViewModel.setActivityNavigator(this)
        mViewModel.setVariable(mBinding)
        setUp()
    }

    var selectedRating: String = "All"
    var ratingPosition: Int = 0

    var showSubCategorySelectedFirstTime = true

    private fun setUp() {

        edtTextMinPrice.setText(filterData.minPrice)
        edtTextMaxPrice.setText(filterData.maxPrice)
        selectedRating = filterData.rating
        ratingPosition = filterData.ratingPosition

        val list = ArrayList<CommonSortBean>()
        list.add(CommonSortBean("All", ratingPosition == 0))
        list.add(CommonSortBean("1 & above", ratingPosition == 1))
        list.add(CommonSortBean("2 & above", ratingPosition == 2))
        list.add(CommonSortBean("3 & above", ratingPosition == 3))
        list.add(CommonSortBean("4 & above", ratingPosition == 4))
        //list.add(CommonSortBean("5 & up", ratingPosition == 5))

        val ratingAdapter = RatingAdapter()
        recyclerViewRating.adapter = ratingAdapter
        ratingAdapter.setOnItemSelectListener(object : RatingAdapter.OnRatingItemSelectListener {
            override fun onRatingSelect(rating: String, position: Int) {
                selectedRating = rating
                ratingPosition = position
                for (ratings in 0 until list.size) {
                    list[ratings].isSelected = position == ratings
                }
                ratingAdapter.notifyDataSetChanged()
            }
        })
        ratingAdapter.submitList(list)

        tvCancel.setOnClickListener {
            dismiss()
        }

        btnResetFilter.setOnClickListener {
            onClickListener?.onSubmitClick(FilterData())
            dismiss()
        }

        selectCustomization.setOnClickListener {
            requireActivity().mStartActivityForResult<FilterActivity>(2000) {
                putExtra(AppConstants.EXTRA_PRODUCT_CATEGORY_ID, filterData.categoryId)
                putExtra("filters", filterData.customizationSelectedFilters)
            }
        }

        customizationFilterStatus()

        /**
         * Call product Category on basis of business Category.
         */
        val json: String = AppPreferencesHelper.getInstance().getString(AppPreferencesHelper.CATEGORY)
        if (json.isEmpty()) {
            mViewModel.productCategoryObserver()
            mViewModel.productCategoryResponse.observe(this, {
                val categoryList = ArrayList<FilterDataResponse>()
                categoryList.add(FilterDataResponse("-1", "Category"))
                for ((catPos, product) in it.withIndex()) {
                    categoryList.add(FilterDataResponse(product._Id, product.name))
                    if (filterData.categoryPos == 0) {
                        if (filterData.categoryId == product._Id) {
                            filterData.categoryPos = catPos + 1
                        }
                    }
                }
                val categoryAdapter = CarAdapter(requireContext(), R.layout.custom_spinner, categoryList)
                spinnerCategory.adapter = categoryAdapter
                spinnerCategory.post {
                    spinnerCategory.setSelection(filterData.categoryPos)
                }
            })
        } else {
            val obj = Gson().fromJson(json, ProductCategoryResponse::class.java)

            val categoryList = ArrayList<FilterDataResponse>()
            categoryList.add(FilterDataResponse("-1", "Category"))
            for ((catPos, product) in obj.docs.withIndex()) {
                categoryList.add(FilterDataResponse(product._Id, product.name))
                if (filterData.categoryPos == 0) {
                    if (filterData.categoryId == product._Id) {
                        filterData.categoryPos = catPos + 1
                    }
                }
            }
            val categoryAdapter = CarAdapter(requireContext(), R.layout.custom_spinner, categoryList)
            spinnerCategory.adapter = categoryAdapter
            spinnerCategory.post {
                spinnerCategory.setSelection(filterData.categoryPos)
            }
        }


        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0) {
                    val product = spinnerCategory.getItemAtPosition(p2) as FilterDataResponse
                    if (product.id != "-1") {
                        filterData.categoryId = product.id!!
                        filterData.categoryPos = p2
                        mViewModel.productSubCategoryObserver(product.id!!)
                    }
                } else {
                    filterData.categoryId = "-1"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        mViewModel.productSubcategoryResponse.observe(this, {

            val subCategoryList = ArrayList<FilterDataResponse>()
            subCategoryList.add(FilterDataResponse("-1", "Sub-Category"))
            for ((pos, product) in it!!.withIndex()) {
                subCategoryList.add(FilterDataResponse(product._Id, product.name))
                if (product._Id == filterData.subCategoryId) {
                    filterData.subCategoryPos = pos + 1
                }
            }
            val subCategoryAdapter = CarAdapter(requireContext(), R.layout.custom_spinner, subCategoryList)
            spinnerSubCategory.adapter = subCategoryAdapter

            spinnerSubCategory.post {
                try {
                    if (showSubCategorySelectedFirstTime) {
                        if(filterData.subCategoryPos < subCategoryList.size) {
                            spinnerSubCategory.setSelection(filterData.subCategoryPos)
                        }
                        showSubCategorySelectedFirstTime = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        val subCategoryList = ArrayList<FilterDataResponse>()
        subCategoryList.add(FilterDataResponse("-1", "Sub-Category"))
        val subCategoryAdapter = CarAdapter(requireContext(), R.layout.custom_spinner, subCategoryList)
        spinnerSubCategory.adapter = subCategoryAdapter

        spinnerSubCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0) {
                    val product = spinnerSubCategory.getItemAtPosition(p2) as FilterDataResponse
                    if (product.id != "-1") {
                        filterData.subCategoryId = product.id!!
                        filterData.subCategoryPos = p2
                    }
                } else {
                    //filterData.subCategoryId = "-1"
                    //filterData.subCategoryPos = 0
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        spinnerBrand.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0) {
                    val product = spinnerBrand.getItemAtPosition(p2) as FilterDataResponse
                    if (product.id != "-1") {
                        filterData.brandId = product.id!!
                        filterData.brandPos = p2
                    }
                } else {
                    filterData.brandId = "-1"
                    filterData.brandPos = 0
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        mViewModel.brandObserver()
        mViewModel.brandListResponse.observe(this, {

            val brandList = ArrayList<FilterDataResponse>()
            brandList.add(FilterDataResponse("-1", "Brand"))
            for (product in it!!) {
                brandList.add(FilterDataResponse(product._Id, product.name))
            }
            val adapter = CarAdapter(requireContext(), R.layout.custom_spinner, brandList)
            spinnerBrand.adapter = adapter
            spinnerBrand.post {
                spinnerBrand.setSelection(filterData.brandPos)
            }
        })

        btnApplyFilter.setOnClickListener {

            filterData.minPrice = edtTextMinPrice.text.toString()
            filterData.maxPrice = edtTextMaxPrice.text.toString()
            filterData.rating = selectedRating
            filterData.ratingPosition = ratingPosition

            when {
                filterData.categoryPos == 0 -> {
                    requireActivity().toast("Please select Category")
                }

                filterData.subCategoryPos == 0 -> {
                    requireActivity().toast("Please select Sub Category")
                }

                else -> {
                    onClickListener?.onSubmitClick(filterData)
                    dismiss()
                }
            }
        }
    }

    interface OnProductFilterClickListener {
        fun onSubmitClick(filterValue: FilterData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2000 && resultCode == Activity.RESULT_OK) {
            val selectedFilter: HashMap<String, MutableList<CustomizationSubTypeResponse.CustomizationSubType>> = data?.getSerializableExtra("filters") as HashMap<String, MutableList<CustomizationSubTypeResponse.CustomizationSubType>>
            filterData.customizationSelectedFilters = selectedFilter
            customizationFilterStatus()
        }
    }

    private fun customizationFilterStatus() {
        var isAnyFilterSelected = false
        for (key in filterData.customizationSelectedFilters.keys) {
            val filters: MutableList<CustomizationSubTypeResponse.CustomizationSubType> = filterData.customizationSelectedFilters[key]!!
            for (filter in filters) {
                if (filter.isSelected) {
                    isAnyFilterSelected = true
                    break
                }
            }
        }
        if (isAnyFilterSelected) {
            selectCustomization.text = "Customization Applied"
            selectCustomization.setBackgroundResource(R.drawable.edittext_bg)
        } else {
            selectCustomization.text = "Customization"
            selectCustomization.setBackgroundResource(R.drawable.edittext_bg)
        }
    }
}