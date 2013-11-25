package glay.ash.shortcut2.fragment;

import glay.ash.shortcut2.R;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ApplicationListFragment extends ListFragment implements LoaderCallbacks<List<ApplicationInfo>>{
	
	final private static String LIST_POSITION = "list-position";
	private OnApplicationSelectedListener listener;
	private int listPosition = 0;
	
	public ApplicationListFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		
		if(savedInstanceState == null){
			return;
		}
		
		listPosition = savedInstanceState.getInt(LIST_POSITION, 0);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setSelection(listPosition);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(isVisible()){
			outState.putInt(LIST_POSITION, getListView().getFirstVisiblePosition());
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnApplicationSelectedListener){
			listener = (OnApplicationSelectedListener)activity;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getLoaderManager().initLoader(0, null, this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if(listener != null){
			listener.onApplicationSelected((ApplicationInfo) getListAdapter().getItem(position));
		}
	}
	
	@Override
	public Loader<List<ApplicationInfo>> onCreateLoader(int arg0, Bundle arg1) {
		ListLoader loader = new ListLoader(getActivity());
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<ApplicationInfo>> loader, List<ApplicationInfo> infos) {
		setListAdapter(new ApplicationAdapter(infos, getActivity()));
	}

	@Override
	public void onLoaderReset(Loader<List<ApplicationInfo>> arg0) {
		
	}
	
	private static class ListLoader extends AsyncTaskLoader<List<ApplicationInfo>>{
		private final Context c;
		private List<ApplicationInfo> cache;

		public ListLoader(Context context) {
			super(context);
			c = context.getApplicationContext();
		}

		@Override
		public List<ApplicationInfo> loadInBackground() {			
			List<ApplicationInfo> result = new ArrayList<ApplicationInfo>();
			PackageManager pm = c.getPackageManager();
			for(ApplicationInfo info : pm.getInstalledApplications(PackageManager.GET_META_DATA)){
				if((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
					result.add(info);
				}
			}
			return result;
		}
		
		@Override
		public void deliverResult(List<ApplicationInfo> data) {
			cache = data;
			super.deliverResult(data);
		}
		
		@Override
		protected void onStartLoading() {
			if(cache != null){
				deliverResult(cache);
			}else{
				super.onStartLoading();
			}
		}
		
		@Override
		protected void onReset() {
			super.onReset();
			cache = null;
		}
	}
	
	private static class ApplicationAdapter extends BaseAdapter{
		final List<ApplicationInfo> appInfos;
		final LayoutInflater inflater;
		final PackageManager pm;
		final float padding;
		
		public ApplicationAdapter(List<ApplicationInfo> appInfos, Context c) {
			this.appInfos = appInfos;
			this.inflater = LayoutInflater.from(c);
			this.pm = c.getPackageManager();
			this.padding = c.getResources().getDimension(R.dimen.standard_padding);
		}

		@Override
		public int getCount() {
			return appInfos.size();
		}

		@Override
		public ApplicationInfo getItem(int position) {
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			if(convertView == null){
				convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
			}
			
			TextView tv1 = (TextView)convertView.findViewById(android.R.id.text1);			
			TextView tv2 = (TextView)convertView.findViewById(android.R.id.text2);
			
			ApplicationInfo info = getItem(position);
			tv1.setText(info.loadLabel(pm).toString());
			tv1.setCompoundDrawablesWithIntrinsicBounds(pm.getApplicationIcon(info), null, null, null);
			tv1.setGravity(Gravity.CENTER_VERTICAL);
			tv1.setCompoundDrawablePadding(Math.round(padding));
			tv2.setText(info.packageName);
			
			return convertView;
		}		
	}
	
	public interface OnApplicationSelectedListener {
		public void onApplicationSelected(ApplicationInfo applicationInfo);
	}
}
