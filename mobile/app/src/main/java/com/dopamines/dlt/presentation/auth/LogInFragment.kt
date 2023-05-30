package com.dopamines.dlt.presentation.auth


import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.dopamines.dlt.databinding.FragmentLogInBinding
import com.dopamines.dlt.presentation.create.CreateApiService
import com.dopamines.dlt.presentation.home.HomeActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LogInFragment : Fragment() {
    private lateinit var binding: FragmentLogInBinding
    private lateinit var viewModel: CheckUserViewModel
    private lateinit var loginViewModel: LogInViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_log_in, container, false)
        viewModel = ViewModelProvider(this).get(CheckUserViewModel::class.java)
        
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProvider(this).get(LogInViewModel::class.java)

        val loginTAG = "로그인!!!!!!!!!!!!!!!!!!!!"
        binding.btnLogin.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(loginTAG, "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i(loginTAG, "카카오계정으로 로그인 성공 ${token.accessToken}")

                    // 추가
                    val kakaoToken = token.accessToken

                    // <<<<<<<<<<<<<<login 체크 >>>>>>>>>>>>
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.sendKakaoTokenToBackend(kakaoToken)
                        viewModel.checkUserResponse.observe(viewLifecycleOwner) { response ->
                            Log.i("LoginFragment", response.toString())
                            if (response?.signup == true) {
                                // 회원가입이 된 상태면
                                // 먼저 서비스 로그인 요청 보내고
                                val kakaoId = viewModel.checkUserResponse.value?.kakaoId
                                val email = viewModel.checkUserResponse.value?.email
                                loginViewModel.loginToDLT(email.toString(), kakaoId.toString())

                                // 로그인이 된 후, Homeactivity로 이동
                                loginViewModel.checkloginResponse.observe(viewLifecycleOwner) { res->
                                    if (res == true) {

                                        // FireBaseToken 받아서 보내기
                                        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                            if (!task.isSuccessful) {
                                                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                                                return@OnCompleteListener
                                            }

                                            // Get new FCM registration token
                                            val token = task.result

                                            // Log and toast
                                            val msg = "FCM Registration Token: $token"
                                            Log.d(ContentValues.TAG, msg)

                                            loginViewModel.sendRegistrationToServer(token.toString())
                                            // update
                                            loginViewModel.updateDeviceToken(token.toString())


                                            val intent = Intent(requireActivity(), HomeActivity::class.java)
                                            // 새로운 태스크에 새로 시작된 액티비티를 넣으며, 기존에 쌓인 태스크가 있다면 모두 제거하여 초기화면으로 돌아감
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)

                                        })

                                    }
                                }
                            } else {
                                // 회원가입이 안되어 있다면
                                val kakaoId = viewModel.checkUserResponse.value?.kakaoId
                                val email = viewModel.checkUserResponse.value?.email

                                // 값을 viewmodel에서 가져와서 signupfrag로 bundle로 넘겨주고
                                Log.i("BEFOREBUNDLE" , kakaoId.toString())
                                val bundle = Bundle().apply {
                                    putString("kakaoId", kakaoId)
                                    putString("email", email)
                                }

                                // 회원가입창으로 이동
                                findNavController().navigate(R.id.signUpFragment, bundle)
                            }
                        }
                    }
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                    if (error != null) {
                        Log.e(loginTAG, "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(
                            requireContext(),
                            callback = callback
                        )
                    } else if (token != null) {
                        Log.i(loginTAG, "카카오톡으로 로그인 성공!!!!!!!! ${token.accessToken}")
                        val kakaoToken = token.accessToken

                        // <<<<<<<<<<<<<<login 체크 >>>>>>>>>>>>
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.sendKakaoTokenToBackend(kakaoToken)
                            viewModel.checkUserResponse.observe(viewLifecycleOwner) { response ->
                                Log.i("LoginFragment", response.toString())
                                if (response?.signup == true) {
                                    // 회원가입이 된 상태면
                                    // 먼저 서비스 로그인 요청 보내고
                                    val kakaoId = viewModel.checkUserResponse.value?.kakaoId
                                    val email = viewModel.checkUserResponse.value?.email
                                    loginViewModel.loginToDLT(email.toString(), kakaoId.toString())



                                    // 로그인이 된 후, Homeactivity로 이동
                                    loginViewModel.checkloginResponse.observe(viewLifecycleOwner) { res->
                                        if (res == true) {

                                            // FireBaseToken 받아서 보내기
                                            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                                if (!task.isSuccessful) {
                                                    Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                                                    return@OnCompleteListener
                                                }

                                                // Get new FCM registration token
                                                val token = task.result

                                                // Log and toast
                                                val msg = "FCM Registration Token: $token"
                                                Log.d(ContentValues.TAG, msg)

                                                loginViewModel.sendRegistrationToServer(token.toString())
                                                // update
                                                loginViewModel.updateDeviceToken(token.toString())

                                                val intent = Intent(requireActivity(), HomeActivity::class.java)
                                                // 새로운 태스크에 새로 시작된 액티비티를 넣으며, 기존에 쌓인 태스크가 있다면 모두 제거하여 초기화면으로 돌아감
                                                intent.flags =
                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                startActivity(intent)

                                            })

                                        }
                                    }
                                } else {
                                    // 회원가입이 안되어 있다면
                                    val kakaoId = viewModel.checkUserResponse.value?.kakaoId
                                    val email = viewModel.checkUserResponse.value?.email

                                    // 값을 viewmodel에서 가져와서 signupfrag로 bundle로 넘겨주고
                                    Log.i("BEFOREBUNDLE" , kakaoId.toString())
                                    val bundle = Bundle().apply {
                                        putString("kakaoId", kakaoId)
                                        putString("email", email)
                                    }

                                    // 회원가입창으로 이동
                                    findNavController().navigate(R.id.signUpFragment, bundle)
                                }
                            }
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
            }
        }
    }


}
