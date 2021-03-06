package twarzo.sunshine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by quikr on 25/6/15.
 */
    /**
     * Manages a local database for weather data.
     */
    public class WeatherDbHelper extends SQLiteOpenHelper {

        // If you change the database schema, you must increment the database version.
        private static final int DATABASE_VERSION = 5;

        public static final String DATABASE_NAME = "weather.db";

        public WeatherDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            /**
             * TODO YOUR CODE BELOW HERE FOR QUIZ
             * QUIZ - 4a - LocationEntry
             * https://www.udacity.com/course/viewer#!/c-ud853/l-1639338560/e-1633698599/m-1633698600
             **/

            final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "+ WeatherContract.LocationEntry.TABLE_NAME+"("+
                    WeatherContract.LocationEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
                    WeatherContract.LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                    WeatherContract.LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, "+
                    WeatherContract.LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL " +
                    ");";

        //TODO Uncomment for
         //4a - Create a Database for SQLiteOpenHelper
         //https://www.udacity.com/course/viewer#!/c-ud853/l-1639338560/m-1633698598
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // the ID of the location entry associated with this weather data
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_DATETEXT + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                WeatherContract.LocationEntry.TABLE_NAME + " (" + WeatherContract.LocationEntry._ID + "), " +
                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATETEXT + ", " +
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //TODO Uncomment for
        //4a - SQLiteOpenHelper onUpgrade() method
       // https://www.udacity.com/course/viewer#!/c-ud853/l-1639338560/m-1633698602
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

        }
    }

