package mx.kobit.marvel.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import mx.kobit.marvel.database.HeroesContract;
import mx.kobit.marvel.database.cnxSQLite;
import mx.kobit.marvel.model.Hero;

public class DownloadHeroesAsyncTask extends AsyncTask<Void, Void, ArrayList<Hero>> {

    public DownloadHeroesInterface delegate;
    private Context context;


    //Constructor
    public DownloadHeroesAsyncTask(Context context){
        this.context = context;
    }

    public interface DownloadHeroesInterface{
        void onHeroesDownloaded(ArrayList<Hero> heroesList);
    }

    @Override
    protected ArrayList<Hero> doInBackground(Void... voids) {
        //Config the Marvel API parameters
        int API_LIMIT = 100;
        int API_TS = 1;
        String API_PUBLIC_KEY = "9f1f57800acfd15dd91612c432837719";
        String API_HASH = "5106184a7d936070341190466c784f05";

        String heroData;
        ArrayList<Hero> heroList = null;

        try{
            heroData = downloadData(new URL("https://gateway.marvel.com/v1/public/characters?limit=" + API_LIMIT + "&apikey=" + API_PUBLIC_KEY + "&ts="+ API_TS +"&hash=" + API_HASH));
            heroList = parseDataFromJson(heroData);
            //Save to database all the data
            saveHeroesOnDatabase(heroList);
        }catch (IOException e){
            e.printStackTrace();
        }
        return heroList;
    }

    //Method to add a list of heroes
    private void saveHeroesOnDatabase(ArrayList<Hero> heroList) {
        cnxSQLite cnxSQLite = new cnxSQLite(context);
        SQLiteDatabase database = cnxSQLite.getWritableDatabase();
        for (Hero hero : heroList){
            ContentValues contentValues = new ContentValues();
            contentValues.put(HeroesContract.HeroesColumns.ID, hero.getId());
            contentValues.put(HeroesContract.HeroesColumns.NAME, hero.getName());
            contentValues.put(HeroesContract.HeroesColumns.DESCRIPTION, hero.getDescription());
            contentValues.put(HeroesContract.HeroesColumns.THUMBNAIL, hero.getThumbnail());

            database.insert(HeroesContract.HeroesColumns.TABLE_NAME, null, contentValues);
        }
        cnxSQLite.close();
    }

    @Override
    protected void onPostExecute(ArrayList<Hero> heroList) {
        super.onPostExecute(heroList);
        delegate.onHeroesDownloaded(heroList);
    }

    private ArrayList<Hero> parseDataFromJson (String heroesData){
        ArrayList<Hero> heroList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(heroesData);
            JSONObject dataJsonObject = jsonObject.getJSONObject("data");
            JSONArray resultsJsonArray = dataJsonObject.getJSONArray("results");

            for(int i=0;i<resultsJsonArray.length();i++){
                JSONObject resultsJsonObject = resultsJsonArray.getJSONObject(i);
                int id = resultsJsonObject.getInt("id");
                String name = resultsJsonObject.getString("name");
                String description = resultsJsonObject.getString("description");

                JSONObject thumbnailJsonObject = resultsJsonObject.getJSONObject("thumbnail");
                String path = thumbnailJsonObject.getString("path");
                String extension = thumbnailJsonObject.getString("extension");
                String thumbnail = path + "." + extension;

                //add to List
                heroList.add(new Hero(id,name,description,thumbnail));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return heroList;
    }

    private String downloadData(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader
                    = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
