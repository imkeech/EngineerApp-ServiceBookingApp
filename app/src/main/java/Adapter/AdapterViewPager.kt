package Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class AdapterViewPager(fragmentActivity: FragmentActivity, arr: ArrayList<Fragment>) :
    FragmentStateAdapter(fragmentActivity) {
    var arr: ArrayList<Fragment>

    init {
        this.arr = arr
    }

    override fun createFragment(position: Int): Fragment {
        return arr[position]
    }


    override fun getItemCount(): Int {
        return arr.size
    }
}
