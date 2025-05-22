package com.example.barta.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.barta.databinding.FragmentHomeBinding
import com.example.barta.ui.home.HomeViewModel
import com.example.barta.ui.player.PlayerActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textHome.text = "BARTA"

        // 변환 버튼 클릭 시
        binding.buttonConvert.setOnClickListener {
            val url = binding.editTextUrl.text.toString()
            if (url.isNotEmpty()) {
                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("youtube_url", url)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "링크를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
