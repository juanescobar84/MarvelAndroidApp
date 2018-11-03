package mx.kobit.marvel.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import mx.kobit.marvel.R;
import mx.kobit.marvel.adapters.DownloadHeroesAsyncTask;
import mx.kobit.marvel.adapters.RecyclerViewAdapterHeroes;
import mx.kobit.marvel.database.HeroesContract;
import mx.kobit.marvel.database.cnxSQLite;
import mx.kobit.marvel.model.Hero;
import mx.kobit.marvel.utils.Utils;

public class MainActivity extends AppCompatActivity implements DownloadHeroesAsyncTask.DownloadHeroesInterface {

    //Config the RecyclerView
    private RecyclerView heroRecyclerView;
    private List<Hero> heroList = new ArrayList<>();

    //Firebase object to validate user logged in
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Validate the user session
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            //Usuario no está logueado, redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        //Prepare the RecyclerView when the data is ready
        heroRecyclerView = findViewById(R.id.recyclerview_heroes);
        //Load heroes from local database
        getHeroesFromDb();
    }

    //Fill the list with the elements in the local database
    private void getHeroesFromDb() {
        cnxSQLite cnxSQLite = new cnxSQLite(this);
        SQLiteDatabase database = cnxSQLite.getReadableDatabase();

        Cursor cursor = database.query(HeroesContract.HeroesColumns.TABLE_NAME, null, null, null, null, null, "name");
        //If there are heroes stored in local database, the info is loaded from the device
        if (cursor.getCount() > 0) {
            ArrayList<Hero> heroList = new ArrayList<>();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(HeroesContract.HeroesColumns.ID_COLUMN_INDEX);
                String name = cursor.getString(HeroesContract.HeroesColumns.NAME_COLUMN_INDEX);
                String description = cursor.getString(HeroesContract.HeroesColumns.DESCRIPTION_COLUMN_INDEX);
                String thumbnail = cursor.getString(HeroesContract.HeroesColumns.THUMBNAIL_COLUMN_INDEX);

                heroList.add(new Hero(id, name, description, thumbnail));
            }
            cursor.close();
            cnxSQLite.close();
            fillHeroesList(heroList);
        }
        //Else the info is downloaded from internet
        else{
            downloadHeroesFromInternet();
        }
    }

    //Fill the list with the elements in the Marvel API
    private void downloadHeroesFromInternet() {
        if(Utils.isNetworkAvailable(this)) {
            DownloadHeroesAsyncTask downloadHeroesAsyncTask = new DownloadHeroesAsyncTask(this);
            downloadHeroesAsyncTask.delegate = this;
            downloadHeroesAsyncTask.execute();
        }
        else {
            Toast.makeText(this, R.string.strNetworkStatus, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHeroesDownloaded(ArrayList<Hero> heroList) {
        fillHeroesList(heroList);
    }

    //Fill the RecyclerView with the Adapter info
    private void fillHeroesList(ArrayList<Hero> heroList){
        //Adapter
        final RecyclerViewAdapterHeroes heroAdapter = new RecyclerViewAdapterHeroes(this, heroList);
        heroRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        heroRecyclerView.setAdapter(heroAdapter);
    }

    //Add a top right menu to show the logout button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                userSignOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Logout the user
    public void userSignOut() {
        // Sing Out the User.
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}