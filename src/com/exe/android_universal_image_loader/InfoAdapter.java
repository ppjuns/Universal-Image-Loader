package com.exe.android_universal_image_loader;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.exe.android_universal_image_loader.R.id;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class InfoAdapter extends ArrayAdapter<Info> {
	private Context con;
	int resid;
	private Dialog dialog;

	public InfoAdapter(Context context, int resource, List<Info> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		resid = resource;
		con=context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		 DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.displayer(new RoundedBitmapDisplayer(20))
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		 Info info = getItem(position);
		View view;
		 final ViewHolder holder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resid, null);
			holder = new ViewHolder();

			holder.id = (TextView) view.findViewById(R.id.id);
			holder.datatime = (TextView) view.findViewById(R.id.datatime);
			holder.img = (ImageView) view.findViewById(R.id.imgurl);
			holder.client = (TextView) view.findViewById(R.id.client);
			holder.message = (TextView) view.findViewById(R.id.message);
			holder.img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					View dialogv = LayoutInflater.from(con).inflate(R.layout.imageview_dialog, null);
					dialog = new Dialog(con, R.style.Photo_dialog);
					dialog.setContentView(dialogv);
					dialog.setCanceledOnTouchOutside(true);
					dialog.setCancelable(true);
					dialog.show();
					
					//ÉèÖÃdialogµÄ¿í¸ß
					WindowManager mWM = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
					Display display = mWM.getDefaultDisplay();
					Window w = dialog.getWindow();
					WindowManager.LayoutParams lp = w.getAttributes();
					lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
					lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
					w.setAttributes(lp);	
					ImageView image=(ImageView)dialogv.findViewById(R.id.imageView1);
					
				}
			});
			view.setTag(holder);

		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();

		}

		
		holder.id.setText(info.getId());
		holder.datatime.setText(info.getDatetime());
		holder.client.setText(info.getClient());
		holder.message.setText(info.getMessage());

		ImageLoader.getInstance().displayImage(info.getImgurl(), holder.img,
				options);
		return view;

	}

	public class ViewHolder {
		ImageView img;
		TextView id;
		TextView datatime;
		TextView client;
		TextView message;

	}
}
