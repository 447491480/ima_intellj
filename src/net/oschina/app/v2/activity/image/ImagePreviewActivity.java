package net.oschina.app.v2.activity.image;

import java.io.IOException;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.adapter.RecyclingPagerAdapter;
import net.oschina.app.v2.base.BaseActivity;
import net.oschina.app.v2.base.Constants;
import net.oschina.app.v2.utils.ImageUtils;
import uk.co.senab.photoview.PhotoView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.tonlin.osc.happy.R;
import com.umeng.analytics.MobclickAgent;

public class ImagePreviewActivity extends BaseActivity implements
		OnPageChangeListener {

	public static final String BUNDLE_KEY_IMAGES = "bundle_key_images";
	private static final String BUNDLE_KEY_INDEX = "bundle_key_index";
	private static final String IMAGE_PREVIEW_SCREEN = "image_preview_screen";
	private HackyViewPager mViewPager;
	private SamplePagerAdapter mAdapter;
	private int mCurrentPostion = 0;
	private String[] mImageUrls;

	public static void showImagePrivew(Context context, int index,
			String[] images) {
		Intent intent = new Intent(context, ImagePreviewActivity.class);
		intent.putExtra(BUNDLE_KEY_IMAGES, images);
		intent.putExtra(BUNDLE_KEY_INDEX, index);
		context.startActivity(intent);
	}

	@Override
	protected boolean hasBackButton() {
		return true;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.v2_activity_image_preview;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.image_menu, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.image_menu_download:
			try {
				if (mAdapter.getCount() > 0) {
					String filePath = Constants.IMAGE_SAVE_PAHT
							+ ImageUtils.getTempFileName() + ".jpg";
					ImageUtils.saveImageToSD(
							this,
							filePath,
							ImageLoader.getInstance().getMemoryCache()
									.get(mAdapter.getItem(mCurrentPostion)),
							100);
					AppContext.showToastShort(getString(
							R.string.tip_save_image_suc, filePath));
				}
			} catch (IOException e) {
				e.printStackTrace();
				AppContext.showToastShort(R.string.tip_save_image_faile);
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		super.init(savedInstanceState);
		mViewPager = (HackyViewPager) findViewById(R.id.view_pager);

		mImageUrls = getIntent().getStringArrayExtra(BUNDLE_KEY_IMAGES);
		int index = getIntent().getIntExtra(BUNDLE_KEY_INDEX, 0);

		mAdapter = new SamplePagerAdapter(mImageUrls);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setCurrentItem(index);
		
		onPageSelected(index);
	}

	static class SamplePagerAdapter extends RecyclingPagerAdapter {

		private String[] images = new String[] {};

		private DisplayImageOptions options;

		SamplePagerAdapter(String[] images) {
			this.images = images;
			options = new DisplayImageOptions.Builder().cacheInMemory(true)
					.postProcessor(new BitmapProcessor() {

						@Override
						public Bitmap process(Bitmap arg0) {
							return arg0;
						}
					}).cacheOnDisk(true).build();
		}

		public String getItem(int position) {
			return images[position];
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			ViewHolder vh = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(container.getContext())
						.inflate(R.layout.v2_image_preview_item, null);
				vh = new ViewHolder(convertView);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			final ProgressBar bar = vh.progress;
			bar.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage(images[position], vh.image,
					options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							// bar.show();
							bar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// bar.hide();
							bar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							bar.setVisibility(View.GONE);
						}
					});
			return convertView;
		}

		static class ViewHolder {
			PhotoView image;
			ProgressBar progress;

			ViewHolder(View view) {
				image = (PhotoView) view.findViewById(R.id.photoview);
				progress = (ProgressBar) view.findViewById(R.id.progress);
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int idx) {
		mCurrentPostion = idx;
		if (mImageUrls != null && mImageUrls.length > 1) {
			setActionBarTitle((mCurrentPostion + 1) + "/" + mImageUrls.length);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(IMAGE_PREVIEW_SCREEN);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(IMAGE_PREVIEW_SCREEN);
		MobclickAgent.onPause(this);
	}
}
