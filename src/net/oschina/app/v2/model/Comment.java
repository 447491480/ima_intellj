package net.oschina.app.v2.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.v2.AppException;
import net.oschina.app.v2.utils.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Xml;

/**
 * 评论实体类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Comment extends Entity implements Parcelable {

	public static final String BUNDLE_KEY_COMMENT = "bundle_key_comment";
	public static final String BUNDLE_KEY_ID = "bundle_key_id";
	public static final String BUNDLE_KEY_CATALOG = "bundle_key_catalog";
	public static final String BUNDLE_KEY_BLOG = "bundle_key_blog";
	public static final String BUNDLE_KEY_OPERATION = "bundle_key_operation";
	
	public static final int OPT_ADD = 1;
	public static final int OPT_REMOVE = 2;
	
	private static final long serialVersionUID = -3376380441354446400L;
	public final static int CLIENT_MOBILE = 2;
	public final static int CLIENT_ANDROID = 3;
	public final static int CLIENT_IPHONE = 4;
	public final static int CLIENT_WINDOWS_PHONE = 5;

	private String face;
	private String content;
	private String author;
	private int authorId;
	private String pubDate;
	private int appClient;
	private List<Reply> replies = new ArrayList<Reply>();
	private List<Refer> refers = new ArrayList<Refer>();

	public static class Reply implements Serializable, Parcelable {
		private static final long serialVersionUID = 1L;
		public String rauthor;
		public String rpubDate;
		public String rcontent;

		public Reply() {
		}

		public Reply(Parcel source) {
			rauthor = source.readString();
			rpubDate = source.readString();
			rcontent = source.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(rauthor);
			dest.writeString(rpubDate);
			dest.writeString(rcontent);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		public static final Parcelable.Creator<Reply> CREATOR = new Creator<Reply>() {

			@Override
			public Reply[] newArray(int size) {
				return new Reply[size];
			}

			@Override
			public Reply createFromParcel(Parcel source) {
				return new Reply(source);
			}
		};
	}

	public static class Refer implements Serializable, Parcelable {
		private static final long serialVersionUID = 1L;
		public String refertitle;
		public String referbody;

		public Refer() {
		}

		public Refer(Parcel source) {
			referbody = source.readString();
			refertitle = source.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(referbody);
			dest.writeString(refertitle);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		public static final Parcelable.Creator<Refer> CREATOR = new Creator<Comment.Refer>() {

			@Override
			public Refer[] newArray(int size) {
				return new Refer[size];
			}

			@Override
			public Refer createFromParcel(Parcel source) {
				return new Refer(source);
			}
		};
	}

	public Comment() {
	}

	@SuppressWarnings("unchecked")
	public Comment(Parcel source) {
		id = source.readInt();
		face = source.readString();
		author = source.readString();
		authorId = source.readInt();
		pubDate = source.readString();
		appClient = source.readInt();
		content = source.readString();

		replies = source.readArrayList(Reply.class.getClassLoader());
		refers = source.readArrayList(Refer.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(face);
		dest.writeString(author);
		dest.writeInt(authorId);
		dest.writeString(pubDate);
		dest.writeInt(appClient);
		dest.writeString(content);

		dest.writeList(replies);
		dest.writeList(refers);
	}

	public int getAppClient() {
		return appClient;
	}

	public void setAppClient(int appClient) {
		this.appClient = appClient;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public List<Reply> getReplies() {
		return replies;
	}

	public void setReplies(List<Reply> replies) {
		this.replies = replies;
	}

	public List<Refer> getRefers() {
		return refers;
	}

	public void setRefers(List<Refer> refers) {
		this.refers = refers;
	}

	public static Comment parse(InputStream inputStream) throws IOException,
			AppException {
		Comment comm = null;
		Reply reply = null;
		Refer refer = null;
		// 获得XmlPullParser解析器
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(inputStream, UTF8);
			// 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
			int evtType = xmlParser.getEventType();
			// 一直循环，直到文档结束
			while (evtType != XmlPullParser.END_DOCUMENT) {
				String tag = xmlParser.getName();
				switch (evtType) {
				case XmlPullParser.START_TAG:
					if (tag.equalsIgnoreCase("comment")) {
						comm = new Comment();
					} else if (comm != null) {
						if (tag.equalsIgnoreCase("id")) {
							comm.id = StringUtils
									.toInt(xmlParser.nextText(), 0);
						} else if (tag.equalsIgnoreCase("portrait")) {
							comm.setFace(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("author")) {
							comm.setAuthor(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("authorid")) {
							comm.setAuthorId(StringUtils.toInt(
									xmlParser.nextText(), 0));
						} else if (tag.equalsIgnoreCase("content")) {
							comm.setContent(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("pubDate")) {
							comm.setPubDate(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase("appclient")) {
							comm.setAppClient(StringUtils.toInt(
									xmlParser.nextText(), 0));
						} else if (tag.equalsIgnoreCase("reply")) {
							reply = new Reply();
						} else if (reply != null
								&& tag.equalsIgnoreCase("rauthor")) {
							reply.rauthor = xmlParser.nextText();
						} else if (reply != null
								&& tag.equalsIgnoreCase("rpubDate")) {
							reply.rpubDate = xmlParser.nextText();
						} else if (reply != null
								&& tag.equalsIgnoreCase("rcontent")) {
							reply.rcontent = xmlParser.nextText();
						} else if (tag.equalsIgnoreCase("refer")) {
							refer = new Refer();
						} else if (refer != null
								&& tag.equalsIgnoreCase("refertitle")) {
							refer.refertitle = xmlParser.nextText();
						} else if (refer != null
								&& tag.equalsIgnoreCase("referbody")) {
							refer.referbody = xmlParser.nextText();
						}
						// 通知信息
						else if (tag.equalsIgnoreCase("notice")) {
							comm.setNotice(new Notice());
						} else if (comm.getNotice() != null) {
							if (tag.equalsIgnoreCase("atmeCount")) {
								comm.getNotice().setAtmeCount(
										StringUtils.toInt(xmlParser.nextText(),
												0));
							} else if (tag.equalsIgnoreCase("msgCount")) {
								comm.getNotice().setMsgCount(
										StringUtils.toInt(xmlParser.nextText(),
												0));
							} else if (tag.equalsIgnoreCase("reviewCount")) {
								comm.getNotice().setReviewCount(
										StringUtils.toInt(xmlParser.nextText(),
												0));
							} else if (tag.equalsIgnoreCase("newFansCount")) {
								comm.getNotice().setNewFansCount(
										StringUtils.toInt(xmlParser.nextText(),
												0));
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					// 如果遇到标签结束，则把对象添加进集合中
					if (tag.equalsIgnoreCase("reply") && comm != null
							&& reply != null) {
						comm.getReplies().add(reply);
						reply = null;
					} else if (tag.equalsIgnoreCase("refer") && comm != null
							&& refer != null) {
						comm.getRefers().add(refer);
						refer = null;
					}
					break;
				}
				// 如果xml没有结束，则导航到下一个节点
				evtType = xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			throw AppException.xml(e);
		} finally {
			inputStream.close();
		}
		return comm;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Comment> CREATOR = new Creator<Comment>() {

		@Override
		public Comment[] newArray(int size) {
			return new Comment[size];
		}

		@Override
		public Comment createFromParcel(Parcel source) {
			return new Comment(source);
		}
	};
}
