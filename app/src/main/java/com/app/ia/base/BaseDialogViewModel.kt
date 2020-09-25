package com.app.ia.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

/**
 * Created by umeshk on 07/07/17.
 */
abstract class BaseDialogViewModel : ViewModel() {

    private var mActivity: WeakReference<Fragment>? = null

    override fun onCleared() {
        super.onCleared()
    }

    fun setActivityNavigator(mActivity: Fragment) {
        this.mActivity = WeakReference(mActivity)
    }

    protected fun getActivityNavigator(): Fragment? {
        return mActivity!!.get()
    }
}