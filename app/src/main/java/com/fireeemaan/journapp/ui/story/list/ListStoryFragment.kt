package com.fireeemaan.journapp.ui.story.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fireeemaan.journapp.data.Result
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.datastore.dataStore
import com.fireeemaan.journapp.database.story.StoryEntity
import com.fireeemaan.journapp.databinding.FragmentListStoryBinding
import com.fireeemaan.journapp.ui.adapter.ListStoryAdapter
import com.fireeemaan.journapp.ui.adapter.LoadingStateAdapter
import com.fireeemaan.journapp.ui.story.StoryViewModelFactory
import com.fireeemaan.journapp.ui.story.camera.CameraActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListStoryFragment : Fragment() {

    private lateinit var storyRecyclerView: RecyclerView
    private lateinit var storyAdapter: ListStoryAdapter
    private lateinit var fabAddStory: FloatingActionButton

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

        fabAddStory = binding.fabAddStory

        (activity as? AppCompatActivity)?.supportActionBar?.show()

        storyAdapter = ListStoryAdapter { storyId ->
            val action =
                ListStoryFragmentDirections.actionListStoryFragmentToDetailStoryFragment(storyId)
            findNavController().navigate(action)
        }
        storyRecyclerView = binding.storyRecyclerView
        storyRecyclerView.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        storyRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        fabAddStory.setOnClickListener {
            val intent = Intent(requireActivity(), CameraActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })

//        showLoading(true)
        observeData()
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    private fun observeData() {
        viewModel.quote.observe(viewLifecycleOwner) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

}