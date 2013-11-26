package glay.ash.shortcut2.activity;

import static glay.ash.shortcut2.Constants.*;
import glay.ash.shortcut2.PackageUtils;
import glay.ash.shortcut2.R;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CreateShortcutActivity extends ActionBarActivity implements OnClickListener{
	
	ImageView iconImage;
	RadioButton applicationLabel;
	RadioButton activityNameLabel;
	RadioGroup radioGroupName;
	EditText editTextCustomLabel;
	
	String packageName;
	String activityName;
	String activityLabel;
	
	Bitmap cacheBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_shortcut);
		
		packageName = getIntent().getStringExtra(PACKAGE_NAME_KEY);
		activityLabel = getIntent().getStringExtra(ACTIVITY_LABEL_KEY);
		activityName = getIntent().getStringExtra(ACTIVITY_NAME_KEY);		
		if(packageName == null || activityLabel == null || activityName == null){
			finish();
		}
		
		iconImage = (ImageView)findViewById(R.id.iconButton);
		applicationLabel = (RadioButton)findViewById(R.id.applicationNameLabel);
		activityNameLabel = (RadioButton)findViewById(R.id.activityNameLabel);
		radioGroupName = (RadioGroup)findViewById(R.id.radioGroupName);
		editTextCustomLabel = (EditText)findViewById(R.id.editTextCustomLabel);
		findViewById(R.id.actionLaunch).setOnClickListener(this);
		findViewById(R.id.createShortcut).setOnClickListener(this);
		
		try{
			initialize(packageName, activityLabel);
		}catch(NameNotFoundException e){
			e.printStackTrace();
			finish();
		}
	}
	
	private void initialize(String packageName, String activityName) throws NameNotFoundException {
		PackageManager pm = getPackageManager();
		applicationLabel.setText(PackageUtils.labelFromPackageName(pm, packageName));
		activityNameLabel.setText(activityName);
		cacheBitmap = PackageUtils.iconFromPackageName(pm, packageName);
		iconImage.setImageBitmap(cacheBitmap);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_shortcut, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.actionLaunch:
			launchActivity();
			break;
		case R.id.createShortcut:
			createShortcut();
			break;
		}	
	}
	
	private void launchActivity(){
		try{
			startActivity(PackageUtils.generateBootIntent(packageName, activityName));
		}catch(Exception e){
			Toast.makeText(this, activityLabel +" boot failure", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void createShortcut(){
		try{
			Intent bootIntent = PackageUtils.generateBootIntent(packageName, activityName);
			sendBroadcast(PackageUtils.generateShortcutIntent(bootIntent, getLabelText(), cacheBitmap, false));
		}catch(Exception e){
			Toast.makeText(this, activityLabel+" can't create shortcut", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	private String getLabelText() throws NameNotFoundException {
		switch(radioGroupName.getCheckedRadioButtonId()){
		case R.id.customLabel:
			return editTextCustomLabel.getText().toString();
		case R.id.applicationLabel:
			return PackageUtils.labelFromPackageName(getPackageManager(), packageName);
		case R.id.activityNameLabel:
		default:
			return activityLabel;
		}
	}

}
