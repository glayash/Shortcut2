package glay.ash.shortcut2.fragment;

import glay.ash.shortcut2.ApplicationListLoader;
import glay.ash.shortcut2.R;
import glay.ash.shortcut2.Settings;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
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

	private OnApplicationListClickListener listener;
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
		if(activity instanceof OnApplicationListClickListener){
			listener = (OnApplicationListClickListener)activity;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getLoaderManager().initLoader(0, null, this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		listPosition = getListView().getFirstVisiblePosition();
		super.onDestroyView();
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
			listener.onApplicationItemClick((ApplicationInfo) getListAdapter().getItem(position));
		}
	}

	@Override
	public Loader<List<ApplicationInfo>> onCreateLoader(int identifier, Bundle bundle) {
		//TODO:設定から変更可能にする
//		boolean isUserApplicationOnly = bundle == null ? true : bundle.getBoolean(USER_APPLICATION_ONLY_KEY, true);
//		return new ApplicationListLoader(getActivity(), isUserApplicationOnly);
		return new ApplicationListLoader(getActivity(), Settings.isUserApplicationOnly);
	}

	@Override
	public void onLoadFinished(Loader<List<ApplicationInfo>> loader, List<ApplicationInfo> infos) {
		setListAdapter(new ApplicationAdapter(infos, getActivity()));
		setSelection(listPosition);
	}

	@Override
	public void onLoaderReset(Loader<List<ApplicationInfo>> arg0) {

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

	/**
	 * アプリケーション一覧から項目を選択した際に呼び出されます
	 */
	public interface OnApplicationListClickListener {
		/**
		 * アイテムをタップした際に呼び出されます
		 * @param applicationInfo
		 */
		public void onApplicationItemClick(ApplicationInfo applicationInfo);
	}
}
