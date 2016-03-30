package com.cyan.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import com.cyan.app.MyApplication;
import com.cyan.bean.Comment;
import com.cyan.community.R;
import com.cyan.ui.MessageActivity;
import com.cyan.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.push.PushConstants;
import de.greenrobot.daoexample.CommentToMe;
import de.greenrobot.daoexample.CommentToMeDao;
import de.greenrobot.daoexample.ReplyToMe;
import de.greenrobot.daoexample.ReplyToMeDao;

/**
 * Created by Administrator on 2016/1/13.
 */
public class MyPushMessageReceiver extends BroadcastReceiver{
    private CommentToMeDao commentToMeDao;
    private ReplyToMeDao replyToMeDao;
    private String username;
    private String content;
    private String title;
    @Override
    public void onReceive(Context context, Intent intent) {
        if((boolean) SPUtils.get(context, "settings","message_key", false))
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            Log.d("bmob", "BmobPushDemo收到消息：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
           // Toast.makeText(context, "BmobPushDemo收到消息：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING), Toast.LENGTH_SHORT).show();
            try {
                createNotification(context,intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}
    public void createNotification(Context context,Intent intent) throws JSONException {
        final NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        String message=intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
        JSONObject jsonObject=new JSONObject(message);
        Comment comment=new Comment();
        Gson gson=new Gson();
        Intent intent1=new Intent(context, MessageActivity.class);
     if(!new JSONObject(jsonObject.optString("alert")).optString("commentToMe").equals("")){
         commentToMeDao= MyApplication.getInstance().getDaoSession().getCommentToMeDao();
         CommentToMe commentToMe=gson.fromJson(new JSONObject(jsonObject.getString("alert")).getString("commentToMe"), new TypeToken<CommentToMe>() {
         }.getType());
         commentToMeDao.insert(commentToMe);
         intent1.putExtra("mode", "comment");
         username=commentToMe.getUser_name();
         content=commentToMe.getComment_content();
         title=username+"评论了你";
     }else{
         replyToMeDao=MyApplication.getInstance().getDaoSession().getReplyToMeDao();
         ReplyToMe replyToMe=gson.fromJson(new JSONObject(jsonObject.getString("alert")).getString("replyToMe"), new TypeToken<ReplyToMe>() {
         }.getType());
         replyToMeDao.insert(replyToMe);
         intent1.putExtra("mode", "reply");
         username=replyToMe.getUser_name();
         content=replyToMe.getComment_content();
         title=username+"回复了你";
     }
       // DatabaseUtil.getInstance(context).insertCommentToMe(commentToMe);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0,
                intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        if(Build.VERSION.SDK_INT >=21)
                // 通过Notification.Builder来创建通知，注意API Level
                // API11之后才支持
        {Notification notify2 = new Notification.Builder(context)
                         .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                                // icon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setTicker(comment.getContent())// 设置在status
                                // bar上显示的提示文字
                .setContentTitle(title)// 设置在下拉status
                                // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                        .setContentText(content)// TextView中显示的详细内容
                        .setContentIntent(pendingIntent2) // 关联PendingIntent
                        .setFullScreenIntent(pendingIntent2,true)
                        // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                        .getNotification(); // 需要注意build()是在API level
        // 16及之后增加的，在API11中可以使用getNotificatin()来代替
        notify2.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notify2.flags = Notification.FLAG_AUTO_CANCEL|Notification.FLAG_SHOW_LIGHTS;;
        manager.notify(0, notify2);}else{
            Notification notify2 = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                            // icon)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setTicker(comment.getContent())// 设置在status
                            // bar上显示的提示文字
                    .setContentTitle(title)// 设置在下拉status
                            // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                    .setContentText(content)// TextView中显示的详细内容
                    .setContentIntent(pendingIntent2) // 关联PendingIntent
                            // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                    .getNotification(); // 需要注意build()是在API level
            // 16及之后增加的，在API11中可以使用getNotificatin()来代替


            notify2.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notify2.flags = Notification.FLAG_AUTO_CANCEL|Notification.FLAG_SHOW_LIGHTS;;
            manager.notify(0, notify2);
        }
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                manager.cancel(0);
            }
        },5000);
    }
}
