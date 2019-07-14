package com.jinnycho503.spark

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import scala.io.Source
import java.nio.charset.CodingErrorAction
import scala.io.Codec
import scala.math.sqrt
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.Pipeline

object SafecastClustering {

  // Case class for a column name for lat, lon, value (radioactivity)
  final case class SafecastR(lat: Double, lon: Double, value: Double)

  /*
   * Filter the given data
   * Create a dataframe of latitude, longitude, value
   */
  def cleanData(safecastDF: DataFrame) : DataFrame = {
    // 1. filter unnecessary columns
    val columnFilterlist = List("captured_at", "unit", "location_name", "device_id", "id", "user_id", "original_id", "measurement_import_id", "height", "devicetype_id", "sensor_id", "station_id", "channel_id")
    var columnFilteredDF = safecastDF
    for (col <- columnFilterlist) {
      columnFilteredDF = columnFilteredDF.drop(col)
    }
    // 2. filter null values
    var nullFilteredDF = columnFilteredDF.na.drop()
    return nullFilteredDF
  }

  /*
   * Apply K-Means Clustering to the given data
   *
   */
  def cluster(safecastDF: DataFrame) = {
    val assembler = new VectorAssembler().setInputCols(Array("latitude","longitude")).setOutputCol("features")
    val kmeans = new KMeans().setK(2).setFeaturesCol("features").setPredictionCol("prediction")
    val pipeline = new Pipeline().setStages(Array(assembler, kmeans))
    val kMeansPredictionModel = pipeline.fit(safecastDF)
    val predictionResult = kMeansPredictionModel.transform(safecastDF)
    println("Cluster Centers: ")
    predictionResult.show()
  }

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
      .option("inferSchema", "true")
      .option("charset", "UTF8")
      .load("/Users/jinnycho/Downloads/mini-measurements.csv")

    val filteredDF = cleanData(safecastDF)
    filteredDF.show()

    //cluster(stringFilteredDF)
    spark.stop()
  }
}
