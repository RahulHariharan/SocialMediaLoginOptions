package com.example.syscon.facebooktest;

/**
 * Created by syscon on 10/3/15.
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by syscon on 6/3/15.
 */
public class ImageAdapter extends BaseAdapter {

    ArrayList<Integer> imageList;
    LayoutInflater inflater;
    Context mContext;

    ImageAdapter(Context context){

        imageList = new ArrayList<Integer>();
        for(int i=0; i<100; i++){

            int remainder = i%5;

            switch(remainder){

                case 0:
                    imageList.add(R.drawable.image_1);
                    break;
                case 1:
                    imageList.add(R.drawable.image_2);
                    break;
                case 2:
                    imageList.add(R.drawable.image_3);
                    break;
                case 3:
                    imageList.add(R.drawable.image_4);
                    break;
                case 4:
                    imageList.add(R.drawable.image_5);
                    break;
                default:
                    break;

            }

        }

        this.mContext = context;
        this.inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){

            convertView = inflater.inflate(R.layout.list_item,parent,false);

        }

        ImageButton imageView = (ImageButton)  convertView.findViewById(R.id.image_element);
        //imageView.setImageResource(R.mipmap.ic_launcher);
        //imageView.setImageBitmap(decodeSampledBitmapFromResource(this.mContext.getResources(),imageList.get(position),100,100));
        loadBitmap(imageList.get(position),imageView);

        imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {


            }
        });
        return convertView;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public void loadBitmap(int resId, ImageView imageView) {
        if (cancelPotentialWork(resId, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(this.mContext.getResources(), /*mPlaceHolderBitmap*/ null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            if (bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class BitmapWorkerTask extends AsyncTask<Integer,Void,Bitmap>{

        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;
        int dimension_width;
        int dimension_height;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            dimension_width = imageView.getWidth();
            dimension_height = imageView.getHeight();
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromResource(mContext.getResources(), data,dimension_width,dimension_height);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {

                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }


    }


}