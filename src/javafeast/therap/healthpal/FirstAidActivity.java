package javafeast.therap.healthpal;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewFlipper;


public class FirstAidActivity extends ListActivity {

	ListView listView;
	ViewFlipper viewFlipper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_aid);

		listView = (ListView) findViewById(android.R.id.list);

		String[] values = new String[] { "CPR",
				"Defibrilating with AED", "Chocking",
				"Burn", "Tissue Damage",
				"Fracture", "Trauma",
				"Unconscious" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Dialog d = new Dialog(FirstAidActivity.this);
				d.setContentView(R.layout.firstaid_content_holder);
				viewFlipper = (ViewFlipper) d.findViewById(R.id.viewFlipperContents);
				
				switch (position) {
				case 0:
					d.setTitle(listView.getItemAtPosition(position).toString());
					viewFlipper.setDisplayedChild(0);
					break;
				case 1:
					d.setTitle(listView.getItemAtPosition(position).toString());
					viewFlipper.setDisplayedChild(1);
					break;
				case 2:
					d.setTitle(listView.getItemAtPosition(position).toString());
					viewFlipper.setDisplayedChild(2);
					break;
				case 3:
					d.setTitle(listView.getItemAtPosition(position).toString());
					viewFlipper.setDisplayedChild(3);
					break;
				case 4:
					d.setTitle(listView.getItemAtPosition(position).toString());
					viewFlipper.setDisplayedChild(4);
					break;
				case 5:
					d.setTitle(listView.getItemAtPosition(position).toString());
					viewFlipper.setDisplayedChild(5);
					break;
				case 6:
					d.setTitle(listView.getItemAtPosition(position).toString());
					viewFlipper.setDisplayedChild(6);
					break;
				case 7:
					d.setTitle(listView.getItemAtPosition(position).toString());
					viewFlipper.setDisplayedChild(7);
					break;
				case 8:
					d.setTitle(listView.getItemAtPosition(position).toString());
					viewFlipper.setDisplayedChild(8);
					break;
				default:
					break;
				}
				
				d.show();

			}

		});
	}
}
