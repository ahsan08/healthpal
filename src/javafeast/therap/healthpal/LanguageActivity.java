package javafeast.therap.healthpal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class LanguageActivity extends Activity {

	private RadioGroup langGroup;
	private RadioButton langSelect;
	private Button langButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language);
		langGroup = (RadioGroup) findViewById(R.id.radio_lang);
		langButton = (Button) findViewById(R.id.lang_confirm);
		langButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int selected = langGroup.getCheckedRadioButtonId();
				langSelect = (RadioButton) findViewById(selected);	
				
				SharedPreferences languagePrefs = getSharedPreferences("LANGUAGE_PREFS", 0);
				SharedPreferences.Editor editor = languagePrefs.edit();
				String radioText = (String) langSelect.getText();
				
				
				if(radioText.equals("English"))
					editor.putString("key_language", "");

				else		
					editor.putString("key_language", "bn");

				
				
				editor.putBoolean("key_firstRun", false);
				editor.commit();
				
				
				
				
				Intent signIn = new Intent(getApplicationContext(), SigninActivity.class);
		
				startActivity(signIn);
				finish();
				
			}
		});
	}

}
