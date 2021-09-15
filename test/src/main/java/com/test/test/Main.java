package com.test.test;

import org.json.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class Main 
{

	// sends a request to the SpaceX API and connects to it
	// then creates a HashMap with the Key being the specific ID (rocket ->
	// rocketid, launch -> launchid, etc.)
	// and the value being the JSONObject with that ID
	public static HashMap<String, JSONObject> getHashMap(String str) throws IOException 
	{
		URL url = new URL("https://api.spacexdata.com/v4/" + str);
		URLConnection http = url.openConnection();
		InputStream stream = http.getInputStream();
		JSONArray jsonArray = new JSONArray(new JSONTokener(stream));

		HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

		for (int i = 0; i < jsonArray.length(); i++) 
		{
			JSONObject object = jsonArray.getJSONObject(i);
			map.put(object.getString("id"), object);
		}

		return map;

	}

	
	
	public static void main(String[] args) throws IOException 
	{
		// filtering through the different JSONArrays
		var rockets = getHashMap("rockets");
		var launches = getHashMap("launches");
		var payloads = getHashMap("payloads");

		double load = 0;

		for (var launch : launches.values()) 
		{
			String rocketid = launch.getString("rocket");
			if (rockets.get(rocketid).getString("name").contains("Falcon")) 
			{
				JSONArray payloadIDs;
				payloadIDs = launch.getJSONArray("payloads");

				for (int i = 0; i < payloadIDs.length(); i++) 
				{
					double value = payloads.get(payloadIDs.get(i)).optDouble("mass_kg");

					// optDouble returns NaN if there is no mass_kg given
					if (!Double.isNaN(value))
						load += value;
				}
			}
		}

		System.out.println(load);

	}
}
