package mx.kobit.marvel.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mx.kobit.marvel.R;
import mx.kobit.marvel.database.HeroesContract;
import mx.kobit.marvel.database.cnxSQLite;
import mx.kobit.marvel.model.Hero;

public class EditHeroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hero);

        //Receive data
        final int heroIdKey = getIntent().getExtras().getInt("heroId");
        String heroNameKey = getIntent().getExtras().getString("heroName");
        String heroDescriptionKey = getIntent().getExtras().getString("heroDescription");

        //Initialize the views
        final TextView heroName = findViewById(R.id.txtName);
        final TextView heroDescription = findViewById(R.id.txtDescription);

        //Load info into views
        heroName.setText(heroNameKey);
        heroDescription.setText(heroDescriptionKey);

        //Configure the edit button
        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validate the fields
                if(heroName.getText().toString().trim().isEmpty()){
                    Toast.makeText(EditHeroActivity.this, R.string.validateEmptyName, Toast.LENGTH_SHORT).show();
                    return;
                }
                //Edit the hero
                editHeroesOnDatabase(heroIdKey, heroName.getText().toString(), heroDescription.getText().toString());
                //Send to hero list
                Intent intent = new Intent(EditHeroActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    //Method to edit a hero
    private void editHeroesOnDatabase(int id, String name, String description) {
        cnxSQLite cnxSQLite = new cnxSQLite(EditHeroActivity.this);
        SQLiteDatabase database = cnxSQLite.getWritableDatabase();
        //Set the values for the query
        ContentValues contentValues = new ContentValues();
        contentValues.put(HeroesContract.HeroesColumns.ID, id);
        contentValues.put(HeroesContract.HeroesColumns.NAME, name);
        contentValues.put(HeroesContract.HeroesColumns.DESCRIPTION, description);
        //Execute the query to DB
        database.update(HeroesContract.HeroesColumns.TABLE_NAME, contentValues,"id=" + id, null);
        cnxSQLite.close();
    }
}
