package cc.spellbook.freecarparks.ui.Login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cc.spellbook.freecarparks.MainActivity
import cc.spellbook.freecarparks.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response


class LoginFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Gets View Model
        val loginViewModel: LoginViewModel =
            ViewModelProvider(this)[LoginViewModel::class.java]

        // Sets _binding to a inflated FragmentHomeBinding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Sets the password and email in the view model to the ones in layout
        loginViewModel.email = _binding!!.EmailAddress
        loginViewModel.password = _binding!!.Password

        // Sets the Onclick Event for the login button
        _binding!!.LoginButton.setOnClickListener {
            // Checks if the email is put in correctly
            if (loginViewModel.email.text.trim().isNullOrEmpty() || loginViewModel.email.text.toString() == "Email") {
                Snackbar.make(requireView(), "Missing Email Field...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Checks if the password is input correctly
            if (loginViewModel.password.text.trim().isNullOrEmpty() || loginViewModel.password.text.toString() == "Password") {
                Snackbar.make(requireView(), "Missing Password Field...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Checks if the email is a valid email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(loginViewModel.email.text.trim()).matches()) {
                Snackbar.make(requireView(), "Invalid Email...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Checks if the password length is correct
            if(loginViewModel.password.text.trim().length <= 6) {
                Snackbar.make(requireView(), "Password is too short...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

           try {
               // Creates a new OKHttp Client
               val client = OkHttpClient()

               // Sets up the parameters for the request
               val formBody: RequestBody = FormBody.Builder().add("email", loginViewModel.email.text.trim().toString()).add("password", loginViewModel.password.text.trim().toString()).build()

               // Creates the Request for Logging in using a post request and the parameters
               val request = Request.Builder()
                   .url("https://spellbook.cc:8443/user/login")
                   .post(formBody)
                   .build()

               // Executes the request and gets the response
               val response: Response = client.newCall(request).execute()

               // Since the body is a stream it needs to be set to a value
               val bodyString = response.body.string()

               // Checks the Response to see if it successfully logged in
               if (bodyString == "No Account Found") {
                   Snackbar.make(requireView(), "No Account Found", Snackbar.LENGTH_LONG).show()
               }
               else if (bodyString == "Incorrect Password") {
                   Snackbar.make(requireView(), "Incorrect Password", Snackbar.LENGTH_LONG).show()
               }
               else if (!bodyString.isNullOrEmpty()) {
                   MainActivity.email = loginViewModel.email.text.toString()
                   MainActivity.token = bodyString.trim()
                   Snackbar.make(requireView(), "Successfully Logged In", Snackbar.LENGTH_LONG).show()
               }
           }
           catch (e: Exception){
               Log.d("HTTPS", "Error Occurred ${e.toString()}");
               Snackbar.make(requireView(), "Error Occured Contacting Login Service...", Snackbar.LENGTH_LONG).show()
           }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}