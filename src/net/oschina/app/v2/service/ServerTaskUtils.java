package net.oschina.app.v2.service;

import net.oschina.app.v2.model.Post;
import net.oschina.app.v2.model.Tweet;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ServerTaskUtils {
	
	public static void publicBlogComment(Context context, PublicCommentTask task) {
		Intent intent = new Intent(ServerTaskService.ACTION_PUBLIC_BLOG_COMMENT);
		Bundle bundle = new Bundle();
		bundle.putParcelable(ServerTaskService.BUNDLE_PUBLIC_COMMENT_TASK, task);
		intent.putExtras(bundle);
		context.startService(intent);
	}

	public static void publicComment(Context context, PublicCommentTask task) {
		Intent intent = new Intent(ServerTaskService.ACTION_PUBLIC_COMMENT);
		Bundle bundle = new Bundle();
		bundle.putParcelable(ServerTaskService.BUNDLE_PUBLIC_COMMENT_TASK, task);
		intent.putExtras(bundle);
		context.startService(intent);
	}

	public static void publicPost(Context context, Post post) {
		Intent intent = new Intent(ServerTaskService.ACTION_PUBLIC_POST);
		Bundle bundle = new Bundle();
		bundle.putParcelable(ServerTaskService.BUNDLE_PUBLIC_POST_TASK, post);
		intent.putExtras(bundle);
		context.startService(intent);
	}

	public static void publicTweet(Context context, Tweet tweet) {
		Intent intent = new Intent(ServerTaskService.ACTION_PUBLIC_TWEET);
		Bundle bundle = new Bundle();
		bundle.putParcelable(ServerTaskService.BUNDLE_PUBLIC_TWEET_TASK, tweet);
		intent.putExtras(bundle);
		context.startService(intent);
	}
}
