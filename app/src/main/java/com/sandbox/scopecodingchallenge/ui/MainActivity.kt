package com.sandbox.scopecodingchallenge.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.sandbox.scopecodingchallenge.databinding.ActivityMainBinding
import com.sandbox.scopecodingchallenge.model.UserData
import com.sandbox.scopecodingchallenge.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity(), UserListAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var userListAdapter: UserListAdapter
    lateinit var viewModel: MainActivityViewModel

    companion object {
        val TAG : String = this::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userListAdapter = UserListAdapter(this)
        binding.userList.adapter = userListAdapter
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        observeViewModel()

        viewModel.getUserList()
    }

    private fun observeViewModel() {
        viewModel.userList.observe(this, { response ->
            Log.d(TAG, "observerViewModel: ${response.size}")

            if (response.isNotEmpty()) {
                userListAdapter.setList(response)
            }
        })

        viewModel.requestError.observe(this, {
            binding.errorMessage.visibility = if (it == null) View.INVISIBLE else View.VISIBLE
            Log.d(TAG, "observerViewModel: $it")
        })

        viewModel.waitingResponse.observe(this, {
            binding.progressIndicator.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
    }

    // UserListAdapter.OnItemClickListener

    override fun onItemClick(item: UserData) {
        Log.d(TAG, "User clicked: ${item.owner.name}")
        startActivity(MapsActivity.newIntent(this, item.userid))
    }
}