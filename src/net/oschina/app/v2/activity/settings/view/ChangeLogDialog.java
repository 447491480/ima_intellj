package net.oschina.app.v2.activity.settings.view;

import java.io.IOException;

import net.oschina.app.v2.ui.dialog.CommonDialog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.tonlin.osc.happy.R;

public class ChangeLogDialog extends CommonDialog {

	private static final String TAG = ChangeLogDialog.class.getSimpleName();

	public ChangeLogDialog(Context context) {
		this(context,R.style.dialog_common);
		initLog(context);
	}

	private ChangeLogDialog(Context context, int defStyle) {
		super(context, defStyle);
		// TODO Auto-generated constructor stub
	}

	private ChangeLogDialog(Context context, boolean flag,
			OnCancelListener listener) {
		super(context, flag, listener);
		// TODO Auto-generated constructor stub
	}

	private void initLog(Context context) {
		// Get resources
		String _PackageName = context.getPackageName();
		Resources _Resource;
		try {
			_Resource = context.getPackageManager().getResourcesForApplication(
					_PackageName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return;
		}
		setTitle(R.string.changelog);
		
		String _HTML = GetHTMLChangelog(R.xml.changelog, _Resource);
		WebView webView = new WebView(context);
		WebSettings settings = webView.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		webView.loadData(_HTML, "text/html; charset=UTF-8", "UTF-8");
		setContent(webView, 0);
		
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		setNegativeButton(R.string.cancle, null);
	}

	// Parse a the release tag and return html code
	private String ParseReleaseTag(XmlResourceParser aXml)
			throws XmlPullParserException, IOException {
		String _Result = "<h1>Release: "
				+ aXml.getAttributeValue(null, "version") + "</h1><ul>";
		int eventType = aXml.getEventType();
		while ((eventType != XmlPullParser.END_TAG)
				|| (aXml.getName().equals("change"))) {
			if ((eventType == XmlPullParser.START_TAG)
					&& (aXml.getName().equals("change"))) {
				eventType = aXml.next();
				_Result = _Result + "<li>" + aXml.getText() + "</li>";
			}
			eventType = aXml.next();
		}
		_Result = _Result + "</ul>";
		return _Result;
	}

	// CSS style for the html
	private String GetStyle() {
		return "<style type=\"text/css\">"
				+ "h1 { margin-left: 0px; font-size: 12pt; }"
				+ "li { margin-left: 0px; font-size: 9pt;}"
				+ "ul { padding-left: 30px;}" + "</style>";
	}

	// Get the changelog in html code, this will be shown in the dialog's
	// webview
	private String GetHTMLChangelog(int aResourceId, Resources aResource) {
		String _Result = "<html><head>" + GetStyle() + "</head><body>";
		XmlResourceParser _xml = aResource.getXml(aResourceId);
		try {
			int eventType = _xml.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if ((eventType == XmlPullParser.START_TAG)
						&& (_xml.getName().equals("release"))) {
					_Result = _Result + ParseReleaseTag(_xml);

				}
				eventType = _xml.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);

		} finally {
			_xml.close();
		}
		_Result = _Result + "</body></html>";
		return _Result;
	}
}
