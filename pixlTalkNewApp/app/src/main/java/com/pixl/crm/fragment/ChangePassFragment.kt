package com.pixl.crm.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.telecalling.crm.NavigationCommunicator
import com.telecalling.crm.R


class ChangePassFragment : Fragment() {


    private var navigationCommunicator: NavigationCommunicator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationCommunicator) {
            navigationCommunicator = context
        } else {
            throw RuntimeException("$context must implement NavigationCommunicator")
        }
    }

    override fun onDetach() {
        super.onDetach()
        navigationCommunicator = null
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_pass, container, false)

        return view
    }
}