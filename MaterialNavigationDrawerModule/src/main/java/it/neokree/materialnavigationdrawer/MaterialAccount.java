package it.neokree.materialnavigationdrawer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import it.neokree.materialnavigationdrawer.util.Utils;

/**
 * Created by neokree on 11/12/14.
 */
public class MaterialAccount {

    // datas

    private Drawable photo;
    private Drawable background;
    private Drawable circularPhoto;
    private int backgroundColor;
    private String title;
    private String subTitle;
    private int accountNumber;

    private Resources resources;
    private OnAccountDataLoaded listener;

    public static final int FIRST_ACCOUNT = 0;
    public static final int SECOND_ACCOUNT = 1;
    public static final int THIRD_ACCOUNT = 2;

    // constructors

    public MaterialAccount(Resources res,String title, String subTitle, int photo,Bitmap background) {
        this.title = title;
        this.subTitle = subTitle;
        resources = res;

        // resize and caching bitmap
        resizePhotoResource.execute(photo);
        if(background != null)
            resizeBackgroundBitmap.execute(background);

    }

    /**
     * Create an account
     * @param res
     * @param title
     * @param subTitle
     * @param photo
     * @param background Background, may be a Resource or a Color
     * @param useBackgroundHasColor True if the background parameter is a Color. Default is false.
     */
    public MaterialAccount(Resources res, String title, String subTitle, int photo, int background, boolean useBackgroundHasColor) {
        this.title = title;
        this.subTitle = subTitle;
        resources = res;

        // resize and caching bitmap
        resizePhotoResource.execute(photo);
        if (useBackgroundHasColor) {
            this.backgroundColor = background;
        } else {
            resizeBackgroundResource.execute(background);
        }
    }

    public MaterialAccount(Resources res,String title, String subTitle, Bitmap photo, int background) {
        this.title = title;
        this.subTitle = subTitle;
        resources = res;

        // resize and caching bitmap
        if(photo != null)
            resizePhotoBitmap.execute(photo);
        resizeBackgroundResource.execute(background);
    }

    public MaterialAccount(Resources res,String title, String subTitle, Bitmap photo, Bitmap background) {
        this.title = title;
        this.subTitle = subTitle;
        resources = res;

        // resize and caching bitmap
        if(photo != null)
            resizePhotoBitmap.execute(photo);
        if (background != null)
            resizeBackgroundBitmap.execute(background);
    }

    // setter

    public void setPhoto(int photo){
        resizePhotoResource.execute(photo);
    }

    public void setPhoto(Bitmap photo) {
        resizePhotoBitmap.execute(photo);
    }

    public void setPhoto(Drawable photo) {
        this.photo = photo;
    }

    public void setBackground(Bitmap background) {
        resizeBackgroundBitmap.execute(background);
    }

    public void setBackground(int background) {
        resizeBackgroundResource.execute(background);
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setAccountNumber(int number) {
        this.accountNumber = number;
    }

    public void setAccountListener(OnAccountDataLoaded listener) {
        this.listener = listener;
    }

    // getter

    public Drawable getPhoto() {
        return photo;
    }

    public Drawable getBackground() {
        return background;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean hasBackgroundColor() {
        return this.backgroundColor != 0;
    }

    public Drawable getCircularPhoto() {
        return circularPhoto;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    // custom

    public void recycle() {
        Utils.recycleDrawable(photo);
        Utils.recycleDrawable(circularPhoto);
        Utils.recycleDrawable(background);
    }

    public interface OnAccountDataLoaded {

        public void onUserPhotoLoaded(MaterialAccount account);

        public void onBackgroundLoaded(MaterialAccount account);
    }

    // asynctasks

    private AsyncTask<Integer,Void, BitmapDrawable> resizePhotoResource = new AsyncTask<Integer, Void, BitmapDrawable>() {
        @Override
        protected BitmapDrawable doInBackground(Integer... params) {
            Point photoSize = Utils.getUserPhotoSize(resources);

            Bitmap photo = Utils.resizeBitmapFromResource(resources,params[0],photoSize.x,photoSize.y);

            circularPhoto = new BitmapDrawable(resources,Utils.getCroppedBitmapDrawable(photo));
            return new BitmapDrawable(resources,photo);
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            photo = drawable;

            if(listener != null)
                listener.onUserPhotoLoaded(MaterialAccount.this);
        }
    };
    private AsyncTask<Bitmap,Void, BitmapDrawable> resizePhotoBitmap = new AsyncTask<Bitmap, Void, BitmapDrawable>() {
        @Override
        protected BitmapDrawable doInBackground(Bitmap... params) {
            Point photoSize = Utils.getUserPhotoSize(resources);


            Bitmap photo = Utils.resizeBitmap(params[0],photoSize.x,photoSize.y);
            params[0].recycle();

            circularPhoto = new BitmapDrawable(resources,Utils.getCroppedBitmapDrawable(photo));
            return new BitmapDrawable(resources,photo);
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            photo = drawable;

            if(listener != null)
                listener.onUserPhotoLoaded(MaterialAccount.this);
        }
    };
    private AsyncTask<Integer,Void, BitmapDrawable> resizeBackgroundResource = new AsyncTask<Integer, Void, BitmapDrawable>() {
        @Override
        protected BitmapDrawable doInBackground(Integer... params) {
            Point backSize = Utils.getBackgroundSize(resources);

            Bitmap back = Utils.resizeBitmapFromResource(resources,params[0],backSize.x,backSize.y);

            return new BitmapDrawable(resources,back);
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            background = drawable;

            if(listener != null)
                listener.onBackgroundLoaded(MaterialAccount.this);
        }
    };
    private AsyncTask<Bitmap,Void, BitmapDrawable> resizeBackgroundBitmap = new AsyncTask<Bitmap, Void, BitmapDrawable>() {
        @Override
        protected BitmapDrawable doInBackground(Bitmap... params) {
            Point backSize = Utils.getBackgroundSize(resources);

            Bitmap back = Utils.resizeBitmap(params[0],backSize.x,backSize.y);
            params[0].recycle();

            return new BitmapDrawable(resources,back);
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            background = drawable;

            if(listener != null)
                listener.onBackgroundLoaded(MaterialAccount.this);
        }
    };
}
