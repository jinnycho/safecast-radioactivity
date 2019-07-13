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
  final case class SafecastR(lat: Int, lon: Int, value: Int)

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

    val data = spark.sparkContext.textFile("/Users/jinnycho/Downloads/mini-measurements.csv")
    // extract header
    val header = data.first()
    // filter out header
    val filtered_data = data.filter(row => row != header)
    // Read in each safecast estimate and extract lat/lon/value
    val lines = filtered_data.map(x => SafecastR(x.split(',')(1).toInt, x.split(',')(2).toInt, x.split(',')(3).toInt))

    // Convert to a Dataset
    import spark.implicits._
    val safecastRDS = lines.toDS()

    safecastRDS.show()

    spark.stop()
  }
}
