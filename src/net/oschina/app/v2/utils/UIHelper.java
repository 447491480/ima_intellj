package net.oschina.app.v2.utils;

import java.util.regex.Pattern;

import net.oschina.app.v2.AppContext;
import net.oschina.app.v2.activity.comment.fragment.CommentFrament;
import net.oschina.app.v2.activity.comment.fragment.CommentReplyFragment;
import net.oschina.app.v2.activity.common.SimpleBackActivity;
import net.oschina.app.v2.activity.friend.fragment.FriendViewPagerFragment;
import net.oschina.app.v2.activity.image.ImagePreviewActivity;
import net.oschina.app.v2.activity.message.fragment.MessageDetailFragment;
import net.oschina.app.v2.activity.message.fragment.MessageForwardFragment;
import net.oschina.app.v2.activity.message.fragment.MessagePublicFragment;
import net.oschina.app.v2.activity.news.DetailActivity;
import net.oschina.app.v2.activity.question.fragment.QuestionTagFragment;
import net.oschina.app.v2.activity.user.LoginActivity;
import net.oschina.app.v2.base.Constants;
import net.oschina.app.v2.model.Active;
import net.oschina.app.v2.model.Comment;
import net.oschina.app.v2.model.News;
import net.oschina.app.v2.model.Notice;
import net.oschina.app.v2.model.Result;
import net.oschina.app.v2.model.SimpleBackPage;
import net.oschina.app.v2.model.Tweet;
import net.oschina.app.v2.model.URLs;
import net.oschina.app.v2.service.NoticeService;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tonlin.osc.happy.R;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper {
	private final static String TAG = "UIHelper";

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;

	public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
	public final static int LISTVIEW_DATATYPE_BLOG = 0x02;
	public final static int LISTVIEW_DATATYPE_POST = 0x03;
	public final static int LISTVIEW_DATATYPE_TWEET = 0x04;
	public final static int LISTVIEW_DATATYPE_ACTIVE = 0x05;
	public final static int LISTVIEW_DATATYPE_MESSAGE = 0x06;
	public final static int LISTVIEW_DATATYPE_COMMENT = 0x07;

	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;

	/** 表情图片匹配 */
	private static Pattern facePattern = Pattern
			.compile("\\[{1}([0-9]\\d*)\\]{1}");

	/** 全局web样式 */
	// 链接样式文件，代码块高亮的处理
	public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
			+ "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
			+ "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
			+ "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
			+ "<script type=\"text/javascript\">function showImagePreview(var url){window.location.url= url;}</script>";
	public final static String WEB_STYLE = linkCss
			+ "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;overflow: auto;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

	private static final String IAM_API_SHOWIMAGE = "ima-api:action=showImage&data=";

	/**
	 * 显示新闻详情
	 * 
	 * @param context
	 * @param newsId
	 */
	public static void showNewsDetail(Context context, int newsId) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("news_id", newsId);
		intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
				DetailActivity.DISPLAY_NEWS);
		context.startActivity(intent);
	}

	/**
	 * 显示帖子详情
	 * 
	 * @param context
	 * @param postId
	 */
	public static void showQuestionDetail(Context context, int postId) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("post_id", postId);
		intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
				DetailActivity.DISPLAY_QUESTION);
		context.startActivity(intent);
	}

	/**
	 * 显示相关Tag帖子列表
	 * 
	 * @param context
	 * @param tag
	 */
	public static void showQuestionListByTag(Context context, String tag) {
		// Intent intent = new Intent(context, QuestionTag.class);
		// intent.putExtra("post_tag", tag);
		// context.startActivity(intent);
		Bundle args = new Bundle();
		args.putString(QuestionTagFragment.BUNDLE_KEY_TAG, tag);
		showSimpleBack(context, SimpleBackPage.QUESTION_TAG, args);
	}

	/**
	 * 显示我要提问页面
	 * 
	 * @param context
	 */
	public static void showQuestionPub(Context context) {
		// Intent intent = new Intent(context, QuestionPub.class);
		// context.startActivity(intent);
		showSimpleBack(context, SimpleBackPage.QUESTION_PUBLIC);
	}

	/**
	 * 显示动弹详情及评论
	 * 
	 * @param context
	 * @param tweetId
	 */
	public static void showTweetDetail(Context context, int tweetId) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("tweet_id", tweetId);
		intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
				DetailActivity.DISPLAY_TWEET);
		context.startActivity(intent);
	}

	/**
	 * 显示动弹一下页面
	 * 
	 * @param context
	 */
	public static void showTweetPub(Activity context) {
		// Intent intent = new Intent(context, TweetPub.class);
		// context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
		showSimpleBackForResult(context, REQUEST_CODE_FOR_RESULT,
				SimpleBackPage.TWEET_PUBLIC);
	}

	/**
	 * 显示博客详情
	 * 
	 * @param context
	 * @param blogId
	 */
	public static void showBlogDetail(Context context, int blogId) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("blog_id", blogId);
		intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
				DetailActivity.DISPLAY_BLOG);
		context.startActivity(intent);
	}

	/**
	 * 显示软件详情
	 * 
	 * @param context
	 * @param ident
	 */
	public static void showSoftwareDetail(Context context, String ident) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("ident", ident);
		intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
				DetailActivity.DISPLAY_SOFTWARE);
		context.startActivity(intent);
	}

	/**
	 * 新闻超链接点击跳转
	 * 
	 * @param context
	 * @param newsId
	 * @param newsType
	 * @param objId
	 */
	public static void showNewsRedirect(Context context, News news) {
		String url = news.getUrl();
		// url为空-旧方法
		if (StringUtils.isEmpty(url)) {
			int newsId = news.getId();
			int newsType = news.getNewType().type;
			String objId = news.getNewType().attachment;
			switch (newsType) {
			case News.NEWSTYPE_NEWS:
				showNewsDetail(context, newsId);
				break;
			case News.NEWSTYPE_SOFTWARE:
				showSoftwareDetail(context, objId);
				break;
			case News.NEWSTYPE_POST:
				showQuestionDetail(context, StringUtils.toInt(objId));
				break;
			case News.NEWSTYPE_BLOG:
				showBlogDetail(context, StringUtils.toInt(objId));
				break;
			}
		} else {
			showUrlRedirect(context, url);
		}
	}

	/**
	 * 动态点击跳转到相关新闻、帖子等
	 * 
	 * @param context
	 * @param id
	 * @param catalog
	 *            0其他 1新闻 2帖子 3动弹 4博客
	 */
	public static void showActiveRedirect(Context context, Active active) {
		String url = active.getUrl();
		// url为空-旧方法
		if (StringUtils.isEmpty(url)) {
			int id = active.getObjectId();
			int catalog = active.getActiveType();
			switch (catalog) {
			case Active.CATALOG_OTHER:
				// 其他-无跳转
				break;
			case Active.CATALOG_NEWS:
				showNewsDetail(context, id);
				break;
			case Active.CATALOG_POST:
				showQuestionDetail(context, id);
				break;
			case Active.CATALOG_TWEET:
				showTweetDetail(context, id);
				break;
			case Active.CATALOG_BLOG:
				showBlogDetail(context, id);
				break;
			}
		} else {
			showUrlRedirect(context, url);
		}
	}

	/**
	 * 显示评论回复页面
	 * 
	 * @param context
	 * @param id
	 * @param catalog
	 * @param replyid
	 * @param authorid
	 */
	public static void showCommentReply(Activity context, int id, int catalog,
			int replyid, int authorid, String author, String content) {
		// Intent intent = new Intent(context, CommentPub.class);
		// intent.putExtra("id", id);
		// intent.putExtra("catalog", catalog);
		// intent.putExtra("reply_id", replyid);
		// intent.putExtra("author_id", authorid);
		// intent.putExtra("author", author);
		// intent.putExtra("content", content);
		// if (catalog == CommentList.CATALOG_POST)
		// context.startActivityForResult(intent, REQUEST_CODE_FOR_REPLY);
		// else
		// context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
		Bundle args = new Bundle();
		showSimpleBack(context, SimpleBackPage.REPLY_COMMENT, args);
	}

	public static void showReplyComment(Context context, boolean isBlog,
			int mId, int mCatalog, Comment comment) {
		Bundle args = new Bundle();
		args.putBoolean(CommentReplyFragment.BUNDLE_KEY_IS_BLOG, isBlog);
		args.putInt(CommentReplyFragment.BUNDLE_KEY_ID, mId);
		args.putInt(CommentReplyFragment.BUNDLE_KEY_CATALOG, mCatalog);
		args.putParcelable(CommentReplyFragment.BUNDLE_KEY_COMMENT, comment);
		showSimpleBack(context, SimpleBackPage.REPLY_COMMENT, args);
	}

	public static void showReplyCommentForResult(Activity context,
			int requestCode, boolean isBlog, int mId, int mCatalog,
			Comment comment) {
		Bundle args = new Bundle();
		args.putBoolean(CommentReplyFragment.BUNDLE_KEY_IS_BLOG, isBlog);
		args.putInt(CommentReplyFragment.BUNDLE_KEY_ID, mId);
		args.putInt(CommentReplyFragment.BUNDLE_KEY_CATALOG, mCatalog);
		args.putParcelable(CommentReplyFragment.BUNDLE_KEY_COMMENT, comment);
		showSimpleBackForResult(context, requestCode,
				SimpleBackPage.REPLY_COMMENT, args);
	}

	public static void showReplyCommentForResult(Fragment fragment,
			int requestCode, boolean isBlog, int mId, int mCatalog,
			Comment comment) {
		Bundle args = new Bundle();
		args.putBoolean(CommentReplyFragment.BUNDLE_KEY_IS_BLOG, isBlog);
		args.putInt(CommentReplyFragment.BUNDLE_KEY_ID, mId);
		args.putInt(CommentReplyFragment.BUNDLE_KEY_CATALOG, mCatalog);
		args.putParcelable(CommentReplyFragment.BUNDLE_KEY_COMMENT, comment);
		showSimpleBackForResult(fragment, requestCode,
				SimpleBackPage.REPLY_COMMENT, args);
	}

	/**
	 * 显示留言对话页面
	 * 
	 * @param context
	 * @param catalog
	 * @param friendid
	 */
	public static void showMessageDetail(Context context, int friendid,
			String friendname) {
		// Intent intent = new Intent(context, MessageDetail.class);
		// intent.putExtra("friend_name", friendname);
		// intent.putExtra("friend_id", friendid);
		// context.startActivity(intent);
		Bundle args = new Bundle();
		args.putInt(MessageDetailFragment.BUNDLE_KEY_FID, friendid);
		args.putString(MessageDetailFragment.BUNDLE_KEY_FNAME, friendname);
		showSimpleBack(context, SimpleBackPage.MESSAGE_DETAIL, args);
	}

	/**
	 * 显示留言回复界面
	 * 
	 * @param context
	 * @param friendId
	 *            对方id
	 * @param friendName
	 *            对方名称
	 */
	public static void showMessagePub(Activity context, int friendId,
			String friendName) {
		// Intent intent = new Intent();
		// intent.putExtra("user_id",
		// ((AppContext) context.getApplication()).getLoginUid());
		// intent.putExtra("friend_id", friendId);
		// intent.putExtra("friend_name", friendName);
		// intent.setClass(context, MessagePub.class);
		// context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
		Bundle args = new Bundle();
		args.putInt(MessagePublicFragment.BUNDLE_KEY_UID,
				((AppContext) context.getApplication()).getLoginUid());
		args.putInt(MessagePublicFragment.BUNDLE_KEY_FID, friendId);
		args.putString(MessagePublicFragment.BUNDLE_KEY_FNAME, friendName);
		showSimpleBack(context, SimpleBackPage.MESSAGE_PUBLIC, args);
	}

	/**
	 * 显示转发留言界面
	 * 
	 * @param context
	 * @param friendName
	 *            对方名称
	 * @param messageContent
	 *            留言内容
	 */
	public static void showMessageForward(Activity context, String friendName,
			String messageContent) {
		// Intent intent = new Intent();
		// intent.putExtra("user_id",
		// ((AppContext) context.getApplication()).getLoginUid());
		// intent.putExtra("friend_name", friendName);
		// intent.putExtra("message_content", messageContent);
		// intent.setClass(context, MessageForward.class);
		// context.startActivity(intent);
		Bundle args = new Bundle();
		args.putInt(MessageForwardFragment.BUNDLE_KEY_UID,
				((AppContext) context.getApplication()).getLoginUid());
		args.putString(MessageForwardFragment.BUNDLE_KEY_FNAME, friendName);
		args.putString(MessageForwardFragment.BUNDLE_KEY_CONTENT,
				messageContent);
		showSimpleBack(context, SimpleBackPage.MESSAGE_FORWARD, args);
	}

	/**
	 * 调用系统安装了的应用分享
	 * 
	 * @param context
	 * @param title
	 * @param url
	 */
	public static void showShareMore(Activity context, final String title,
			final String url) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
		intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
		context.startActivity(Intent.createChooser(intent, "选择分享"));
	}

	/**
	 * 显示系统设置界面
	 * 
	 * @param context
	 */
	public static void showSetting(Context context) {
		// Intent intent = new Intent(context, Setting.class);
		// context.startActivity(intent);
		showSimpleBack(context, SimpleBackPage.SETTINGS);
	}

	public static void showSimpleBackForResult(Fragment fragment,
			int requestCode, SimpleBackPage page, Bundle args) {
		Intent intent = new Intent(fragment.getActivity(),
				SimpleBackActivity.class);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void showSimpleBackForResult(Activity context,
			int requestCode, SimpleBackPage page, Bundle args) {
		Intent intent = new Intent(context, SimpleBackActivity.class);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
		context.startActivityForResult(intent, requestCode);
	}

	public static void showSimpleBackForResult(Activity context,
			int requestCode, SimpleBackPage page) {
		Intent intent = new Intent(context, SimpleBackActivity.class);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
		context.startActivityForResult(intent, requestCode);
	}

	public static void showSimpleBack(Context context, SimpleBackPage page) {
		Intent intent = new Intent(context, SimpleBackActivity.class);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
		context.startActivity(intent);
	}

	public static void showSimpleBack(Context context, SimpleBackPage page,
			Bundle args) {
		Intent intent = new Intent(context, SimpleBackActivity.class);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
		intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
		context.startActivity(intent);
	}

	/**
	 * 显示搜索界面
	 * 
	 * @param context
	 */
	public static void showSearch(Context context) {
		// Intent intent = new Intent(context, Search.class);
		// context.startActivity(intent);
		showSimpleBack(context, SimpleBackPage.SEARCH);
	}

	/**
	 * 显示软件界面
	 * 
	 * @param context
	 */
	public static void showSoftware(Context context) {
		// Intent intent = new Intent(context, SoftwareLib.class);
		// context.startActivity(intent);
		showSimpleBack(context, SimpleBackPage.SOFTEARE);
	}

	/**
	 * 显示我的资料
	 * 
	 * @param context
	 */
	public static void showUserInfo(Activity context) {
		// AppContext ac = (AppContext) context.getApplicationContext();
		// if (!ac.isLogin()) {
		// showLoginDialog(context);
		// } else {
		// Intent intent = new Intent(context, UserInfo.class);
		// context.startActivity(intent);
		// }
		showSimpleBack(context, SimpleBackPage.PROFILE);
	}

	/**
	 * 显示用户动态
	 * 
	 * @param context
	 * @param uid
	 * @param hisuid
	 * @param hisname
	 */
	public static void showUserCenter(Context context, int hisuid,
			String hisname) {
		// Intent intent = new Intent(context, UserCenter.class);
		// intent.putExtra("his_id", hisuid);
		// intent.putExtra("his_name", hisname);
		// context.startActivity(intent);
		Bundle args = new Bundle();
		args.putInt("his_id", hisuid);
		args.putString("his_name", hisname);
		showSimpleBack(context, SimpleBackPage.USER_CENTER, args);
	}

	/**
	 * 显示用户收藏夹
	 * 
	 * @param context
	 */
	public static void showUserFavorite(Context context) {
		// Intent intent = new Intent(context, UserFavorite.class);
		// context.startActivity(intent);
		showSimpleBack(context, SimpleBackPage.FAVORITES);
	}

	/**
	 * url跳转
	 * 
	 * @param context
	 * @param url
	 */
	public static void showUrlRedirect(Context context, String url) {
		if (url.startsWith(IAM_API_SHOWIMAGE)) {
			String realUrl = url.substring(IAM_API_SHOWIMAGE.length());
			try {
				JSONObject json = new JSONObject(realUrl);
				int idx = json.optInt("index");
				String[] urls = json.getString("urls").split(",");
				showImagePreview(context, idx, urls);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		URLs urls = URLs.parseURL(url);
		if (urls != null) {
			showLinkRedirect(context, urls.getObjType(), urls.getObjId(),
					urls.getObjKey());
		} else {
			openBrowser(context, url);
		}
	}

	public static void showLinkRedirect(Context context, int objType,
			int objId, String objKey) {
		switch (objType) {
		case URLs.URL_OBJ_TYPE_NEWS:
			showNewsDetail(context, objId);
			break;
		case URLs.URL_OBJ_TYPE_QUESTION:
			showQuestionDetail(context, objId);
			break;
		case URLs.URL_OBJ_TYPE_QUESTION_TAG:
			showQuestionListByTag(context, objKey);
			break;
		case URLs.URL_OBJ_TYPE_SOFTWARE:
			showSoftwareDetail(context, objKey);
			break;
		case URLs.URL_OBJ_TYPE_ZONE:
			showUserCenter(context, objId, objKey);
			break;
		case URLs.URL_OBJ_TYPE_TWEET:
			showTweetDetail(context, objId);
			break;
		case URLs.URL_OBJ_TYPE_BLOG:
			showBlogDetail(context, objId);
			break;
		case URLs.URL_OBJ_TYPE_OTHER:
			openBrowser(context, objKey);
			break;
		}
	}

	/**
	 * 打开浏览器
	 * 
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			AppContext.showToastShort("无法浏览此网页");
		}
	}

	/**
	 * 获取webviewClient对象
	 * 
	 * @return
	 */
	public static WebViewClient getWebViewClient() {

		return new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				showUrlRedirect(view.getContext(), url);
				return true;
			}
		};
	}

	/**
	 * 获取TextWatcher对象
	 * 
	 * @param context
	 * @param tmlKey
	 * @return
	 */
	public static TextWatcher getTextWatcher(final Activity context,
			final String temlKey) {
		return new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 保存当前EditText正在编辑的内容
				((AppContext) context.getApplication()).setProperty(temlKey,
						s.toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		};
	}

	/**
	 * 发送通知广播
	 * 
	 * @param context
	 * @param notice
	 */
	public static void sendBroadCast(Context context, Notice notice) {
		// if (!((AppContext) context.getApplicationContext()).isLogin()
		// || notice == null)
		// return;
		TLog.log("发送通知广播");
		Intent intent = new Intent(Constants.INTENT_ACTION_NOTICE);
		intent.putExtra("atmeCount", notice.getAtmeCount());
		intent.putExtra("msgCount", notice.getMsgCount());
		intent.putExtra("reviewCount", notice.getReviewCount());
		intent.putExtra("newFansCount", notice.getNewFansCount());
		context.sendBroadcast(intent);
	}

	/**
	 * 发送广播-发布动弹
	 * 
	 * @param context
	 * @param notice
	 */
	public static void sendBroadCastTweet(Context context, int what,
			Result res, Tweet tweet) {
		if (res == null && tweet == null)
			return;
		Intent intent = new Intent("net.oschina.app.action.APP_TWEETPUB");
		intent.putExtra("MSG_WHAT", what);
		if (what == 1)
			intent.putExtra("RESULT", res);
		// else
		// intent.putExtra("TWEET", tweet);
		context.sendBroadcast(intent);
	}

	/**
	 * 组合动态的动作文本
	 * 
	 * @param objecttype
	 * @param objectcatalog
	 * @param objecttitle
	 * @return
	 */
	@SuppressLint("NewApi")
	public static SpannableString parseActiveAction(String author,
			int objecttype, int objectcatalog, String objecttitle) {
		String title = "";
		int start = 0;
		int end = 0;
		if (objecttype == 32 && objectcatalog == 0) {
			title = "加入了开源中国";
		} else if (objecttype == 1 && objectcatalog == 0) {
			title = "添加了开源项目 " + objecttitle;
		} else if (objecttype == 2 && objectcatalog == 1) {
			title = "在讨论区提问：" + objecttitle;
		} else if (objecttype == 2 && objectcatalog == 2) {
			title = "发表了新话题：" + objecttitle;
		} else if (objecttype == 3 && objectcatalog == 0) {
			title = "发表了博客 " + objecttitle;
		} else if (objecttype == 4 && objectcatalog == 0) {
			title = "发表一篇新闻 " + objecttitle;
		} else if (objecttype == 5 && objectcatalog == 0) {
			title = "分享了一段代码 " + objecttitle;
		} else if (objecttype == 6 && objectcatalog == 0) {
			title = "发布了一个职位：" + objecttitle;
		} else if (objecttype == 16 && objectcatalog == 0) {
			title = "在新闻 " + objecttitle + " 发表评论";
		} else if (objecttype == 17 && objectcatalog == 1) {
			title = "回答了问题：" + objecttitle;
		} else if (objecttype == 17 && objectcatalog == 2) {
			title = "回复了话题：" + objecttitle;
		} else if (objecttype == 17 && objectcatalog == 3) {
			title = "在 " + objecttitle + " 对回帖发表评论";
		} else if (objecttype == 18 && objectcatalog == 0) {
			title = "在博客 " + objecttitle + " 发表评论";
		} else if (objecttype == 19 && objectcatalog == 0) {
			title = "在代码 " + objecttitle + " 发表评论";
		} else if (objecttype == 20 && objectcatalog == 0) {
			title = "在职位 " + objecttitle + " 发表评论";
		} else if (objecttype == 101 && objectcatalog == 0) {
			title = "回复了动态：" + objecttitle;
		} else if (objecttype == 100) {
			title = "更新了动态";
		}
		title = author + " " + title;
		SpannableString sp = new SpannableString(title);
		// 设置用户名字体大小、加粗、高亮
		sp.setSpan(new AbsoluteSizeSpan(14, true), 0, author.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				author.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 0,
				author.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置标题字体大小、高亮
		if (!StringUtils.isEmpty(objecttitle)) {
			start = title.indexOf(objecttitle);
			if (objecttitle.length() > 0 && start > 0) {
				end = start + objecttitle.length();
				sp.setSpan(new AbsoluteSizeSpan(14, true), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				sp.setSpan(
						new ForegroundColorSpan(Color.parseColor("#0e5986")),
						start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return sp;
	}

	public static SpannableString parseActiveAction2(int objecttype,
			int objectcatalog, String objecttitle) {
		String title = "";
		int start = 0;
		int end = 0;
		if (objecttype == 32 && objectcatalog == 0) {
			title = "加入了开源中国";
		} else if (objecttype == 1 && objectcatalog == 0) {
			title = "添加了开源项目 " + objecttitle;
		} else if (objecttype == 2 && objectcatalog == 1) {
			title = "在讨论区提问：" + objecttitle;
		} else if (objecttype == 2 && objectcatalog == 2) {
			title = "发表了新话题：" + objecttitle;
		} else if (objecttype == 3 && objectcatalog == 0) {
			title = "发表了博客 " + objecttitle;
		} else if (objecttype == 4 && objectcatalog == 0) {
			title = "发表一篇新闻 " + objecttitle;
		} else if (objecttype == 5 && objectcatalog == 0) {
			title = "分享了一段代码 " + objecttitle;
		} else if (objecttype == 6 && objectcatalog == 0) {
			title = "发布了一个职位：" + objecttitle;
		} else if (objecttype == 16 && objectcatalog == 0) {
			title = "在新闻 " + objecttitle + " 发表评论";
		} else if (objecttype == 17 && objectcatalog == 1) {
			title = "回答了问题：" + objecttitle;
		} else if (objecttype == 17 && objectcatalog == 2) {
			title = "回复了话题：" + objecttitle;
		} else if (objecttype == 17 && objectcatalog == 3) {
			title = "在 " + objecttitle + " 对回帖发表评论";
		} else if (objecttype == 18 && objectcatalog == 0) {
			title = "在博客 " + objecttitle + " 发表评论";
		} else if (objecttype == 19 && objectcatalog == 0) {
			title = "在代码 " + objecttitle + " 发表评论";
		} else if (objecttype == 20 && objectcatalog == 0) {
			title = "在职位 " + objecttitle + " 发表评论";
		} else if (objecttype == 101 && objectcatalog == 0) {
			title = "回复了动态：" + objecttitle;
		} else if (objecttype == 100) {
			title = "更新了动态";
		}
		SpannableString sp = new SpannableString(title);
		// 设置用户名字体大小、加粗、高亮
		// sp.setSpan(new AbsoluteSizeSpan(14, true), 0, author.length(),
		// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
		// author.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 0,
		// author.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置标题字体大小、高亮
		if (!StringUtils.isEmpty(objecttitle)) {
			start = title.indexOf(objecttitle);
			if (objecttitle.length() > 0 && start > 0) {
				end = start + objecttitle.length();
				sp.setSpan(new AbsoluteSizeSpan(14, true), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				sp.setSpan(
						new ForegroundColorSpan(Color.parseColor("#0e5986")),
						start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return sp;
	}

	/**
	 * 组合动态的回复文本
	 * 
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableStringBuilder parseActiveReply(String name,
			String body) {
		Spanned span = Html.fromHtml(body.trim());
		SpannableStringBuilder sp = new SpannableStringBuilder(name + "：");
		sp.append(span);
		// 设置用户名字体加粗、高亮
		// sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
		// name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#576B95")), 0,
				name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return sp;
	}

	/**
	 * 组合回复引用文本
	 * 
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableString parseQuoteSpan(String name, String body) {
		SpannableString sp = new SpannableString("回复：" + name + "\n" + body);
		// 设置用户名字体加粗、高亮
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3,
				3 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 3,
				3 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sp;
	}

	/**
	 * 点击返回监听事件
	 * 
	 * @param activity
	 * @return
	 */
	public static View.OnClickListener finish(final Activity activity) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		};
	}

	/**
	 * 显示关于我们
	 * 
	 * @param context
	 */
	public static void showAbout(Context context) {
		// Intent intent = new Intent(context, About.class);
		// context.startActivity(intent);
		showSimpleBack(context, SimpleBackPage.ABOUT);
	}

	/**
	 * 用户登录或注销
	 * 
	 * @param activity
	 */
	public static void loginOrLogout(Activity activity) {
		AppContext ac = (AppContext) activity.getApplication();
		if (ac.isLogin()) {
			ac.Logout();
			// ToastMessage(activity, "已退出登录");
			AppContext.showToastShort(R.string.tip_logout_success);
		} else {
			// showLoginDialog(activity);
			UIHelper.showLogin(activity);
		}
	}

	public static void changeSettingIsLoadImage(Activity activity, boolean b) {
		AppContext ac = (AppContext) activity.getApplication();
		ac.setConfigLoadimage(b);
	}

	/**
	 * 清除app缓存
	 * 
	 * @param activity
	 */
	public static void clearAppCache(Activity activity) {
		final AppContext ac = (AppContext) activity.getApplication();
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					AppContext.showToastShort("缓存清除成功");
				} else {
					AppContext.showToastShort("缓存清除失败");
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					ac.clearAppCache();
					msg.what = 1;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 添加网页的点击图片展示支持
	 */
	@SuppressLint("JavascriptInterface")
	public static void addWebImageShow(final Context cxt, WebView wv) {
		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new OnWebViewImageListener() {

			@Override
			public void onImageClick(String bigImageUrl) {
				if (bigImageUrl != null) {
					// UIHelper.showImageZoomDialog(cxt, bigImageUrl);
					UIHelper.showImagePreview(cxt, new String[] { bigImageUrl });
				}
			}
		}, "mWebViewImageListener");
	}

	public static String setHtmlCotentSupportImagePreview(String body) {
		// 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
		if (AppContext.shouldLoadImage() || TDevice.isWifiOpen()) {
			// 过滤掉 img标签的width,height属性
			body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
			body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
			// 添加点击图片放大支持
			body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
					"$1$2\" onClick=\"showImagePreview('$2')\"");
			// mWebViewImageListener.onImageClick
		} else {
			// 过滤掉 img标签
			body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
		}
		return body;
	}

	public static void showLogin(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}

	public static void exitApp(Context context) {
		Intent intent = new Intent(
				net.oschina.app.v2.base.BaseActivity.INTENT_ACTION_EXIT_APP);
		context.sendBroadcast(intent);
	}

	public static void showFriends(Context context, int tabIdx) {
		Bundle args = new Bundle();
		args.putInt(FriendViewPagerFragment.BUNDLE_KEY_TABIDX, tabIdx);
		showSimpleBack(context, SimpleBackPage.FRIENDS, args);
	}

	public static void showBlogComment(Context context, int id, int ownerId) {
		Bundle args = new Bundle();
		args.putInt(CommentFrament.BUNDLE_KEY_ID, id);
		args.putInt(CommentFrament.BUNDLE_KEY_OWNER_ID, ownerId);
		args.putBoolean(CommentFrament.BUNDLE_KEY_BLOG, true);
		showSimpleBack(context, SimpleBackPage.COMMENT, args);
	}

	public static void showComment(Context context, int id, int catalog) {
		Bundle args = new Bundle();
		args.putInt(CommentFrament.BUNDLE_KEY_ID, id);
		args.putInt(CommentFrament.BUNDLE_KEY_CATALOG, catalog);
		showSimpleBack(context, SimpleBackPage.COMMENT, args);
	}

	public static void showLisence(Context context) {
		showSimpleBack(context, SimpleBackPage.LISENCE);
	}

	/**
	 * 发送广播告知评论发生变化
	 * 
	 * @param context
	 * @param isBlog
	 * @param id
	 * @param catalog
	 * @param operation
	 * @param replyComment
	 */
	public static void sendBroadCastCommentChanged(Context context,
			boolean isBlog, int id, int catalog, int operation,
			Comment replyComment) {
		Intent intent = new Intent(Constants.INTENT_ACTION_COMMENT_CHANGED);
		Bundle args = new Bundle();
		args.putInt(Comment.BUNDLE_KEY_ID, id);
		args.putInt(Comment.BUNDLE_KEY_CATALOG, catalog);
		args.putBoolean(Comment.BUNDLE_KEY_BLOG, isBlog);
		args.putInt(Comment.BUNDLE_KEY_OPERATION, operation);
		args.putParcelable(Comment.BUNDLE_KEY_COMMENT, replyComment);
		intent.putExtras(args);
		context.sendBroadcast(intent);
	}

	public static void showImagePreview(Context context, String[] imageUrls) {
		ImagePreviewActivity.showImagePrivew(context, 0, imageUrls);
	}

	public static void showImagePreview(Context context, int index,
			String[] imageUrls) {
		ImagePreviewActivity.showImagePrivew(context, index, imageUrls);
	}

	public static void sendBroadcastForNotice(Context context) {
		Intent intent = new Intent(NoticeService.INTENT_ACTION_BROADCAST);
		context.sendBroadcast(intent);
	}

	public static void showNotificaitonSettings(Context context) {
		showSimpleBack(context, SimpleBackPage.NOTIFICATION_SETTINGS);
	}

	public static void showDailyEnglish(Context context) {
		showSimpleBack(context, SimpleBackPage.DAILY_ENGLISH);
	}
}
