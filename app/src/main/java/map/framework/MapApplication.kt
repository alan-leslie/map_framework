package map.framework

import android.app.Application
import map.framework.database.PlaceDatabase


public class MapApplication : Application() {
    val database: PlaceDatabase by lazy { PlaceDatabase.getInstance(this) }
}