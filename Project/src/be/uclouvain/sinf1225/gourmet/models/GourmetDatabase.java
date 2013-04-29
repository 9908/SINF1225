package be.uclouvain.sinf1225.gourmet.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import be.uclouvain.sinf1225.gourmet.Gourmet;
import be.uclouvain.sinf1225.gourmet.R;
import be.uclouvain.sinf1225.gourmet.utils.GourmetUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

/**
 * Layer between the models classes and the database.
 * ONLY models should access to this class!
 * NO verification will be done on rights!
 * @author guillaumederval
 */
class GourmetDatabase extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "gourmet";
    private Context context;
    
    /**
     * Initialize database
     * @param context Context which call the database
     */
	public GourmetDatabase(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	
	/**
	 * Initialize database
	 */
	public GourmetDatabase()
	{
		super(Gourmet.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
		this.context = Gourmet.getAppContext();
	}

	//TODO delete this
	@Override
	public void onOpen(SQLiteDatabase db)
	{
		onCreate(db);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql = GourmetUtils.readRawTextFile(this.context, R.raw.dbv1);
		StringTokenizer st = new StringTokenizer(sql, ";");
		while(st.hasMoreElements())
		{
			String t = st.nextToken();
			db.execSQL(t);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onCreate(db);
	}

	/* User */
	//TODO implement. Commented lines are not essential
	//Must handle the case if the User is a Restaurator!
	public void addUser(User user) {}
	public User getUser(String username) { return null; }
	//public List<User> getAllUsers() { return null; }
	public void updateUser(User user) {}
	//public void deleteUser(User user) {}

	
	/* Dish */
	//TODO implement. Commented lines are not essential
	public void addDish(Dish dish) {}
	public Dish getDish(int dishId) { return null;}
	//public List<Dish> getAllDishes() { return null; }
	public List<Dish> getDishInRestaurant(Restaurant restaurant) { return null; }
	public void updateDish(Dish dish) {}
	public void deleteDish(Dish dish) {}
	
	/* Restaurant */
	//TODO implement. Commented lines are not essential
	//public void addRestaurant(Restaurant restaurant) {}
	public Restaurant getRestaurant(int restoId) { return null; }
	//public List<Restaurant> getAllRestaurants() { return null; }
	public List<Restaurant> getRestaurantsInCity(City city) { return null; }
	public void updateRestaurant(Restaurant restaurant) { }
	//public void deleteRestaurant(Restaurant restaurant) {}
	
	/* Cities */
	
	/**
	 * Add a city to the database
	 * @param city
	 */
	public void addCity(City city)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put("name", city.getName());
	    values.put("country", city.getCountry());
	    values.put("longitude", city.getLocation().getLongitude());
	    values.put("latitude", city.getLocation().getLatitude());
	    db.insert("city", null, values);
	    
	    db.close(); // Closing database connection
	}
	
	/**
	 * Get a city from the database
	 * @param name Name of the city
	 * @param country Country of the city
	 * @return a City object if object exists, null else
	 */
	public City getCity(String name, String country)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query("city", //table to select on
				new String[]{"name","country","longitude","latitude"}, //column to get
				"`name` = ? AND `country` = ?", //where clause, ?s will be replaced by...
				new String[]{name,country}, //... these values
				null, //group by
				null, //having
				null); //orderby
		if(cursor == null)
			return null;
		
		cursor.moveToFirst();
		
		Location loc = new Location("Database");
		loc.setLongitude(cursor.getDouble(2));
		loc.setLatitude(cursor.getDouble(3));
		
		List<Integer> restaurantsID = new ArrayList<Integer>();
		cursor = db.query(true, "restaurant", new String[]{"restoId"}, "`cityName` = ? AND `cityCountry` = ?", new String[]{name, country}, null, null, null,null);
		if(cursor != null)
		{
			cursor.moveToFirst();
			for(int i = 0; i < cursor.getCount(); i++)
			{
				restaurantsID.add(cursor.getInt(0));
				cursor.moveToNext();
			}
		}
		
		return new City(name,country,loc, restaurantsID);
	}
	
	/**
	 * Return all cities available in database
	 * @return all cities available in database
	 */
	public List<City> getAllCities()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query("city", //table to select on
				new String[]{"name","country","longitude","latitude"}, //column to get
				null, //where
				null, //where string
				null, //group by
				null, //having
				null); //orderby
		if(cursor == null)
			return null;
		cursor.moveToFirst();
		
		List<City> cities = new ArrayList<City>();
		for(int j = 0; j < cursor.getCount(); j++)
		{
			Location loc = new Location("Database");
			loc.setLongitude(cursor.getDouble(2));
			loc.setLatitude(cursor.getDouble(3));
		
			List<Integer> restaurantsID = new ArrayList<Integer>();
			Cursor restos = db.query(true, "restaurant", new String[]{"restoId"}, "`cityName` = ? AND `cityCountry` = ?", new String[]{cursor.getString(0),cursor.getString(1)}, null, null, null,null);
			if(restos != null)
			{
				restos.moveToFirst();
				for(int i = 0; i < restos.getCount(); i++)
				{
					restaurantsID.add(restos.getInt(0));
					restos.moveToNext();
				}
			}
		
			cities.add(new City(cursor.getString(0),cursor.getString(1),loc, restaurantsID));
			cursor.moveToNext();
		}
		return cities;
	}
	
	/**
	 * Return number of cities available in the database
	 * @return number of cities available in the database
	 */
	public int getCitiesCount()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query("city", //table to select on
				new String[]{"name","country"}, //column to get
				null, //where
				null, //where string
				null, //group by
				null, //having
				null); //orderby
		if(cursor == null)
			return 0;
		return cursor.getCount();
	}
	
	/**
	 * Update city 
	 * @param city
	 * @return number of row updated
	 */
	public int updateCity(City city)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put("name", city.getName());
	    values.put("country", city.getCountry());
	    values.put("longitude", city.getLocation().getLongitude());
	    values.put("latitude", city.getLocation().getLatitude());
	    db.insert("city", null, values);
	    
	    return db.update("city", values, "`name` = ? AND `country` = ?", new String[] {city.getName(), city.getCountry()});
	}
	
	/**
	 * Delete a city
	 * @param city
	 */
	public void deleteCity(City city)
	{
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete("city", "`name` = ? AND `country` = ?", new String[] {city.getName(), city.getCountry()});
	    db.close();
	}
	
	/** 
	 * Add a reservation in the database
	 * @param reservation
	 */
	public void addReservation(Reservation reservation)
	{
		// ouverture de la base de donn�es
		SQLiteDatabase db = this.getWritableDatabase();
	    ContentValues values = new ContentValues();
	    
	    // valeur des diff�rents champs
	    values.put("user", reservation.getUser());
	    values.put("resto", reservation.getRestaurant());
	    values.put("nbrReservation", reservation.getNbrReservation());
	    values.put("date", reservation.getDate());
	    
	    // insertion dans la base de donn�es
	    db.insert("reservation", null, values);
	    
	    // fermeture de la base de donn�es
	    db.close();
	    
	    // resvId de la r�servation
	    SQLiteDatabase db = this.getReadableDatabase();
	    String str[] = new string[] {(reservation.getUser()).getName(), (reservation.getRestaurant()).getName(), reservation.getDate()};
	    Cursor cursor = db.rawQuery("select resvId from reservation where user = ? AND resto = ? AND date = ?", str);
	    if(Cursor == null || cursor.getCount() != 1) return null;
	    cursor.moveToFirst();
	    int RESVID = cursor.getInteger(0);
	    db.close();
	    
	    // insertion des diff�rents plats command�s dans la base de donn�es
	    SQLiteDatabase db = this.getWritableDatabase();
	    ContentValues values = new ContentValues();
	    for(dishNode node : reservation.dishes)
	    {
	    	values.put("nameDish", node.dish.name);
	    	values.put("nbrDish", node.nbrDishes);
	    	values.put("resvId", RESVID);
	    	db.insert("reservationDish", null, values);
	    }
	    db.close();	    
	}
	/**
	 * Supprime la r�servation correspondant au triplet (user,resto,date) dans la table r�servation
	 * et surpprime tt les plats command�s dans la table reservationDish
	 */
	
	public void deleteReservation(User user, Restaurant resto, Date date)
	{
	    // resvId de la r�servation
	    SQLiteDatabase db = this.getReadableDatabase();
	    String str[] = new string[] {user.getName(), resto.getName(), date};
	    Cursor cursor = db.rawQuery("select resvId from reservation where user = ? AND resto = ? AND date = ?", str);
	    if(Cursor == null || cursor.getCount() != 1) return null;
	    cursor.moveToFirst();
	    int RESVID = cursor.getInteger(0);
	    db.close();
	    
	    // suppression des plats r�serv�s dans la table reservationDish
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete("reservationDish", "`resvId` = ?", new String[] {RESVID});
	    
	    // suppression de la r�servation dans la table reservation
	    db.delete("reservation","`resvId`= ?", new string[] {RESVID});
	    db.close();
	}
	
	// retourner ttes lesr�servtaion d'un m�me user => database
	// retourner ttes les users ayant effectu�s une r�servation dans un restaurant => database
	// retourner toutes les r�servation d'un m�me plat => database
	
	/* Reservation */
	//TODO implement. Commented lines are not essential
	public Reservation getReservation(User user, Restaurant restaurant, Date date) {return null;}
	//public List<Reservation> getAllReservations() { return null; }
	public List<Reservation> getReservationInRestaurant(Restaurant restaurant) { return null; }
	public List<Reservation> getReservationByUser(User user) { return null; }
	public void updateReservation(Reservation reservation) {}	
}
