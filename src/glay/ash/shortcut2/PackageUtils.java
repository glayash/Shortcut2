package glay.ash.shortcut2;

import static glay.ash.shortcut2.Constants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * パッケージ情報を扱うユーティリティメソッド群を定義したクラスです。
 */
public class PackageUtils {
	
	/**
	 * 起動用のインテントを生成します
	 * @param activityInfo
	 * @return
	 */
	public static Intent generateBootIntent(ActivityInfo activityInfo) {
		return generateBootIntent(activityInfo.packageName, activityInfo.name);
	}
	
	/**
	 * 起動用のインテントを生成します
	 * @param packageName
	 * @param activityName
	 * @return
	 */
	public static Intent generateBootIntent(String packageName, String activityName) {
		Intent i = new Intent();
		i.setClassName(packageName, activityName);
		i.setAction(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return i;
	}

	/**
	 * 指定したActivityInfoのショートカットをホーム画面に作成します
	 * @param c
	 * @param activityInfo
	 * @throws Exception
	 */
	public static void generateShortcut(Context c, ActivityInfo activityInfo) throws Exception{
		generateShortcut(c, activityInfo.packageName, activityInfo.name);
	}
	
	/**
	 * 指定したActivityInfoのショートカットをホーム画面に作成します
	 * @param c
	 * @param packageName
	 * @param activityName
	 * @throws Exception
	 */
	public static void generateShortcut(Context c, String packageName, String activityName) throws Exception{
		PackageManager pm = c.getPackageManager();
		Intent bootIntent = generateBootIntent(packageName, activityName);
		Bitmap shortcutIcon = iconFromPackageName(pm, packageName);
		String shortcutName = labelFromPackageName(pm, packageName);
		Intent shortcutIntent = generateShortcutIntent(bootIntent, shortcutName, shortcutIcon, false);
		c.sendBroadcast(shortcutIntent);
	}
	
	/**
	 * パッケージ名からActivityのリストを返却します
	 * @param context
	 * @param packageName
	 * @param canExportedOnly
	 * @return
	 * @throws NameNotFoundException
	 * @throws ActivitiesNotFoundException 
	 */
	public static List<ActivityInfo> generateActivitiesList(Context context, String packageName, boolean canExportedOnly) throws NameNotFoundException, ActivityNotFoundException{
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

		if(info == null || info.activities == null || info.activities.length == 0){
			throw new ActivityNotFoundException("Activity is not included in "+ info.packageName);
		}
		
		List<ActivityInfo> infos = Arrays.asList(info.activities);

		if(canExportedOnly){
			List<ActivityInfo> temp = new ArrayList<ActivityInfo>();
			Iterator<ActivityInfo> iterator = infos.iterator();
			while(iterator.hasNext()){
				ActivityInfo aInfo = iterator.next();
				if(aInfo.exported){
					temp.add(aInfo);
				}
			}
			infos = temp;
		}

		Collections.sort(infos, new Comparator<ActivityInfo>() {
			@Override
			public int compare(ActivityInfo lhs, ActivityInfo rhs) {
				if(lhs.exported ^ rhs.exported){
					return lhs.exported ? -1 : 1;
				}else{
					return lhs.name.compareTo(rhs.name);						
				}
			}
		});
			
		return infos;
	}
	
	/**
	 * パッケージ名からアプリケーション名を取得します
	 * @param pm
	 * @param packageName
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String labelFromPackageName(PackageManager pm, String packageName) throws NameNotFoundException {
		PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
		return pm.getApplicationLabel(packageInfo.applicationInfo).toString();
	}
	
	/**
	 * パッケージ名からアプリケーションアイコンを取得します
	 * @param pm
	 * @param packageName
	 * @return
	 * @throws NameNotFoundException
	 */
	public static Bitmap iconFromPackageName(PackageManager pm, String packageName) throws NameNotFoundException {
		Drawable d = pm.getApplicationIcon(packageName);
		return ((BitmapDrawable)d).getBitmap();
	}
	
	/**
	 * アクティビティ情報から、アクティビティ名を取得します
	 * @param pm
	 * @param activityInfo
	 * @return
	 */
	public static String activityLabelFromActivityInfo(PackageManager pm, ActivityInfo activityInfo){
		return activityInfo.loadLabel(pm).toString();
	}
	
	/**
	 * ショートカット作成を行うインテントを生成します
	 * @param intent
	 * @param name
	 * @param icon
	 * @return
	 */
	public static Intent generateShortcutIntent(Intent intent, String name, Bitmap icon, boolean duplicate){
		Intent i = new Intent();
		i.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		i.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		i.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
		i.putExtra(EXTRA_SHORTCUT_DUPLICATE, duplicate);
		i.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		return i;
	}
}
