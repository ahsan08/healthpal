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
import android.widget.Toast;

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
				if(langSelect.getText()=="Bangla")
					
					editor.putString("key_language", "bn");
				
                
				editor.putBoolean("key_firstRun", false);
				editor.commit();
				
				Toast.makeText(getApplicationContext(), "languageActivity"+ langSelect.getText(), Toast.LENGTH_LONG).show();
				
				
				
				Intent i = new Intent(getApplicationContext(), SigninActivity.class);
				startActivity(i);
				finish();
				
			}
		});
	}

}
