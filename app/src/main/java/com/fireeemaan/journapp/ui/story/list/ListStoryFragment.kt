package com.fireeemaan.journapp.ui.story.list

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.datastore.dataStore
import com.fireeemaan.journapp.database.story.StoryEntity
import com.fireeemaan.journapp.databinding.FragmentListStoryBinding
import com.fireeemaan.journapp.ui.adapter.ListStoryAdapter
import com.fireeemaan.journapp.ui.story.StoryViewModelFactory

class ListStoryFragment : Fragment() {

    private lateinit var storyRecyclerView: RecyclerView
    private lateinit var storyAdapter: ListStoryAdapter
    private lateinit var progressBar: ProgressBar

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListStoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(
            requireActivity(),
            TokenDataStore.getInstance(requireContext().applicationContext.dataStore)
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.progressBar

        storyAdapter = ListStoryAdapter { storyId ->
            val action =
                ListStoryFragmentDirections.actionListStoryFragmentToDetailStoryFragment(storyId)
            findNavController().navigate(action)
        }
        storyRecyclerView = binding.storyRecyclerView
        storyRecyclerView.adapter = storyAdapter

        storyRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        showLoading(true)
        observeData()
    }

    private fun observeData() {
        viewModel.getAllStores().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    setData(result.data)
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(context, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    setData(emptyList())
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setData(listStory: List<StoryEntity>) {
        storyAdapter.submitList(listStory)
    }
}