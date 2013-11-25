package glay.ash.shortcut2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class IntentUtil {
	
	/**
	 * 起動用のインテントを生成します
	 * @param activityInfo
	 * @return
	 */
	public static Intent generateBootIntent(ActivityInfo activityInfo) {
		Intent i = new Intent();
		i.setClassName(activityInfo.packageName, activityInfo.name);
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
		PackageManager pm = c.getPackageManager();
		Intent bootIntent = generateBootIntent(activityInfo);
		Bitmap shortcutIcon = iconFromPackageName(pm, activityInfo.packageName);
		String shortcutName = labelFromPackageName(pm, activityInfo.packageName);
		Intent shortcutIntent = generateShortcutIntent(bootIntent, shortcutName, shortcutIcon);
		c.sendBroadcast(shortcutIntent);
	}
	
	/**
	 * パッケージ名からアプリケーション名を取得します
	 * @param pm
	 * @param packageName
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String labelFromPackageName(PackageManager pm, String packageName)throws NameNotFoundException {
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
	
	private static Intent generateShortcutIntent(Intent intent, String name, Bitmap icon){
		Intent i = new Intent();
		i.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		i.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		i.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
		i.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		return i;
	}	
}
