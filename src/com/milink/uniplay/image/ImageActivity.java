package com.milink.uniplay.image;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.milink.api.v1.MilinkClientManager;
import com.milink.api.v1.type.ErrorCode;
import com.milink.api.v1.type.MediaType;
import com.milink.api.v1.type.ReturnCode;
import com.milink.uniplay.Device;
import com.milink.uniplay.MilinkClient;
import com.milink.uniplay.R;

import java.util.ArrayList;

public class ImageActivity extends Activity implements IImageCallback{

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private String timeout = "5000";
    
    private ImageView imageView;
    private Bitmap imageBitmap;
    
    Intent intent;
    Bundle bundle;
    
    int index = 0; //定义当前图片的坐标
    int size = 0; //一共有多少张图片
    Photos photos; //图片的集合
    String path = ""; //图片路径
    Handler uiHandler = null; // uiHandler处理图片的变化

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_details);
        
        MilinkClient.mMilinkClient.setCallback(this);
        if ((intent = getIntent()) != null) {
            bundle = intent.getBundleExtra("bundle");
            index = bundle.getInt("index");
            photos = (Photos) bundle.getSerializable("photos");
            size = photos.size();
            path = photos.get(index).getFilePath();
            Log.v("index", index + "");
            Toast.makeText(getApplicationContext(), "index" + index, Toast.LENGTH_LONG).show();
        }
        imageView = (ImageView)findViewById(R.id.img);
        
        imageBitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(imageBitmap);
        
        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case LEFT:
                        index--;
                        Log.v("lee index", index + "");
                        if (index < 0) {
                            index++;
                            break;
                        } else {
                            move();
                        }
                        break;
                    case RIGHT:
                        index++;
                        Log.v("lee index", index + "");
                        if (index > size - 1) {
                            index--;
                            break;
                        } else {
                            move();
                        }
                        break;
                    default:
                        break;
                }
            };
        };

        imageView.setOnTouchListener(new OnTouchListener() {
            MotionEvent begin = null, end = null;
            float beginX = 0, endX = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                mGestureDetector.onTouchEvent(event);
                
                Log.v("lee", "x = " + event.getX() + " y = " + event.getY() + "action = " + event.getAction());
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        begin = event;
                        beginX =  begin.getX();
                        Log.v("lee", "begin");
//                        showPhoto();
                        break;
                    case MotionEvent.ACTION_UP:
                        end = event;
                        endX = end.getX();
                        Log.v("lee", "beign = " + begin.getX() + " end = " + end.getX());
                        if(beginX > endX) {
                            Log.v("lee", "left");
                            uiHandler.sendEmptyMessage(LEFT);
                        } else {
                            Log.v("lee", "right");
                            uiHandler.sendEmptyMessage(RIGHT);
                        }
                        Log.v("lee", "beginX = " + beginX + " endX = " + endX);
//                        showPhoto();
                        break;
                    default:
                        break;
                }
                
                
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mMenuItem = menu.add("push");
        mMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mMenuItem.setIcon(android.R.drawable.ic_menu_share);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("push")) {
            Log.d("lee", "push");

            ArrayList<Device> deviceList = null;
            synchronized (MilinkClient.mDeviceList) {
                deviceList = (ArrayList<Device>) MilinkClient.mDeviceList.clone();
            }
            final ArrayList<Device> finalDeviceList = deviceList;
            final ArrayList<String> names = new ArrayList<String>();
            for (Device device : finalDeviceList) {
                names.add(device.name);
            }
            String[] deviceNames = new String[names.size()];
            names.toArray(deviceNames);

            new AlertDialog.Builder(this).setTitle(R.string.deviceListName).setItems(
                    deviceNames,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            if (pos == 0) {
                                return;
                            }
                            String deviceId = finalDeviceList.get(pos).id;
                            MilinkClientManager mMilinkClientManager = MilinkClient.mMilinkClient
                                    .getManagerInstance();
                            ReturnCode retcode = mMilinkClientManager.connect(deviceId,
                                    Integer.valueOf(timeout));
                            Log.d("lee", "ret code: " + retcode);
                        }

                    })
                    .create().show();

            return true;
        }
        return false;
    }

    public void move() {
        photos = (Photos) bundle.getSerializable("photos");
        path = photos.get(index).getFilePath();
        imageBitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(imageBitmap);
        showPhoto(path);
    }
    public void initShowPhoto() {
        MilinkClientManager mMilinkClientManager = MilinkClient.mMilinkClient.getManagerInstance();
        Log.v("image", "" + mMilinkClientManager.startShow());
        showPhoto(path);
    }

    public void showPhoto(String path) {
        MilinkClientManager mMilinkClientManager = MilinkClient.mMilinkClient.getManagerInstance();
        Log.v("image path", path);
        ReturnCode retcode = mMilinkClientManager.show(path);
        Log.v("image", "" + retcode);
    }

    @Override
    public String getPrevPhoto(String uri, boolean isRecyle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNextPhoto(String uri, boolean isRecyle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onConnected() {
        initShowPhoto();
        Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectedFailed(ErrorCode errorCode) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLoading() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPlaying() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStopped() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPaused() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onVolume(int volume) {
        // TODO Auto-generated method stub
        
    }

}
