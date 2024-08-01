package com.example.socialmedia.util

class Util {
    companion object{
        val now = System.currentTimeMillis()
        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAYS_MILLIS = 24 * HOUR_MILLIS

        fun getTimeAgo(time:Long) : String?{

            if(time> now || time<=0){
                return null
            }

            val diff = now -time
             return if(diff < MINUTE_MILLIS){
                "just now"
            }else if(diff < 2* MINUTE_MILLIS){
                "a min ago"
             }else if(diff< 60* MINUTE_MILLIS){
                 (diff/ MINUTE_MILLIS).toString()+"min ago"
             }else if(diff<90* MINUTE_MILLIS){
                 "a hour ago"
             }else if(diff<24* HOUR_MILLIS){
                 (diff/ HOUR_MILLIS).toString()+"hour ago"
             }else if(diff<48* HOUR_MILLIS){
                 "yesterday"
             }else {
                 (diff / DAYS_MILLIS).toString() + "days ago"
             }
        }
    }
}