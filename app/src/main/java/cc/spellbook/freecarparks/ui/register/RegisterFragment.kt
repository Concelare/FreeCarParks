package cc.spellbook.freecarparks.ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cc.spellbook.freecarparks.MainActivity
import cc.spellbook.freecarparks.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.lang.Exception

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Gets the View Model for RegisterFragment
        val registerViewModel =
            ViewModelProvider(this)[RegisterViewModel::class.java]
        // Sets _binding to the inflated layout
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Sets the layout values equal to the viewmodel ones
        registerViewModel.email =  _binding!!.Email
        registerViewModel.password = _binding!!.RegisterPassword

        // Sets the onclick event for the button
        _binding!!.button.setOnClickListener {
            // Checks if the email has been filled in correctly
            if (registerViewModel.email.text.trim().isNullOrEmpty() || registerViewModel.email.text.toString() == "Email") {
                Snackbar.make(requireView(), "Missing Email Field...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Checks if the password has been filled in correctly
            if (registerViewModel.password.text.trim().isNullOrEmpty() || registerViewModel.email.text.toString() == "Password") {
                Snackbar.make(requireView(), "Missing Password Field...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Checks if the email is a valid email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(registerViewModel.email.text.trim()).matches()) {
                Snackbar.make(requireView(), "Invalid Email...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Checks if the password length is correct
            if(registerViewModel.password.text.trim().length <= 6) {
                Snackbar.make(requireView(), "Password is too short...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {
                // Creates a new OkHttp Client
                val client = OkHttpClient()

                // Sets up the FormBody and Add the Email and Password Parameters
                val formBody: RequestBody = FormBody.Builder().add("email", registerViewModel.email.text.trim().toString()).add("password", registerViewModel.password.text.trim().toString()).build()

                // Sets up the Request with the url, method and body
                val request = Request.Builder()
                    .url("https://spellbook.cc:8443/user/register")
                    .post(formBody)
                    .build()

                // Executes the request and gets the response
                val response: Response = client.newCall(request).execute();

                // Reads the body stream and sets to the value
                val bodyString = response.body.string()

                // Checks if the Account Already Exists or if it contains the user token
                if (bodyString == "Exists") {
                    Snackbar.make(requireView(), "Account Already Exists", Snackbar.LENGTH_LONG).show()
                }
                else if(!bodyString.isNullOrEmpty()) {
                    MainActivity.email = registerViewModel.email.text.toString()
                    MainActivity.token = bodyString
                    Snackbar.make(requireView(), "Successfully Registered", Snackbar.LENGTH_LONG).show()
                }
                else {
                    Snackbar.make(requireView(), "An Error Occurred", Snackbar.LENGTH_LONG).show()
                }
            }
            catch (e: Exception){
                // Catches Errors
                Snackbar.make(requireView(), "Error Occurred When Contacting Register Service...", Snackbar.LENGTH_LONG).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}