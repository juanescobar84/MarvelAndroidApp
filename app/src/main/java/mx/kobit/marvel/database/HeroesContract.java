package mx.kobit.marvel.database;

public class HeroesContract {

    public class HeroesColumns{
        public static final String TABLE_NAME = "heroes";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String THUMBNAIL = "thumbnail";

        public static final int ID_COLUMN_INDEX = 0;
        public static final int NAME_COLUMN_INDEX = 1;
        public static final int DESCRIPTION_COLUMN_INDEX = 2;
        public static final int THUMBNAIL_COLUMN_INDEX = 3;
    }
}
