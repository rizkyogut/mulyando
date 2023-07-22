package com.rizkym.mulyando.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.rizkym.mulyando.MainActivity
import com.rizkym.mulyando.databinding.ActivityVerificationBinding
import java.util.concurrent.TimeUnit

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding
    lateinit var auth: FirebaseAuth

    private lateinit var inputOTP1: EditText
    private lateinit var inputOTP2: EditText
    private lateinit var inputOTP3: EditText
    private lateinit var inputOTP4: EditText
    private lateinit var inputOTP5: EditText
    private lateinit var inputOTP6: EditText

    private lateinit var OTP: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        auth = FirebaseAuth.getInstance()

        initView()

        binding.resendOTP.setOnClickListener {

            showLoading(true)

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)                 // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS)   // Timeout and unit
                .setActivity(this)                          // Activity (for callback binding)
                .setCallbacks(callbacks)
                .setForceResendingToken(resendToken)       // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

            binding.resendOTP.visibility = View.INVISIBLE
            binding.resendOTP.isEnabled = false

            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                binding.resendOTP.visibility = View.VISIBLE
                binding.resendOTP.isEnabled = true
            }, 60000)
        }

        binding.button.setOnClickListener {

            showLoading(true)

            val otp =
                (inputOTP1.text.toString() + inputOTP2.text.toString() + inputOTP3.text.toString()
                        + inputOTP4.text.toString() + inputOTP5.text.toString() + inputOTP6.text.toString())

            if (otp.length == 6) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    OTP, otp
                )
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show()
            }

            if (otp.isEmpty()) {
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initView() {
        inputOTP1 = binding.otpEditText1
        inputOTP2 = binding.otpEditText2
        inputOTP3 = binding.otpEditText3
        inputOTP4 = binding.otpEditText4
        inputOTP5 = binding.otpEditText5
        inputOTP6 = binding.otpEditText6

        inputOTP1.addTextChangedListener(EditTextWatcher(inputOTP1))
        inputOTP2.addTextChangedListener(EditTextWatcher(inputOTP2))
        inputOTP3.addTextChangedListener(EditTextWatcher(inputOTP3))
        inputOTP4.addTextChangedListener(EditTextWatcher(inputOTP4))
        inputOTP5.addTextChangedListener(EditTextWatcher(inputOTP5))
        inputOTP6.addTextChangedListener(EditTextWatcher(inputOTP6))
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {

            val text = s.toString()

            when (view.id) {
                binding.otpEditText1.id -> if (binding.otpEditText1.text.length == 1) inputOTP2.requestFocus()
                binding.otpEditText2.id -> if (binding.otpEditText2.text.length == 1) inputOTP3.requestFocus() else if (text.isEmpty()) inputOTP1.requestFocus()
                binding.otpEditText3.id -> if (binding.otpEditText3.text.length == 1) inputOTP4.requestFocus() else if (text.isEmpty()) inputOTP2.requestFocus()
                binding.otpEditText4.id -> if (binding.otpEditText4.text.length == 1) inputOTP5.requestFocus() else if (text.isEmpty()) inputOTP3.requestFocus()
                binding.otpEditText5.id -> if (binding.otpEditText5.text.length == 1) inputOTP6.requestFocus() else if (text.isEmpty()) inputOTP4.requestFocus()
                binding.otpEditText6.id -> if (binding.otpEditText6.text.isEmpty()) inputOTP5.requestFocus()
            }
        }

    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            OTP = verificationId
            resendToken = token
            showLoading(false)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Authenticate Successfully", Toast.LENGTH_SHORT).show()
                    intentMain()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                    // Update UI
                }

                showLoading(false)
            }
    }

    private fun intentMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}