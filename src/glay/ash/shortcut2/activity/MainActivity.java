package glay.ash.shortcut2.activity;

import static glay.ash.shortcut2.Constants.*;
import glay.ash.shortcut2.PackageUtils;
import glay.ash.shortcut2.R;
import glay.ash.shortcut2.fragment.ApplicationDetailFragment;
import glay.ash.shortcut2.fragment.ApplicationDetailFragment.OnActivityListItemClickListener;
import glay.ash.shortcut2.fragment.ApplicationListFragment;
import glay.ash.shortcut2.fragment.ApplicationListFragment.OnApplicationListClickListener;
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

public class MainActivity extends ActionBarActivity implements OnApplicationListClickListener, OnActivityListItemClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//初回生成時にアプリケーションリストのFragmentを表示
		//画面回転などでActivityが再生成される場合は、Fragmentのバックスタックで表示しているリストを判定しUpボタンの表示を切り替える
		if(savedInstanceState == null){
			ApplicationListFragment fragment = new ApplicationListFragment();

			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.fragmentContainer, fragment)
			.commit();
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
	public void onApplicationItemClick(ApplicationInfo applicationInfo) {
		//取得したアプリケーション情報から、ActivityリストのFragmentを生成する
		ApplicationDetailFragment fragment = new ApplicationDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString(PACKAGE_NAME_KEY, applicationInfo.packageName);
		fragment.setArguments(bundle);
		fragment.setRetainInstance(true);

		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.fragmentContainer, fragment)
		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
		.addToBackStack(null)
		.commit();

		//アクションバーのUpボタンでアプリケーションリストに戻す
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onActivityItemClick(ActivityInfo activityInfo) {
		try{		
			Intent i = new Intent(this, CreateShortcutActivity.class);
			i.putExtra(PACKAGE_NAME_KEY, activityInfo.packageName);
			i.putExtra(ACTIVITY_LABEL_KEY, PackageUtils.activityLabelFromActivityInfo(getPackageManager(), activityInfo));
			i.putExtra(ACTIVITY_NAME_KEY, activityInfo.name);
			startActivity(i);
			
		}catch(Exception e){
			Toast.makeText(this, activityInfo.name+" can't create shortcut", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityItemLongClick(ActivityInfo activityInfo) {
		try{
			PackageUtils.generateShortcut(this, activityInfo);
		}catch(Exception e){
			Toast.makeText(this, activityInfo.name+" can't create shortcut", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Override
	public void onPackageNotFound(String packageName) {
		Toast.makeText(this, packageName+" not found", Toast.LENGTH_SHORT).show();
		popFragment();
	}

	@Override
	public void onActivitiesNotFound(String packageName) {
		Toast.makeText(this, packageName+" has not Activities", Toast.LENGTH_SHORT).show();
		popFragment();
	}

	/**
	 * ActivityリストFragmentをpopして、ApplicationリストのFragmentを表示させます<br />
	 * アクションバーのUpボタン表示を切り替えます
	 */
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
