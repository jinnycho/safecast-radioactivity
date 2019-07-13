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

    val safecastDF = spark.read
      .format("csv")
      .option("header", "true") // filter header
      .option("charset", "UTF8")
      .load("/Users/jinnycho/Downloads/mini-measurements.csv")

    // filter unnecessary columns
    val filterList = List("captured_at", "unit", "location_name", "device_id", "id", "user_id", "original_id", "measurement_import_id", "height", "devicetype_id", "sensor_id", "station_id", "channel_id")
    var filteredDF = safecastDF
    for (col <- filterList) {
        filteredDF = filteredDF.drop(col)
    }

    filteredDF.show()

    spark.stop()
  }
}
