package com.example.bcard;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

public class contactAdapter extends ArrayAdapter<Contact> {

    private static final String TAG = "contactAdapter";

    private Context mContext;
    private int lastPosition =-1;
    ArrayList<Contact> object;
    private int mReesource;


    private static class ViewHolder {
        EditText company;
        EditText email;
        EditText website;
        EditText phone;
        EditText person;
        EditText address;
        ImageView image;
    }
    public contactAdapter(Context context, int resource, ArrayList<Contact> objects){
            super(context,resource,objects);
            mContext=context;
            mReesource=resource;
            object=objects;
    }

    public int getCount() {
        return this.object.size();
    }
    public Contact getItem(int index) {
        return this.object.get(index);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        setupImageLoader();

        String imgUrl = getItem(position).getImgUrl();
        String company = getItem(position).getCompany();
        String email = getItem(position).getEmail();
        String website = getItem(position).getWebsite();
        String phone = getItem(position).getPhone();
        String person = getItem(position).getPerson();
        String address = getItem(position).getAddress();

        try {

            final View result;


            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.model, parent, false);
                holder = new ViewHolder();
                holder.company = (EditText) convertView.findViewById(R.id.company);
                holder.email = (EditText) convertView.findViewById(R.id.email);
                holder.website = (EditText) convertView.findViewById(R.id.website);
                holder.phone = (EditText) convertView.findViewById(R.id.phone);
                holder.person = (EditText) convertView.findViewById(R.id.name);
                holder.address = (EditText) convertView.findViewById(R.id.address);
                holder.image = (ImageView) convertView.findViewById(R.id.card);

                result = convertView;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);

            result.startAnimation(animation);
            lastPosition = position;

            holder.company.setText(company);
            holder.email.setText(email);
            holder.website.setText(website);
            holder.phone.setText(phone);
            holder.person.setText(person);
            holder.address.setText(address);

            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed/image_failed", null, mContext.getPackageName());
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            imageLoader.displayImage(imgUrl, holder.image, options);


            return convertView;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage());
            return convertView;
        }
    }

    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }
}


