package com.app.ia.dialog.bottom_sheet_dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import com.app.ia.R
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.dialog.bottom_sheet_dialog.adapter.CarAdapter
import com.app.ia.dialog.bottom_sheet_dialog.adapter.RatingAdapter
import com.app.ia.model.CommonSortBean
import com.app.ia.model.FilterData
import com.app.ia.model.FilterDataResponse
import com.app.ia.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_product_filter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception

class ProductFilterDialogFragment(private val businessCategoryId: String, selectedFilterData: FilterData) : BottomSheetDialogFragment() {

    private var onClickListener: OnProductFilterClickListener? = null
    private var filterData = selectedFilterData

    fun setOnItemClickListener(onClickListener: OnProductFilterClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return inflater.inflate(R.layout.dialog_product_filter, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    var selectedRating : String = ""
    var ratingPosition : Int = -1

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUp() {

        edtTextMinPrice.setText(filterData.minPrice)
        edtTextMaxPrice.setText(filterData.maxPrice)
        selectedRating = filterData.rating
        ratingPosition = filterData.ratingPosition

        val list = ArrayList<CommonSortBean>()
        list.add(CommonSortBean("1.0", ratingPosition == 0))
        list.add(CommonSortBean("2.0", ratingPosition == 1))
        list.add(CommonSortBean("3.0", ratingPosition == 2))
        list.add(CommonSortBean("4.0", ratingPosition == 3))
        list.add(CommonSortBean("5.0", ratingPosition == 4))

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

        /**
         * Call product Category on basis of business Category.
         */
        callProductCategory()

        val categoryList = ArrayList<FilterDataResponse>()
        categoryList.add(FilterDataResponse("-1", "Sub-Category"))
        val categoryAdapter = CarAdapter(requireContext(), R.layout.custom_spinner, categoryList)
        spinnerSubCategory.adapter = categoryAdapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0) {
                    val product = spinnerCategory.getItemAtPosition(p2) as FilterDataResponse
                    if (product.id != "-1") {
                        filterData.categoryId = product.id!!
                        filterData.categoryPos = p2
                        callProductSubCategory(product.id!!)
                    }
                } else {
                    filterData.categoryId = "-1"
                    //filterData.categoryPos = 0
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        spinnerSubCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 != 0) {
                    val product = spinnerSubCategory.getItemAtPosition(p2) as FilterDataResponse
                    if (product.id != "-1") {
                        filterData.subCategoryId = product.id!!
                        filterData.subCategoryPos = p2
                    }
                } else {
                    filterData.subCategoryId = "-1"
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
        callBrandCategory()

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

    private fun callProductCategory() {
        val requestParams = HashMap<String, String>()
        requestParams["business_category_id"] = businessCategoryId

        val service = RetrofitFactory.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getProductCategory(requestParams)
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        //Do something with response e.g show to the UI.

                        val productList = response.body()?.data?.docs
                        val categoryList = ArrayList<FilterDataResponse>()
                        categoryList.add(FilterDataResponse("-1", "Category"))
                        for (product in productList!!) {
                            categoryList.add(FilterDataResponse(product._Id, product.name))
                        }
                        val categoryAdapter = CarAdapter(requireContext(), R.layout.custom_spinner, categoryList)
                        spinnerCategory.adapter = categoryAdapter
                        spinnerCategory.post {
                            spinnerCategory.setSelection(filterData.categoryPos)
                        }

                    } else {
                        requireActivity().toast("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    requireActivity().toast("Exception ${e.message}")
                } catch (e: Throwable) {
                    requireActivity().toast("Oops: Something else went wrong")
                }
            }
        }
    }

    fun callProductSubCategory(category_id: String) {
        val requestParams = HashMap<String, String>()
        requestParams["business_category_id"] = businessCategoryId
        requestParams["category_id"] = category_id

        val service = RetrofitFactory.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getProductSubCategory(requestParams)
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        //Do something with response e.g show to the UI.

                        val productList = response.body()?.data?.docs
                        val categoryList = ArrayList<FilterDataResponse>()
                        categoryList.add(FilterDataResponse("-1", "Sub-Category"))
                        for (product in productList!!) {
                            categoryList.add(FilterDataResponse(product._Id, product.name))
                        }
                        val categoryAdapter = CarAdapter(requireContext(), R.layout.custom_spinner, categoryList)
                        spinnerSubCategory.adapter = categoryAdapter

                        spinnerSubCategory.post {
                            /*if(categoryList.size > filterData.subCategoryPos ) {
                                spinnerSubCategory.setSelection(filterData.subCategoryPos)
                            }*/
                            try {
                                spinnerSubCategory.setSelection(filterData.subCategoryPos)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        requireActivity().toast("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    requireActivity().toast("Exception ${e.message}")
                } catch (e: Throwable) {
                    requireActivity().toast("Ooops: Something else went wrong")
                }
            }
        }
    }


    private fun callBrandCategory() {
        val requestParams = HashMap<String, String>()
        requestParams["page_no"] = "1"

        val service = RetrofitFactory.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getBrands(requestParams)
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        //Do something with response e.g show to the UI.

                        val brandList = response.body()?.data?.docs
                        val categoryList = ArrayList<FilterDataResponse>()
                        categoryList.add(FilterDataResponse("-1", "Brand"))
                        for (product in brandList!!) {
                            categoryList.add(FilterDataResponse(product._Id, product.name))
                        }
                        val adapter = CarAdapter(requireContext(), R.layout.custom_spinner, categoryList)
                        spinnerBrand.adapter = adapter
                        spinnerBrand.post {
                            spinnerBrand.setSelection(filterData.brandPos)
                        }
                    } else {
                        requireActivity().toast("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    requireActivity().toast("Exception ${e.message}")
                } catch (e: Throwable) {
                    requireActivity().toast("Ooops: Something else went wrong")
                }
            }
        }
    }
}