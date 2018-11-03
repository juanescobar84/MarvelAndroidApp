package mx.kobit.marvel.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import mx.kobit.marvel.R;
import mx.kobit.marvel.database.HeroesContract;
import mx.kobit.marvel.database.cnxSQLite;

public class HeroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero);

        //Hide de generic Action bar
        getSupportActionBar().hide();

        //Receive data
        final int heroIdKey = getIntent().getExtras().getInt("heroId");
        String heroNameKey = getIntent().getExtras().getString("heroName");
        String heroDescriptionKey = getIntent().getExtras().getString("heroDescription");
        String heroThumbnailKey = getIntent().getExtras().getString("heroThumbnail");

        //Initialize the views
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingtoolbar_id);
        collapsingToolbarLayout.setTitleEnabled(true);
        TextView heroName = findViewById(R.id.heroName);
        TextView heroDescription = findViewById(R.id.heroDescription);
        ImageView heroThumbnail = findViewById(R.id.heroThumbnail);

        //Load info into views
        collapsingToolbarLayout.setTitle(heroNameKey);
        heroName.setText(heroNameKey);
        heroDescription.setText(heroDescriptionKey);

        //Load image with Glide
        RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
        Glide.with(this).load(heroThumbnailKey).apply(requestOptions).into(heroThumbnail);

        //Configure the edit button
        Button btnEditHero = findViewById(R.id.btnEditHero);
        btnEditHero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(HeroActivity.this, "Mandar a editar", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HeroActivity.this, EditHeroActivity.class);
                intent.putExtra("heroId", getIntent().getExtras().getInt("heroId"));
                intent.putExtra("heroName", getIntent().getExtras().getString("heroName"));
                intent.putExtra("heroDescription", getIntent().getExtras().getString("heroDescription"));
                startActivity(intent);
            }
        });

        //Configure the delete button
        Button btnDeleteHero = findViewById(R.id.btnDeleteHero);
        btnDeleteHero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Alert the user to confirm the delete
                AlertDialog.Builder builder = new AlertDialog.Builder(HeroActivity.this);
                builder.setMessage("Do you want to delete?");
                builder.setPositiveButton(R.string.lblYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //Delete the hero
                        deleteHeroesOnDatabase(heroIdKey);
                        //Send to hero list
                        Intent intent = new Intent(HeroActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.lblNo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                //End alert confirm
            }
        });
        //End delete button
    }

    //Method to delete a hero
    private void deleteHeroesOnDatabase(int id) {
        cnxSQLite cnxSQLite = new cnxSQLite(HeroActivity.this);
        SQLiteDatabase database = cnxSQLite.getWritableDatabase();
        //Execute the query to DB
        database.delete(HeroesContract.HeroesColumns.TABLE_NAME,"id=" + id, null);
        cnxSQLite.close();
    }
}