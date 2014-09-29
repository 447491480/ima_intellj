package net.oschina.app.v2.activity.news.view;

import net.oschina.app.v2.ui.dialog.CommonDialog;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.tonlin.osc.happy.R;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class ShareDialog extends CommonDialog implements
        android.view.View.OnClickListener {

    public interface OnSharePlatformClick {
        void onPlatformClick(SHARE_MEDIA media);
    }

    private OnSharePlatformClick mListener;

    private ShareDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    @SuppressLint("InflateParams")
    private ShareDialog(Context context, int defStyle) {
        super(context, defStyle);
        View shareView = getLayoutInflater().inflate(
                R.layout.v2_dialog_cotent_share, null);
        shareView.findViewById(R.id.ly_share_qq).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_qzone).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_tencent_weibo).setOnClickListener(
                this);
        shareView.findViewById(R.id.ly_share_sina_weibo).setOnClickListener(
                this);
        shareView.findViewById(R.id.ly_share_weichat).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_weichat_circle)
                .setOnClickListener(this);
        setContent(shareView, 0);
    }

    public ShareDialog(Context context) {
        this(context, R.style.dialog_bottom);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    public void setOnPlatformClickListener(OnSharePlatformClick lis) {
        mListener = lis;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        SHARE_MEDIA media = null;
        if (id == R.id.ly_share_qq) {
            media = SHARE_MEDIA.QQ;
        } else if (id == R.id.ly_share_qzone) {
            media = SHARE_MEDIA.QZONE;
        } else if (id == R.id.ly_share_tencent_weibo) {
            media = SHARE_MEDIA.TENCENT;
        } else if (id == R.id.ly_share_sina_weibo) {
            media = SHARE_MEDIA.SINA;
        } else if (id == R.id.ly_share_weichat) {
            media = SHARE_MEDIA.WEIXIN;
        } else if (id == R.id.ly_share_weichat_circle) {
            media = SHARE_MEDIA.WEIXIN_CIRCLE;
        }

        if (mListener != null && media != null) {
            mListener.onPlatformClick(media);
        }
    }
}
