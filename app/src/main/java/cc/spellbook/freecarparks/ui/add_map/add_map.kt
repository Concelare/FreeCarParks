package cc.spellbook.freecarparks.ui.add_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cc.spellbook.freecarparks.MainActivity
import cc.spellbook.freecarparks.databinding.FragmentAddMapBinding
import com.google.android.material.snackbar.Snackbar
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody


class add_map : Fragment() {

    // Makes the properties in the layout accessible in code
    private var _binding: FragmentAddMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Sets _binding to the inflated layout
        _binding = FragmentAddMapBinding.inflate(inflater, container, false)

        // Sets the button on click effect
        _binding!!.AddMapButton.setOnClickListener {

            // Checks if Location Name is filled in
            if(_binding!!.LocationName.text.trim().isNullOrEmpty()) {
                Snackbar.make(requireView(), "Missing Location Name...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Checks if Latitude is filled in
            if(_binding!!.Latitude.text.trim().isNullOrEmpty()) {
                Snackbar.make(requireView(), "Missing Latitude...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Checks if longitude is filled in
            if(_binding!!.Longitude.text.trim().isNullOrEmpty()) {
                Snackbar.make(requireView(), "Missing Longitude...", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Creates a new OkHttpClient
            val client = OkHttpClient()

            // Sets up the Form Body Parameters
            val formBody: RequestBody = FormBody.Builder().add("token", MainActivity.token.toString()).add("name", binding!!.LocationName.text.toString()).add("long", binding!!.Longitude.text.toString()).add("lat", binding.Latitude.text.toString()).build()

            // Creating the Request
            val request = Request.Builder()
                    // Sets the URL
                .url("https://spellbook.cc:8443/maps/add")
                    // Adds the body and sets the request as a post request
                .post(formBody)
                    // Builds Request
                .build()
            // Executes Request
            val response = client.newCall(request).execute()

            // Only Accepted Response is good
            if(response.code != 202) {
                // Tells the user it failed to add the map
                Snackbar.make(requireView(), "Failed to Add Map", Snackbar.LENGTH_LONG).show()
            }
            else {
                // Tells the user it successfully added the map
                Snackbar.make(requireView(), "Successfully Added Map", Snackbar.LENGTH_LONG).show()
            }
        }

        // Returns Inflated Layout for this Fragment
        return _binding!!.root
    }
}