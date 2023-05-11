package cc.spellbook.freecarparks.ui.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import cc.spellbook.freecarparks.MainActivity
import cc.spellbook.freecarparks.MapLocation
import cc.spellbook.freecarparks.databinding.FragmentMapBinding
import cc.spellbook.freecarparks.responses.MapsResponse
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private var map: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Sets _binding to an Inflated version of the layout
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        // Gets the Activity Context
        val ctx: Context = requireContext()

        // Gets the Configuration for Osmdroid
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        // Get the map from binding
        map = _binding!!.map
        // Set The Source for the Tiles of the map
        map!!.setTileSource(TileSourceFactory.MAPNIK);
        // Turns on the built in zoom controls
        map!!.setBuiltInZoomControls(true);
        // Enables the multi touch controls
        map!!.setMultiTouchControls(true);

        // If token is null it doesn't try to get the maps
        if(MainActivity.token != null) {
            // Create a new OkHttp Client
            val client = OkHttpClient()

            // Sets up the formbody with the token parameter
            val formBody: RequestBody = FormBody.Builder().add("token", MainActivity.token.toString()).build()

            // Sets up the request, sets the url the method and paramets
            val request = Request.Builder()
                .url("https://spellbook.cc:8443/maps/get")
                .post(formBody)
                .build()

            // Executes the request and gets the response
            val response: Response = client.newCall(request).execute()

            // Checks the response code
            if (response.code != 203) {
                // gets the body stream and sets it to the value
                val bodyString = response.body.string()

                // Creates a Jackson ObjectMapper
                val objectMapper = ObjectMapper()
                // Deserializes the bodyString into an Array of MapLocation
                val maps = objectMapper.readValue(bodyString, Array<MapLocation>::class.java)

                // Creates an ArrayList to store the Markers
                val items = ArrayList<OverlayItem>()

                // Goes through each of the map locations recieved and creates them into markers
                maps.forEach {
                    items.add(OverlayItem(it.name, "Free Car Park", GeoPoint(it.lat, it.lon)))
                }

                // Creates the Overlay with Focus Menus
                val mOverlay: ItemizedOverlayWithFocus<OverlayItem> =
                    ItemizedOverlayWithFocus<OverlayItem>(requireContext(), items,
                        object : OnItemGestureListener<OverlayItem?> {
                            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                                return true
                            }

                            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                                return false
                            }
                        });

                // Enabled focus mode on tap allowing the name and description to be seen
                mOverlay.setFocusItemsOnTap(true)

                // Adds the overlay to the map
                map!!.overlays.add(mOverlay)
            }

        }

        // Checks if the app has permissions to get location
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            // Gets the location service
            val lm = getSystemService(requireContext(), LocationManager::class.java)
            // Gets the last known location
            val location = lm!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            // Gets the longitude or takes the default one
            val longitude = location?.longitude ?: -0.753590
            // Gets the latitude or takes the default one
            val latitude = location?.latitude ?: 51.627338

            // Gets the Map Controller
            val mapController = map!!.controller
            // Sets the Starting Zoom
            mapController.setZoom(16.0)
            // Creates the Starting Point
            val startPoint = GeoPoint(latitude, longitude)
            // Sets the starting point
            mapController.setCenter(startPoint)
        }

        // Returns Inflated Layout for this Fragment
        return binding.root
    }
}