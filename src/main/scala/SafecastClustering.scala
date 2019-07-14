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

import java.io.File
import java.io.PrintWriter

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
   * to create clusterings
   */
  def getCluster(safecastDF: DataFrame, numClusters: Int) : DataFrame = {
    val assembler = new VectorAssembler().setInputCols(Array("latitude","longitude")).setOutputCol("features")
    val kmeans = new KMeans().setK(numClusters).setFeaturesCol("features").setPredictionCol("prediction")
    val pipeline = new Pipeline().setStages(Array(assembler, kmeans))
    val kMeansPredictionModel = pipeline.fit(safecastDF)
    val predictionResult = kMeansPredictionModel.transform(safecastDF)
    return predictionResult
  }


  /*
   * Summarize K-Means clustering result
   * Get average radioactivity value in cluster
   */
  def summarizeCluster(kMeansDF: DataFrame) : DataFrame = {
    // list of average radioactivity value in cluster
    // [clusterNum: avgValue]
    val avgValues = kMeansDF.groupBy("prediction").avg("value")
    val avgLats = kMeansDF.groupBy("prediction").avg("latitude")
    val avgLons = kMeansDF.groupBy("prediction").avg("longitude")
    val avgLatsLons = avgLats.join(avgLons, "prediction")
    val avgLatsLonsVals = avgLatsLons.join(avgValues, "prediction")
    return avgLatsLonsVals
  }


  /*
   * convert csv to geojson
   */
  def setGeojson(lat: String, lon: String, value: String) : String = {
    val geoStr = s"""{"type": "Feature", "geometry": {"type": "Point", "coordinates": [$lon, $lat] }, "properties": { "value": $value }}"""
    return geoStr
  }


  /*
   * convert csv to geojson
   */
  def convertToGeojson(clusterSummary: DataFrame) : String = {
    var finalGeoStr = """{"type": "FeatureCollection", "features": ["""
    for (row <- clusterSummary.rdd.collect) {
      val lat = row.mkString(",").split(",")(1)
      val lon = row.mkString(",").split(",")(2)
      val value = row.mkString(",").split(",")(3)
      val geoStr = setGeojson(lat, lon, value)
      finalGeoStr = finalGeoStr.concat(geoStr + ",")
    }
    finalGeoStr = finalGeoStr.dropRight(1)
    finalGeoStr = finalGeoStr.concat("]}")
    return finalGeoStr
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
      .load("/Users/jinnycho/Downloads/measurements-out.csv")

    val filteredDF = cleanData(safecastDF)
    //filteredDF.show()

    val predictionResultDF = getCluster(filteredDF, 80000)
    //predictionResult.show()

    val clusterSummaryDF = summarizeCluster(predictionResultDF)

    val geoJsonStr = convertToGeojson(clusterSummaryDF)

    // Write resulting geojson to a file
    val writer = new PrintWriter(new File("clusters.geojson"))
    writer.write(geoJsonStr)
    writer.close()

    spark.stop()
  }
}
