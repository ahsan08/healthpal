package javafeast.therap.healthpal;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

public class GetphNumber extends Activity {
    TextView t;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jihadi);
		t = (TextView) findViewById(R.id.textPhone);
		TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		Toast.makeText(getApplicationContext(), mPhoneNumber, Toast.LENGTH_LONG).show();
		
	}

	

}
