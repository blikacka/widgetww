package temperature.kuba.tempera;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("LOGG", "onreceive se vola");
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    String[] data = TimeWidgetProvider.getData().split(":::");
                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
                    //Acquire the lock
                    wl.acquire();

                    Log.d("LOGG", "VOLA SE RUN");

                    //You can do the processing here update the widget/remote views.
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.time_widget_layout);
                    remoteViews.setTextViewText(R.id.widget_title, data[0]);
                    remoteViews.setTextViewText(R.id.widget_text, data[1]);
                    ComponentName thiswidget = new ComponentName(context, TimeWidgetProvider.class);
                    AppWidgetManager manager = AppWidgetManager.getInstance(context);
                    manager.updateAppWidget(thiswidget, remoteViews);
                    //Release the lock
                    wl.release();
                } catch (Exception e) {
                    Log.e("LOGG", e.getMessage());
                }
            }
        });

        thread.start();
    }
}
