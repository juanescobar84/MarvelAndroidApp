package mx.kobit.marvel.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class cnxSQLite extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "marvel.db";

    public cnxSQLite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String HEROES_DATABASE = "CREATE TABLE " + HeroesContract.HeroesColumns.TABLE_NAME + " (" +
                HeroesContract.HeroesColumns.ID + " INTEGER PRIMARY KEY," +
                HeroesContract.HeroesColumns.NAME + " TEXT NOT NULL," +
                HeroesContract.HeroesColumns.DESCRIPTION + " TEXT,"+
                HeroesContract.HeroesColumns.THUMBNAIL + " TEXT)";
        sqLiteDatabase.execSQL(HEROES_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int versionOld, int versionNew) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXIST " + DATABASE_NAME);
        onCreate(sqLiteDatabase);
    }
}
