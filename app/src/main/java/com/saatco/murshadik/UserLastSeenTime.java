package com.saatco.murshadik;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.Date;

public class UserLastSeenTime extends Application{

    /*
     * Copyright 2012 Google Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time,long lastActiveTime, Context applicationContext) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        if (lastActiveTime < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            lastActiveTime *= 1000;
        }

       /* if(lastActiveTime >= time){
            return "متصل الان";
        }*/

        long now = System.currentTimeMillis();

      /*  if (time >= now) {

            return "متصل الان";
            //return null;
        }*/

        Log.v("sohaib", "time now :" + new Date(now) +"time user"+ new Date(time));

        // TODO: localize
        final long diff = now - time;

         if (diff < MINUTE_MILLIS) {
            return "آخر ظهور قبل ثانية";

        } else if (diff < 2 * MINUTE_MILLIS) {
            return "اخر ظهور منذ دقيقة";

        } else if (diff < 50 * MINUTE_MILLIS) {
            return "دقائق مضت " + diff / MINUTE_MILLIS + " اخر ظهور";

        } else if (diff < 90 * MINUTE_MILLIS) {
            return "اخر ظهور ١ ساعات ";

        } else if (diff < 24 * HOUR_MILLIS) {
            return " ساعات " + diff / HOUR_MILLIS + " اخر ظهور";

        } else if (diff < 48 * HOUR_MILLIS) {
            return "اخر ظهور قبل يوم واحد";

        } else {
            return "منذ أيام " + diff / DAY_MILLIS + " اخر ظهور";
        }
    }

    public static String getOnlineStatus(long time,long lastActiveTime, Context applicationContext) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        if (lastActiveTime < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            lastActiveTime *= 1000;
        }

        long now = System.currentTimeMillis();

        if(lastActiveTime >= time){
            return "online";
        }

       /* if (time > now || time <= 0) {

            return "Online";
            //return null;
        }*/

        // TODO: localize
        final long diff = lastActiveTime - time;
        if (diff < MINUTE_MILLIS) {
            return "away";

        } else if (diff < 2 * MINUTE_MILLIS) {
            return "away";

        } else if (diff < 50 * MINUTE_MILLIS) {
            return "away";

        } else if (diff < 90 * MINUTE_MILLIS) {
            return "away";

        } else if (diff < 24 * HOUR_MILLIS) {
            return "away";

        } else if (diff < 48 * HOUR_MILLIS) {
            return "away";

        } else {
            return "away";
        }
    }

    public static String getTimeAgo2(long time, Context applicationContext) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "آخر ظهور قبل ثانية";

        } else if (diff < 2 * MINUTE_MILLIS) {
            return "اخر ظهور منذ دقيقة";

        } else if (diff < 50 * MINUTE_MILLIS) {
            return "دقائق مضت " + diff / MINUTE_MILLIS + " اخر ظهور";

        } else if (diff < 90 * MINUTE_MILLIS) {
            return "اخر ظهور ١ ساعات ";

        } else if (diff < 24 * HOUR_MILLIS) {
            return " ساعات " + diff / HOUR_MILLIS + " اخر ظهور";

        } else if (diff < 48 * HOUR_MILLIS) {
            return "اخر ظهور قبل يوم واحد";

        } else {
            return "منذ أيام " + diff / DAY_MILLIS + " اخر ظهور";
        }
    }


}
