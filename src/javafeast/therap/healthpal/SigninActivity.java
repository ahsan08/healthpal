package javafeast.therap.healthpal;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SigninActivity extends Activity implements OnClickListener {

	public class signinUser extends AsyncTask<String, String, String> {

		boolean flag;

		ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();

//		private static final String SIGNIN_URL = "http://1healthpal.fh2web.com/Server/Server/user_login.php";
		private static final String SIGNIN_URL = "http://2healthpal.fh2web.com/Server/log_in_user.php";
		private static final String TAG_SUCCESS = "success";

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(SigninActivity.this);
			pDialog.setMessage("Please wait while you are signed in");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			List<NameValuePair> parameter = new ArrayList<NameValuePair>();
			parameter.add(new BasicNameValuePair("mail", username));
			parameter.add(new BasicNameValuePair("pass", password));

			JSONObject json = jsonParser.makeHttpRequest(SIGNIN_URL, "POST",
					parameter);

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					flag = true;
				} else {
					flag = false;
				}
			} catch (JSONException e) {
				Toast.makeText(getApplicationContext(),
						"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			pDialog.dismiss();

			if (flag) {

				Toast.makeText(getApplicationContext(),
						"You are now signed in!", Toast.LENGTH_LONG).show();

				Intent intent = new Intent(getApplicationContext(), Main.class);
				startActivity(intent);
			} else {

				AlertDialog.Builder dBuilder = new AlertDialog.Builder(
						SigninActivity.this);

				dBuilder.setIcon(R.drawable.ic_dialog_alert);
				dBuilder.setTitle("Sign In failed!");
				dBuilder.setMessage("You are not signed in! Please check your username and password are correct then try again.");
				dBuilder.setNeutralButton("Ok",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				dBuilder.setCancelable(true);

				AlertDialog signInAlert = dBuilder.create();
				signInAlert.show();

			}
		}

	}

	EditText etUsername, etPassword;
	Button signinButton;
	TextView tvSignup;
	CheckBox checkSignedIn;

	String username, password;
	boolean isSigninSaving;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);

		initializeComponent();
	}

	private void initializeComponent() {
		etUsername = (EditText) findViewById(R.id.editTextUsername);
		etPassword = (EditText) findViewById(R.id.editTextPassword);
		signinButton = (Button) findViewById(R.id.buttonSignin);
		tvSignup = (TextView) findViewById(R.id.textViewSignUp);
		checkSignedIn = (CheckBox) findViewById(R.id.checkBoxRememberLogin);

		SpannableString content = new SpannableString("Sign Up Now");
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		tvSignup.setText(content);

		signinButton.setOnClickListener(this);
		tvSignup.setOnClickListener(this);

		checkSignedIn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					isSigninSaving = true;
				} else {
					isSigninSaving = false;
				}

			}
		});

		SharedPreferences signinPrefs = getSharedPreferences("SIGNIN_PREFS", 0);

		etUsername.setText(signinPrefs.getString("key_username", ""));
		etPassword.setText(signinPrefs.getString("key_password", ""));
		if (!etUsername.getText().toString().isEmpty()
				&& !etPassword.getText().toString().isEmpty()) {

			checkSignedIn.setChecked(true);
		} else {
			checkSignedIn.setChecked(false);
		}

	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dBuilder = new AlertDialog.Builder(
				SigninActivity.this);

		dBuilder.setIcon(R.drawable.ic_dialog_alert);
		dBuilder.setTitle("Healthpal");
		dBuilder.setMessage("Are you sure you want to quit?");
		dBuilder.setCancelable(false);
		dBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SigninActivity.this.finish();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonSignin:

			username = etUsername.getText().toString();
			password = etPassword.getText().toString();

			if (hasNetworkConnection()) {

				if (username.length() > 0 && password.length() > 0) {
					// Toast.makeText(getBaseContext(), "signing in",
					// Toast.LENGTH_SHORT).show();
					new signinUser().execute();

					if (isSigninSaving) {
						saveSignin();
					}

				} else {
					Toast.makeText(getBaseContext(),
							"Empty fields! Can't sign in.", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getBaseContext(),
						"You are not connected to the Internet",
						Toast.LENGTH_SHORT).show();
			}

			break;

		case R.id.textViewSignUp:

			Intent signupIntent = new Intent(getApplicationContext(),
					SignupActivity.class);
			startActivity(signupIntent);
			break;
		}
	}

	private void saveSignin() {
		SharedPreferences signinPrefs = getSharedPreferences("SIGNIN_PREFS", 0);
		SharedPreferences.Editor editor = signinPrefs.edit();

		editor.putString("key_username", username);
		editor.putString("key_password", password);
		editor.putBoolean("key_signinData", true);

		editor.commit();
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
}
