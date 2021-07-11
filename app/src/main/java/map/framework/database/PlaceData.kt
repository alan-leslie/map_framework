package map.framework.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single table in the database. Each row is a separate instance of the Schedule class.
 * Each property corresponds to a column. Additionally, an ID is needed as a unique identifier for
 * each row in the database.
 */
@Entity
data class PlaceData(
    @PrimaryKey @NonNull @ColumnInfo(name = "name") val name: String,
    @NonNull @ColumnInfo(name = "latitude") val latitude: Float,
    @NonNull @ColumnInfo(name = "longitude") val longitude: Float,
    @NonNull @ColumnInfo(name = "address") val address: String,
    @NonNull @ColumnInfo(name = "rating") val rating: Float
)