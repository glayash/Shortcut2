package glay.ash.shortcut2.activity;

import static glay.ash.shortcut2.Constants.*;
import glay.ash.shortcut2.R;
import glay.ash.shortcut2.fragment.ApplicationDetailFragment;
import glay.ash.shortcut2.fragment.ApplicationDetailFragment.OnActivitySelectedListener;
import glay.ash.shortcut2.fragment.ApplicationListFragment;
import glay.ash.shortcut2.fragment.ApplicationListFragment.OnApplicationSelectedListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnApplicationSelectedListener, OnActivitySelectedListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(savedInstanceState == null){
			FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
			ApplicationListFragment fragment = new ApplicationListFragment();
			tran.replace(R.id.fragmentContainer, fragment).commit();
		}else{
			boolean showDetails = getSupportFragmentManager().getBackStackEntryCount() != 0;
			getSupportActionBar().setDisplayHomeAsUpEnabled(showDetails);
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			popFragment();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onApplicationSelected(ApplicationInfo applicationInfo) {
		FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
		ApplicationDetailFragment fragment = new ApplicationDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_KEY_PACKAGE_NAME, applicationInfo.packageName);
		fragment.setArguments(bundle);
		tran.replace(R.id.fragmentContainer, fragment);
		tran.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		tran.addToBackStack(null);
		tran.commit();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onActivitySelected(ActivityInfo activityInfo) {
		Intent i = new Intent();
		i.setClassName(activityInfo.packageName, activityInfo.name);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try{
			startActivity(i);
		}catch(Exception e){
			Toast.makeText(this, activityInfo.name+" boot failure", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPackageNotFound() {
		popFragment();
	}
	
	private void popFragment(){
		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack();
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		}
		return super.onKeyDown(keyCode, event);
	}
}
