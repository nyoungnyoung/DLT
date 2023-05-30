package com.dopamines.dlt.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentSignUpBinding
import com.dopamines.dlt.presentation.home.HomeActivity

import kotlinx.coroutines.launch


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel
    private lateinit var loginModel : LogInViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.signUpToolbar.title = "회원정보 입력"


        binding.tvNameCnt.text = getString(R.string.num_signup, 0)
        binding.etSignUpInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = s?.length ?: 0
                binding.tvNameCnt.text = resources.getString(R.string.num_signup, textLength)
            }

            override fun afterTextChanged(s: Editable?) {
                val textLength = s?.length ?: 0
                binding.tvNameCnt.text = resources.getString(R.string.num_signup, textLength)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SignUpViewModel 인스턴스 생성
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        loginModel = ViewModelProvider(requireActivity()).get(LogInViewModel::class.java)


        binding.signUpToolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }


        val bundle = arguments
        val kakaoId = bundle?.getString("kakaoId").toString()
        val email = bundle?.getString("email").toString()

        Log.i("TAG", kakaoId)
        Log.i("TAg", email)

        binding.btnSignUp.setOnClickListener {

            lifecycleScope.launch {
                val nickname = binding.etSignUpInput.text.toString()
                viewModel.signupToDLT(email, kakaoId, nickname)
                Log.i("TAG닉네임 왜없음?", nickname)
                viewModel.signUpResponse.observe(viewLifecycleOwner) {response->
                    if(response == true) {
                        // 회원가입이 완료되었다면 로그인 요청을 보내고 로그인이 완료되면 이동
                        loginModel.loginToDLT(email,kakaoId)
                        loginModel.checkloginResponse.observe(viewLifecycleOwner) {res->
                            if(res ==true) {
                                val intent = Intent(requireActivity(), HomeActivity::class.java)
                                // 새로운 태스크에 새로 시작된 액티비티를 넣으며, 기존에 쌓인 태스크가 있다면 모두 제거하여 초기화면으로 돌아감
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }

    }
}

