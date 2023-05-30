package com.dopamines.dlt.presentation.auth



import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentPermissionBinding


class PermissionFragment : Fragment() {

    private lateinit var binding: FragmentPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_permission, container, false)


        return binding.root
    }

    // OnViewCreate:   Fragment 의 View가 생성된 후에 호출
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPermissionCheck.setOnClickListener {
            findNavController().navigate(R.id.action_permissionFragment_to_logInFragment)
        }
    }

}


