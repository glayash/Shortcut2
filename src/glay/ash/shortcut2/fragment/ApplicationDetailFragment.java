package glay.ash.shortcut2.fragment;
import static glay.ash.shortcut2.Constants.*;
import glay.ash.shortcut2.PackageUtils;
import glay.ash.shortcut2.R;
import glay.ash.shortcut2.Settings;

import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ApplicationDetailFragment extends ListFragment implements OnItemLongClickListener{

	private OnActivityListItemClickListener mListener;

	public ApplicationDetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		if(activity instanceof OnActivityListItemClickListener){
			mListener = (OnActivityListItemClickListener) activity;
		}
	}			

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setOnItemLongClickListener(this);
		
		String packageName = getArguments().getString(PACKAGE_NAME_KEY);
		//TODO:設定から変更可能にする
//		boolean canExportedOnly = getArguments().getBoolean(CAN_EXPORTED_ONLY, true);
		boolean canExportedOnly = Settings.canExportedOnly;
		
		if(packageName == null && mListener != null){
			mListener.onPackageNotFound("null");
			return;
		}
		
		try{
			getListView().addHeaderView(inflateHeaderView(getActivity(), packageName), null, false);
			setListAdapter(new ActivityListAdapter(PackageUtils.generateActivitiesList(getActivity(), packageName, canExportedOnly), getActivity()));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			if(mListener != null){
				mListener.onPackageNotFound(packageName);
			}			
		} catch (ActivityNotFoundException e){
			e.printStackTrace();
			if(mListener != null){
				mListener.onActivitiesNotFound(packageName);
			}
		}
	}
	
	private View inflateHeaderView(Context context, String packageName) throws NameNotFoundException{
		LayoutInflater inflater = LayoutInflater.from(context);
		PackageManager pm = context.getPackageManager();
		
		View header = inflater.inflate(R.layout.list_header, null);
		ImageView iv = (ImageView)header.findViewById(R.id.applicationIcon);
		TextView tv1 = (TextView)header.findViewById(R.id.applicationLabel);
		TextView tv2 = (TextView)header.findViewById(R.id.packageNameLabel);

		tv1.setText(PackageUtils.labelFromPackageName(pm, packageName));
		tv2.setText(packageName);
		iv.setImageBitmap(PackageUtils.iconFromPackageName(pm, packageName));
		
		return header;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (null != mListener) {
			 mListener.onActivityItemClick((ActivityInfo) getListAdapter().getItem((int)id));
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
		if(mListener != null){
			mListener.onActivityItemLongClick((ActivityInfo) getListAdapter().getItem((int)id));
		}
		return true;
	}
	
	private static class ActivityListAdapter extends BaseAdapter{
		final private List<ActivityInfo> aInfos;
		final private LayoutInflater inflater;
		final private PackageManager pm;
		
		private ActivityListAdapter(List<ActivityInfo> aInfos, Context c){
			this.aInfos = aInfos;
			this.inflater = LayoutInflater.from(c);
			this.pm = c.getPackageManager();
		}

		@Override
		public int getCount() {
			return aInfos.size();
		}

		@Override
		public ActivityInfo getItem(int position) {
			return aInfos.get(position);
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
			tv1.setText(getItem(position).loadLabel(pm));
			tv1.setTextColor(isEnabled(position) ? Color.BLACK : Color.LTGRAY);
			tv2.setText(getItem(position).name);
			tv2.setTextColor(isEnabled(position) ? Color.GRAY : Color.LTGRAY);
			
			return convertView;
		}
		
		@Override
		public boolean isEnabled(int position) {
			return getItem(position).exported;
		}
		
	}
	
	/**
	 * Activity一覧のアイテムを選択した際に呼び出されるリスナ
	 */
	public interface OnActivityListItemClickListener {
		/**
		 * アイテムをタップした際に呼び出されます
		 * @param activityInfo
		 */
		public void onActivityItemClick(ActivityInfo activityInfo);
		
		/**
		 * アイテムをロングタップした際に呼び出されます
		 * @param activityInfo
		 */
		public void onActivityItemLongClick(ActivityInfo activityInfo);
		
		/**
		 * 指定したパッケージ情報が存在しない際に呼び出されます
		 * @param packageName
		 */
		public void onPackageNotFound(String packageName);
		
		/**
		 * 指定したパッケージがActivityを持たない時に呼び出されます。
		 * @param packageName
		 */
		public void onActivitiesNotFound(String packageName);
	}

}
