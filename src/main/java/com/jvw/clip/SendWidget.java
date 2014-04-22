package com.jvw.clip;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link SendWidgetConfigureActivity SendWidgetConfigureActivity}
 */
public class SendWidget extends AppWidgetProvider {

	public static void updateWidget(Context context, AppWidgetManager manager, int widgetId, RemoteViews views) {
		manager.updateAppWidget(widgetId, views);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int id = appWidgetIds[i];
			//updateWidget(context, appWidgetManager, id);
		}
	}
}


