package com.sandbox.scopecodingchallenge.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.sandbox.scopecodingchallenge.R
import com.sandbox.scopecodingchallenge.databinding.ActivityMainBinding
import com.sandbox.scopecodingchallenge.model.UserData
import com.sandbox.scopecodingchallenge.viewmodel.MainActivityViewModel
import androidx.recyclerview.widget.DividerItemDecoration

class MainActivity : AppCompatActivity(), UserListAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userListAdapter: UserListAdapter
    private lateinit var viewModel: MainActivityViewModel

    companion object {
        val TAG : String = this::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userListAdapter = UserListAdapter(this)
        binding.userList.adapter = userListAdapter
        binding.userList.addItemDecoration(
            DividerItemDecoration(
                binding.userList.context,
                DividerItemDecoration.VERTICAL
            )
        )
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

        viewModel.loadingLocally.observe(this, {
            Toast.makeText(
                applicationContext,
                getString(if (it) R.string.main_activity_loading_locally else R.string.main_activity_loading_remotely),
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    // UserListAdapter.OnItemClickListener

    override fun onItemClick(item: UserData) {
        Log.d(TAG, "User clicked: ${item.owner?.name}")
        startActivity(MapsActivity.newIntent(this, item.userid!!))
    }
}