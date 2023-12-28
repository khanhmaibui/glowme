package com.example.khanh_bui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.khanh_bui.fragment.FragmentHistory
import com.example.khanh_bui.fragment.FragmentSettings
import com.example.khanh_bui.fragment.FragmentStart
import com.example.khanh_bui.fragment.MyFragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy

class MainActivity : AppCompatActivity() {
    private lateinit var fragmentStart: FragmentStart
    private lateinit var fragmentHistory: FragmentHistory
    private lateinit var fragmentSettings: FragmentSettings
    private val titles = arrayOf("START", "HISTORY", "SETTINGS")
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var myFragmentStateAdapter: MyFragmentStateAdapter
    private lateinit var tabConfigurationStrategy: TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tool_bar))

        Util.checkPermissions(this)

        viewPager2 = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tab)

        fragmentStart = FragmentStart()
        fragmentHistory = FragmentHistory()
        fragmentSettings = FragmentSettings()
        fragments = ArrayList()
        fragments.add(fragmentStart)
        fragments.add(fragmentHistory)
        fragments.add(fragmentSettings)

        myFragmentStateAdapter = MyFragmentStateAdapter(this, fragments)
        viewPager2.adapter = myFragmentStateAdapter

        tabConfigurationStrategy =
            TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                titles
                tab.text = titles[position]
            }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }

    //Inflate action bar activities
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        //modify toolbar to not include setting button
        menu.removeItem(R.id.action_settings)

        return true
    }
}