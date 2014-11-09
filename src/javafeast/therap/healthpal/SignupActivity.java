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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SignupActivity extends Activity implements OnClickListener {

	public class SignUpUser extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();

//		private static final String SIGNUP_URL = "http://1healthpal.fh2web.com/Server/Server/item_insert.php";
		private static final String SIGNUP_URL = "http://2healthpal.fh2web.com/Server/sign_up_user.php";
		private static final String TAG_SUCCESS = "success";
		boolean flag;

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(SignupActivity.this);
			pDialog.setMessage("Please wait.\nYour account is being created...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			List<NameValuePair> parameters = new ArrayList<NameValuePair>();

			parameters.add(new BasicNameValuePair("mail", email));
			parameters.add(new BasicNameValuePair("pass", password));
			parameters.add(new BasicNameValuePair("blood", blood_group));

			JSONObject json = jsonParser.makeHttpRequest(SIGNUP_URL, "POST",
					parameters);

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
				Toast.makeText(
						getApplicationContext(),
						"You are now signed up!\nSign in using your new username and password",
						Toast.LENGTH_LONG).show();

				Intent intent = new Intent(getApplicationContext(),
						SigninActivity.class);
				startActivity(intent);

			} else {

				AlertDialog.Builder dBuilder = new AlertDialog.Builder(
						SignupActivity.this);

				dBuilder.setIcon(R.drawable.ic_dialog_alert);
				dBuilder.setTitle("Sign Up failed!");
				dBuilder.setMessage("Could not sign you up! Please check your network connection or try again later.");
				dBuilder.setNeutralButton("Ok",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				dBuilder.setCancelable(true);

				AlertDialog signupAlert = dBuilder.create();
				signupAlert.show();
			}
		}

	}

	EditText etEmail, etPassword, etBloodGroup;
	Button btnSignup;

	String email, password, blood_group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		initializeComponent();
	}

	private void initializeComponent() {
		etEmail = (EditText) findViewById(R.id.editTextEmail);
		etPassword = (EditText) findViewById(R.id.editTextFirstPassword);
		etBloodGroup = (EditText) findViewById(R.id.editTextBloodgroup);
		btnSignup = (Button) findViewById(R.id.buttonSignUp);

		btnSignup.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.buttonSignUp:

			email = etEmail.getText().toString();
			password = etPassword.getText().toString();
			blood_group = etBloodGroup.getText().toString();

			if (hasNetworkConnection()) {
				if (email.length() > 0 && password.length() > 0
						&& blood_group.length() > 0) {
					new SignUpUser().execute();
				} else {
					Toast.makeText(
							SignupActivity.this,
							"Can not sign up!\nOne or more required field is left blank.",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(SignupActivity.this,
						"You are not connected to the Internet",
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
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
