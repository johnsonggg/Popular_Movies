package com.example.android.populoarmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private GridView gridView;
    private GridviewAdapter gridViewAdapter;
    private int numColumn = 3;
    String[] items = new String[]{};

//            "http://image.tmdb.org/t/p/w342/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
//            "http://image.tmdb.org/t/p/w342/7SGGUiTE6oc2fh9MjIk5M00dsQd.jpg",
//            "http://image.tmdb.org/t/p/w342/uXZYawqUsChGSj54wcuBtEdUJbh.jpg",
//            "http://image.tmdb.org/t/p/w342/s5uMY8ooGRZOL0oe4sIvnlTsYQO.jpg",
//            "http://image.tmdb.org/t/p/w342/aBBQSC8ZECGn6Wh92gKDOakSC8p.jpg"

    //---the images to display---
//    Integer[] items = {
//            R.drawable.test1,
//            R.drawable.test1,
//            R.drawable.test1,
//            R.drawable.test1,
//            R.drawable.test1,
//            R.drawable.test1,
//    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setHasOptionsMenu(true);
        gridView = (GridView) findViewById(R.id.gridview);
        GridviewAdapter gridviewAdapter = new GridviewAdapter(MainActivity.this,items);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String rutaDeLaImagen = gridViewAdapter.getItem(position).toString();

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + rutaDeLaImagen), "image/*");
                startActivity(intent);
            }
        });

    }

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            public void onItemClick(AdapterView<?> parent,View v, int position, long id)
//            {
//                Toast.makeText(getBaseContext(),"pic" + (position + 1) + "selected",Toast.LENGTH_SHORT).show();
//            }
//        });

    //bitmap = getBitmapFromURL("http://image.tmdb.org/t/p/w342/aBBQSC8ZECGn6Wh92gKDOakSC8p.jpg");


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getOverflowMenu();
        return super.onCreateOptionsMenu(menu);
    }

    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        item.setCheckable(false);
        int id = item.getItemId();

//        switch (id){
//            case R.id.oneColumn:
//                numColumn=1;
//                item.setChecked(true);
//                break;
//            case R.id.twoColumn:
//                numColumn=2;
//                item.setChecked(true);
//                break;
//            case R.id.threeColumn:
//                numColumn=3;
//                item.setChecked(true);
//                break;
//            case R.id.fourColumn:
//                numColumn=4;
//                item.setChecked(true);
//                break;
//            default:
//                break;
//        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            GetPic getpic = new GetPic();
            getpic.execute("popularity.desc");
            return true;
        }
        gridView.setNumColumns(numColumn);
        gridView.setAdapter(new GridviewAdapter(MainActivity.this,items));
        return super.onOptionsItemSelected(item);
    }

    /**
     * Getting All Images Path
     *
     * @param
     * @return ArrayList with images Path
     */

    public class GetPic extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = GetPic.class.getSimpleName();

        private String[] getPopMovieFromJson(String popMovieJsonStr)
                throws JSONException {

            final String TMDB_PIC_SIZE = "w342";
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER_PATH = "poster_path";
            JSONObject movieJson = new JSONObject(popMovieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

            String[] items = new String[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject eachMovie = movieArray.getJSONObject(i);
                String picFileName = eachMovie.getString(TMDB_POSTER_PATH);
                items[i] = "http://image.tmdb.org/t/p/" + TMDB_PIC_SIZE + picFileName;
                Log.d(LOG_TAG, "ITEMS_VALUE" + items[i]);
            }

            return items;
        }

        protected String[] doInBackground(String... params){
            //if there's no rule of ordering,there is nothing to look up.Verify size of params.
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String popMovieJsonStr = null;
            String sort_by = "popularity.desc";
            String api_key = "22fae03d55ebd9a2ad6df0f36e489087";
            try {
                //The format is : http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=22fae03d55ebd9a2ad6df0f36e489087
                final String POP_MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(POP_MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(API_KEY, api_key)
                        .build();
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                popMovieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getPopMovieFromJson(popMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
    }
}
//            public static ArrayList<String> getAllShownImagesPath (Activity activity){
//                Uri url;
//                Cursor cursor;
//                int column_index_data, column_index_folder_name;
//                ArrayList<String> listOfAllImages = new ArrayList<String>();
//                String absolutePathOfImage = null;
//                url = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//                String[] projection = {MediaStore.MediaColumns.DATA,
//                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
//
//                cursor = activity.getContentResolver().query(url, projection, null,
//                        null, MediaStore.Images.Media.DATE_ADDED);
//
//                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//                column_index_folder_name = cursor
//                        .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//                while (cursor.moveToNext()) {
//                    absolutePathOfImage = cursor.getString(column_index_data);
//
//                    listOfAllImages.add(absolutePathOfImage);
//                }
//
//                Collections.reverse(listOfAllImages);
//
//                for (int i = 0; i < listOfAllImages.size(); i++) {
//                    System.out.println(listOfAllImages.get(i));
//                }
//                return listOfAllImages;
////        Log.d("Value of listOfAllImages",listOfAllImages );
//            }





