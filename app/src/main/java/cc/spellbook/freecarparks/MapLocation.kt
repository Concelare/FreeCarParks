package cc.spellbook.freecarparks

import com.fasterxml.jackson.annotation.JsonProperty

// Turns the json response into a object using Jackson
class MapLocation(@JsonProperty("name")var name: String, @JsonProperty("lon")var lon: Double, @JsonProperty("lat")var lat: Double) {

}