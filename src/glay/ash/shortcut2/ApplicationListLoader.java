package glay.ash.shortcut2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;

/**
 * インストール中のパッケージのリストを取得するローダーです。
 */
public class ApplicationListLoader extends AsyncTaskLoader<List<ApplicationInfo>> {

	private final PackageManager pm;
	private boolean isUserApplicationOnly;
	private List<ApplicationInfo> cache;

	public ApplicationListLoader(Context context, Boolean isUserApplicationOnly) {
		super(context);
		this.pm = context.getPackageManager();
		this.isUserApplicationOnly = isUserApplicationOnly;
	}

	public ApplicationListLoader(Context context) {
		this(context, true);
	}

	@Override
	public List<ApplicationInfo> loadInBackground() {
		List<PackageInfo> work = pm.getInstalledPackages(PackageManager.GET_META_DATA);
		Iterator<PackageInfo> iterator = work.iterator();
		while(iterator.hasNext()){
			PackageInfo info = iterator.next();
			if(info.applicationInfo == null || isUserApplicationOnly && (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
				iterator.remove();
			}
		}

		//最終アップデート日の降順でソート
		Collections.sort(work, new Comparator<PackageInfo>(){
			@Override
			public int compare(PackageInfo lhs, PackageInfo rhs) {
				return Long.valueOf(rhs.lastUpdateTime).compareTo(lhs.lastUpdateTime);
			}			
		});

		List<ApplicationInfo> result = new ArrayList<ApplicationInfo>();
		for(PackageInfo info : work){
			result.add(info.applicationInfo);
		}
		cache = result;
		return result;
	}

	@Override
	public void deliverResult(List<ApplicationInfo> data) {
		super.deliverResult(data);
	}

	@Override
	protected void onStartLoading() {
		if(cache != null){
			deliverResult(cache);
		}else{
			forceLoad();
		}
	}

	@Override
	protected void onReset() {
		super.onReset();
		cache = null;
	}
}
