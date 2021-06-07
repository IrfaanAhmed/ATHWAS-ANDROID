package com.app.ia.ui.faq

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.ia.BR
import com.app.ia.R
import com.app.ia.ViewModelFactory
import com.app.ia.apiclient.RetrofitFactory
import com.app.ia.base.BaseActivity
import com.app.ia.base.BaseRepository
import com.app.ia.databinding.ActivityFaqBinding
import com.app.ia.ui.faq.adapter.FaqAdapter
import com.app.ia.ui.product_list.adapter.ProductListAdapter
import com.app.ia.utils.EqualSpacingItemDecoration
import com.app.ia.utils.makeStatusBarTransparent
import com.app.ia.utils.setOnApplyWindowInset
import kotlinx.android.synthetic.main.activity_faq.*
import kotlinx.android.synthetic.main.activity_faq.content_container
import kotlinx.android.synthetic.main.activity_faq.toolbar
import kotlinx.android.synthetic.main.activity_product_list.*


class FaqActivity : BaseActivity<ActivityFaqBinding, FaqViewModel>() {

    /**
     * Variable declaration
     */
    private lateinit var mViewModel: FaqViewModel
    private lateinit var mBinding: ActivityFaqBinding

    private var faqAdapter: FaqAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_faq
    }

    override fun getViewModel(): FaqViewModel {
        return mViewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mBinding.lifecycleOwner = this
        mViewModel.setActivityNavigator(this)
        mViewModel.setVariable(mBinding)
        mViewModel.setIntent(intent)

        //makeStatusBarTransparent()
        setOnApplyWindowInset(toolbar, content_container)

        faqRecyclerView.addItemDecoration(EqualSpacingItemDecoration(20, EqualSpacingItemDecoration.VERTICAL))
        faqRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        faqAdapter = FaqAdapter()
        faqRecyclerView.adapter = faqAdapter

        mViewModel.faqList.observe(this, {
            if (it.size <= 0) {
                faqAdapter?.notifyDataSetChanged()
            } else {
                if (faqAdapter?.currentList?.size!! == 0) {
                    faqAdapter?.submitList(it)
                } else {
                    faqAdapter?.notifyDataSetChanged()
                }
            }
        })

    }


    /*  fun setAdapter(contentData: List<FaqResponseBean.Data.ContentData>) {
          mViewModel.dataFound.value = !contentData.isNullOrEmpty()
          mBinding.adapter = FaqAdapter(contentData)
      }*/

    private fun setViewModel() {
        val factory = ViewModelFactory(FaqViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(FaqViewModel::class.java)
    }
}
