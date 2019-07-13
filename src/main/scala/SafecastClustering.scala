package com.sundogsoftware.spark

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import scala.io.Source
import java.nio.charset.CodingErrorAction
import scala.io.Codec
import scala.math.sqrt
import org.apache.spark.sql._
import org.apache.spark.sql.functions._


object SafecastClustering {

  // Case class for a column name for lat, lon, value (radioactivity)
  final case class SafecastR(lat: Double, lon: Double, value: Double)

  /** Our main function where the action happens */
  def main(args: Array[String]) {

    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Use new SparkSession interface in Spark 2.0
    val spark= SparkSession
      .builder
      .appName("SafecastClustering")
      .master("local[*]")
      .getOrCreate()

    val safecast_DF = spark.read
      .format("csv")
      .option("header", "true") // filter header
      .option("charset", "UTF8")
      .load("/Users/jinnycho/Downloads/mini-measurements.csv")

    // filter unnecessary columns
    val column_filterlist = List("captured_at", "unit", "location_name", "device_id", "id", "user_id", "original_id", "measurement_import_id", "height", "devicetype_id", "sensor_id", "station_id", "channel_id")
    var column_filtered_DF = safecast_DF
    for (col <- column_filterlist) {
        column_filtered_DF = column_filtered_DF.drop(col)
    }
    // filter null values
    var null_filtered_DF = column_filtered_DF.na.drop()
    // filter if it's not numeric
    var string_filter_list = List("latitude", "longitude", "value")
    var string_filtered_DF = null_filtered_DF
    for (col <- string_filter_list) {
        string_filtered_DF = string_filtered_DF.filter(row => row.getAs[String](col).matches("""^\d{1,}\.*\d*$"""))
    }
    string_filtered_DF.show()

    spark.stop()
  }
}
