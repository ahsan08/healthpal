package javafeast.therap.healthpal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;


public class SplashActivity extends Activity {

	public class PreloadData extends AsyncTask<String, String, String> {

		private boolean signinStatus;
		@SuppressWarnings("unused")
		private String username, password;

		@Override
		protected void onPreExecute() {
			SharedPreferences signinPrefs = getSharedPreferences(
					"SIGNIN_PREFS", 0);
			signinStatus = signinPrefs.getBoolean("key_signinData", false);
			username = signinPrefs.getString("key_username", "");
			password = signinPrefs.getString("key_password", "");
		}

		@Override
		protected String doInBackground(String... params) {

			if (signinStatus && hasNetworkConnection()) {
				signIn();
			} else {
				Intent loginIntent = new Intent(getApplicationContext(),
						SigninActivity.class);
				startActivity(loginIntent);
			}

			return null;
		}

		private boolean hasNetworkConnection() {

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

		private void signIn() {
			Thread timer = new Thread() {

				@Override
				public void run() {
					try {
						sleep(750);

					} catch (InterruptedException e) {
						Toast.makeText(getBaseContext(),
								"Error: " + e.getMessage().toString(),
								Toast.LENGTH_SHORT).show();
					}

					// finally if signed in then go to map view
					// go to sign in view
					finally {
						Intent loginIntent = new Intent(
								getApplicationContext(), Main.class);
						startActivity(loginIntent);
					}
				};
			};

			timer.start();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new PreloadData().execute();

	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
