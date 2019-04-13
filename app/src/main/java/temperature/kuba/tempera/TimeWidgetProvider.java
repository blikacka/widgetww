package temperature.kuba.tempera;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TimeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Toast.makeText(context, "TimeWidgetRemoved id(s):"+appWidgetIds, Toast.LENGTH_SHORT).show();
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        Toast.makeText(context, "onDisabled():last widget instance removed", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 3 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 100 * 3, 20000 , pi);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final ComponentName thisWidget = new ComponentName(context, TimeWidgetProvider.class);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    String[] data = getData().split(":::");
                    for (final int widgetId : appWidgetManager.getAppWidgetIds(thisWidget)) {
                        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.time_widget_layout);
                        remoteViews.setTextViewText(R.id.widget_title, data[0]);
                        remoteViews.setTextViewText(R.id.widget_text, data[1]);
                        appWidgetManager.updateAppWidget(widgetId, remoteViews);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager, int appWidgetId,
                                          Bundle newOptions) {
        //Do some operation here, once you see that the widget has change its size or position.
        Toast.makeText(context, "onAppWidgetOptionsChanged() called", Toast.LENGTH_SHORT).show();
    }

    public static String getData() {
        HttpURLConnection urlConnection = null;

        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL("http://192.168.2.35/tempova.php");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return result.toString();
    }
}

