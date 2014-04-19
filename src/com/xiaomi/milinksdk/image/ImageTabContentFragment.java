package com.xiaomi.milinksdk.image;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomi.milinksdk.R;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageTabContentFragment extends Fragment {
    private Context mContext;
    String[] mediaColumns = new String[] {
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DATA
    };
    Photos photos = new Photos();
    GridView gridView;

    public ImageTabContentFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.image_content_grid, container, false);
        Cursor mCursor = mContext.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaColumns, null,
                        null, null);
        
        while(mCursor.moveToNext()) {
          Log.v("lee", mCursor.getString(mCursor
                  .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)));
          Log.v("lee", mCursor.getString(mCursor
                  .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
          Photo photo = new Photo();
          photo.setFileName(mCursor.getString(mCursor
                  .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)));
          photo.setFilePath(mCursor.getString(mCursor
                  .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
          photos.add(photo);
        }
        
        
        gridView = (GridView)fragView.findViewById(R.id.gridview);
        ImageAdapter imageAdapter = new ImageAdapter(mContext);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, ImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("photos", photos);
                bundle.putInt("index", position);
                intent.putExtra("bundle", bundle);
                
                startActivity(intent);
                
            }});
        /*return super.onCreateView(inflater, container, savedInstanceState);*/
        return fragView;
    }
    static class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView info;
    }
    
    class ImageAdapter extends BaseAdapter{
        private LayoutInflater mInflater = null;
        private ImageAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return photos.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            Log.v("lee", "" + position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView  = mInflater.inflate(R.layout.image_content, null);
                viewHolder.img = (ImageView)convertView.findViewById(R.id.image);
                BitmapFactory.Options bm = new BitmapFactory.Options();
                bm.inSampleSize = 16;
                Bitmap bmp = BitmapFactory.decodeFile(photos.get(position).getFilePath(), bm);
                viewHolder.img.setImageBitmap(bmp);
                convertView.setTag(viewHolder);
                /*viewHolder.title = (TextView)convertView.findViewById(R.id.tv);
                viewHolder.info = (TextView)convertView.findViewById(R.id.info);*/
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            
            return convertView;
        }
        
    }
}



class Photos implements Serializable{
    ArrayList<Photo> photoContainer = new ArrayList<Photo>();
    public void add(Photo photo) {
        photoContainer.add(photo);
    }
    public Photo get(int index) {
        return photoContainer.get(index);
    }
    public int size() {
        return photoContainer.size();
    }
}

class Photo implements Serializable{
    public String fileName;
    public String filePath;
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
}