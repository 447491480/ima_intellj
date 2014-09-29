package net.oschina.app.v2.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DailyEnglish {

	private String sid;
	private String tts;
	private String content;
	private String note;
	private String translation;
	private String date;
	private String picture;
	private String pictureBig;
	
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getTts() {
		return tts;
	}

	public void setTts(String tts) {
		this.tts = tts;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}
	
	//{
	//  "sid": "1047", 
	//  "tts": "http://news.iciba.com/admin/tts/2014-09-22.mp3", 
	//  "content": "Success seems to be largely a matter of hanging on after others have let go.", 
	//  "note": "成功在很大程度上似乎就是别人放弃后，你仍然坚持下去了。(WIlliam Feather)", 
	//  "love": "1469", 
	//  "translation": "词霸小编：一种只生长在中国最东边的毛竹，4年只长了3cm，第5年开始，每天以30cm的速度疯狂生长，只用6周就长到15米。其实，前面的4年，毛竹根部在土壤里延伸了数百米。人生亦是如此，不要担心你此时此刻的付出得不到回报，因为这些付出都是为了扎根。人生需要储备！多少人，没熬过那三厘米！", 
	//  "picture": "http://cdn.iciba.com/news/word/2014-09-22.jpg", 
	//  "picture2": "http://cdn.iciba.com/news/word/big_2014-09-22b.jpg", 
	//  "caption": "词霸每日一句", 
	//  "dateline": "2014-09-22", 
	//  "s_pv": "9750", 
	//  "sp_pv": "968", 
	//  "tags": [
	//      {
	//          "id": "4", 
	//          "name": "励志"
	//      }, 
	//      {
	//          "id": "10", 
	//          "name": "正能量"
	//      }, 
	//      {
	//          "id": "13", 
	//          "name": "名人名言"
	//      }
	//  ], 
	//  "fenxiang_img": "http://cdn.iciba.com/web/news/longweibo/imag/2014-09-22.jpg"
	//}
	public static DailyEnglish make(String jsonStr) {
		try {
			JSONObject json = new JSONObject(jsonStr);
			DailyEnglish de = new DailyEnglish();
			de.setSid(json.optString("sid"));
			de.setContent(json.getString("content"));
			de.setNote(json.getString("note"));
			de.setTranslation(json.optString("translation"));
			de.setDate(json.optString("dateline"));
			de.setPicture(json.optString("picture"));
			de.setPictureBig(json.optString("picture2"));
			return de;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPictureBig() {
		return pictureBig;
	}

	public void setPictureBig(String pictureBig) {
		this.pictureBig = pictureBig;
	}
}
