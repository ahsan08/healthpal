package javafeast.therap.healthpal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class Main extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	private static final float DEFAULTZOOM = 11;
	GoogleMap mMap;
	GMapV2Direction md;
	Document doc;

	String duration;
	String distance;
	String end_address;

	//private static String url_markers = "http://1healthpal.fh2web.com/Server/Server/fetch_markers.php";
	private static String url_markers = "http://2healthpal.fh2web.com/Server/fetch_markers.php";
	LatLng ll;

	private static final String TAG_SUCCESS = "success";
//	private static final String TAG_MARKERS = "markers";
//	private static final String TAG_LAT = "lat";
//	private static final String TAG_LONG = "lng";
	private static final String TAG_MARKERS = "entries";
	private static final String TAG_LAT = "hLat";
	private static final String TAG_LONG = "hLng";
	public static final String TAG_HNAME = "hName";

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> markerList;
	JSONArray markers = null;

	LocationClient mLocationClient;
	private Location currentLocation;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		md = new GMapV2Direction();
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();

		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				View v = getLayoutInflater()
						.inflate(R.layout.info_window, null);
				
				String s = marker.getTitle();

				
				TextView distance = (TextView) v.findViewById(R.id.distance);
				
				TextView placeName = (TextView) v.findViewById(R.id.placeName);

				
				placeName.setText(s);
				distance.setText(Main.this.distance);

				// Returning the view containing InfoWindow contents
				return v;

			}
		});

		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				if (hasInternetConnection()) {
					// Toast.makeText(getApplicationContext(), "Hello",
					// Toast.LENGTH_LONG).show();
					LatLng pos = marker.getPosition();
					shortestPath(ll, pos);
					marker.showInfoWindow();
					
					return true;
				} else {
					Toast.makeText(getApplicationContext(), "No Internet Connection",
							Toast.LENGTH_LONG).show();
				}
				return false;
			}
		});

		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker arg0) {
				Dialog d = new Dialog(Main.this);
				d.setContentView(R.layout.marker_dialog_layout);
				d.setTitle("Hospital Details");

				TextView hospitalName, distance, time, lat, lng, address;
				Button btnCallHospital, btnCallAmbulance;

				hospitalName = (TextView) d
						.findViewById(R.id.textViewHospitalName);
				distance = (TextView) d.findViewById(R.id.textViewDistance);
				time = (TextView) d.findViewById(R.id.textViewTime);
				lat = (TextView) d.findViewById(R.id.textViewLatitude);
				lng = (TextView) d.findViewById(R.id.textViewLongitude);
				address = (TextView) d.findViewById(R.id.textViewAddress);
				
				
				btnCallAmbulance = (Button) d
						.findViewById(R.id.buttonCallAmbulance);
				btnCallHospital = (Button) d
						.findViewById(R.id.buttonCallHospital);

				LatLng position = arg0.getPosition();
				
				hospitalName.setText(arg0.getTitle());

				lat.append(String.valueOf(position.latitude));
				lng.append(String.valueOf(position.longitude));
				distance.append(Main.this.distance);
				time.append(duration);
				address.append(end_address);

				btnCallAmbulance.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Uri number = Uri.parse("tel:01712022544");
						Intent intent = new Intent(Intent.ACTION_DIAL, number);
						startActivity(intent);
					}
				});

				btnCallHospital.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Uri number = Uri.parse("tel:01712022544");
						Intent intent = new Intent(Intent.ACTION_DIAL, number);
						startActivity(intent);
					}
				});

				d.show();

			}
		});

	}

	protected boolean hasInternetConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;

	}

	protected void gotoCurrentLocation() {

		if (mLocationClient != null && mLocationClient.isConnected()) {
			currentLocation = mLocationClient.getLastLocation();
		}

		else {
			LocationManager locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager != null) {
				Location lastKnownLocationGPS = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (lastKnownLocationGPS != null) {
					currentLocation = lastKnownLocationGPS;
				}

				else {
					currentLocation = locationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				}
			}
		}

		if (currentLocation == null) {
			Toast.makeText(this, "Current location isn't available",
					Toast.LENGTH_SHORT).show();
			LatLng ll = new LatLng(22.900471, 89.501779);
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,
					DEFAULTZOOM);
			mMap.animateCamera(update);

		} else {
			ll = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());
			MarkerOptions options = new MarkerOptions()
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ic_mylocation))
					.position(ll).title("Your last known location");
			mMap.addMarker(options);

			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,
					DEFAULTZOOM);
			mMap.animateCamera(update);

		}
		puttingMarkers();
	}

	void shortestPath(LatLng fromPosition, LatLng toPosition) {

		doc = md.getDocument(fromPosition, toPosition,
				GMapV2Direction.MODE_DRIVING);
		duration = md.getDurationText(doc);
		distance = md.getDistanceText(doc);
		end_address = md.getEndAddress(doc);

		ArrayList<LatLng> directionPoint = md.getDirection(doc);
		PolylineOptions rectLine = new PolylineOptions().width(4).color(
				Color.RED);

		for (int i = 0; i < directionPoint.size(); i++) {

			rectLine.add(directionPoint.get(i));

		}

		mMap.addPolyline(rectLine);
		Toast.makeText(getApplicationContext(), end_address, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

	}

	@Override
	public void onConnected(Bundle arg0) {

		gotoCurrentLocation();

	}

	private void puttingMarkers() {

		markerList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadingMarkers().execute();

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.first_aid, menu);
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemFirstAid:
			Intent aidIntent = new Intent(getApplicationContext(),
					FirstAidActivity.class);
			startActivity(aidIntent);
			return true;
		case R.id.itemSignOut:
			Intent intent = new Intent(getApplicationContext(),
					SigninActivity.class);
			startActivity(intent);

			finish();
			return true;
		case R.id.itemReload:

			if (hasInternetConnection()) {
				puttingMarkers();
			} else {
				Toast.makeText(this, "No internet connection",
						Toast.LENGTH_SHORT).show();
			}
			return true;
		
		case R.id.itemAboutUs:
			Dialog d = new Dialog(this);
			d.setContentView(R.layout.dialog_about_us);
			d.show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dBuilder = new AlertDialog.Builder(Main.this);

		dBuilder.setIcon(R.drawable.ic_dialog_alert);
		dBuilder.setTitle("Healthpal");
		dBuilder.setMessage("Are you sure you want to quit?");
		dBuilder.setCancelable(false);
		dBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Main.this.finish();
					}
				});

		dBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		AlertDialog quitAlert = dBuilder.create();
		quitAlert.show();
	}

	class LoadingMarkers extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(Main.this);
			pDialog.setMessage("Loading data");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			super.onPreExecute();

		}

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			JSONObject json = jsonParser.makeHttpRequest(url_markers, "GET",
					params);

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// flag = true;
					markers = json.getJSONArray(TAG_MARKERS);

				} else {

				}
			} catch (JSONException e) {
				e.printStackTrace();

			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {

			// dismiss the dialog once done

			for (int i = 0; i < markers.length(); i++) {
				JSONObject c = null;
				try {
					c = markers.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				// Storing each json item in variable
				Double lat = null, lng = null;
				String hospital_name = null;
				try {
					hospital_name = c.getString(TAG_HNAME);
					lat = c.getDouble(TAG_LAT);
					lng = c.getDouble(TAG_LONG);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				LatLng mark = new LatLng(lat, lng);

				MarkerOptions opt = new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.first)).position(mark)
						.title(hospital_name);
				mMap.addMarker(opt);

				// closing this screen
				pDialog.dismiss();

			}
			
			//pDialog.dismiss();
		}

	}

}
