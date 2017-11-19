package com.simplicity.maged.mccobjectdetection.components.decisionModel;

import java.io.InputStream;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class AppService {
	public String name;
	public ArrayList<String> leafs;

	public AppService() {
		name = "";
		leafs = new ArrayList<String>();
	}

	public static AppService getAppService(Context context, String findName) {
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			InputStream in_s = context.getAssets().open("services.xml");
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in_s, "UTF-8");
			ArrayList<AppService> services = parseXML(parser);

			if (services != null) {
				for (AppService appService : services) {
					int x = findName.compareTo(appService.name);
					if (x == 0) {
						return appService;
					}
				}
			}
		} catch (Exception e) {
			Log.e("simplicity", e.toString());
		}
		return null;
	}

	private static ArrayList<AppService> parseXML(XmlPullParser parser) {
		ArrayList<AppService> appServices = null;
		int eventType;
		try {
			eventType = parser.getEventType();

			AppService currentService = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					appServices = new ArrayList<AppService>();
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if (name.equals("service")) {
						currentService = new AppService();
					} else if (currentService != null) {
						if (name.equals("name")) {
							currentService.name = parser.nextText();
						} else if (name.equals("leaf")) {
							currentService.leafs.add(parser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("service")
							&& currentService != null) {
						appServices.add(currentService);
					}
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e("simplicity", e.toString());
		}
		return appServices;
	}
}
