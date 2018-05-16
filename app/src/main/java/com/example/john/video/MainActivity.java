package com.example.john.video;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity  extends Activity implements AdapterView.OnItemClickListener {
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) this.findViewById(R.id.lt1);

        String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID };

        String[] mediaColumns = { MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE };

        //cursor = (MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns,null,null,null);

        cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        ArrayList<VideoViewInfo> videoRows = new ArrayList<VideoViewInfo>();

        if (cursor.moveToFirst()) {
            do {

                VideoViewInfo newVVI = new VideoViewInfo();
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                Cursor thumbCursor = managedQuery(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    newVVI.thumbPath = thumbCursor.getString(thumbCursor
                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                    Log.v("", newVVI.thumbPath);
                }

                newVVI.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                newVVI.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                Log.v("", newVVI.title);
                newVVI.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                Log.v("", newVVI.mimeType);
                videoRows.add(newVVI);
            } while (cursor.moveToNext());
        }
        listView.setAdapter(new VideoGalleryAdapter(this, videoRows));
        listView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        if (cursor.moveToPosition(position)) {
            int fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE);
            String videoFilePath = cursor.getString(fileColumn);
            String mimeType = cursor.getString(mimeColumn);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            File newFile = new File(videoFilePath);
            intent.setDataAndType(Uri.fromFile(newFile), mimeType);
            startActivity(intent);
        }
    }


}
class VideoViewInfo {
    String filePath;
    String mimeType;
    String thumbPath;
    String title;
}

class VideoGalleryAdapter extends BaseAdapter {
    private Context context;
    private List<VideoViewInfo> videoItems;

    LayoutInflater inflater;

    public VideoGalleryAdapter(Context _context,
                               ArrayList<VideoViewInfo> _items) {
        context = _context;
        videoItems = _items;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return videoItems.size();
    }

    public Object getItem(int position) {
        return videoItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View videoRow = inflater.inflate(R.layout.row, null);
        ImageView videoThumb = (ImageView) videoRow
                .findViewById(R.id.ImageView);
        if (videoItems.get(position).thumbPath != null) {
            videoThumb.setImageURI(Uri
                    .parse(videoItems.get(position).thumbPath));
        }

        TextView videoTitle = (TextView) videoRow
                .findViewById(R.id.TextView);
        videoTitle.setText(videoItems.get(position).title);

        return videoRow;
    }

    }



