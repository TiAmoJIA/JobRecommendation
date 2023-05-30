package com.lxc

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions._

object ALSSpark {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

    Class.forName("com.mysql.cj.jdbc.Driver")
    val conf = new SparkConf()
      .set("spark.some.config.option", "some-value")
      .setMaster("local")

    val spark = SparkSession
      .builder()
      .config(conf)
      //      .enableHiveSupport()
      .appName("als_rec")
      //.master("local[2]") //单机版配置，集群情况下，可以去掉
      .getOrCreate()
    import spark.implicits._
    val jdbcHostname = "localhost"
    val jdbcPort = 3306
    val jdbcDatabase = "jobs"
    val jdbcUsername = "root"
    val jdbcPassword = "123"

    val jdbcUrl = s"jdbc:mysql://${jdbcHostname}:${jdbcPort}/${jdbcDatabase}"

    val connectionProperties = new java.util.Properties()

    connectionProperties.put("user", jdbcUsername)
    connectionProperties.put("password", jdbcPassword)
    connectionProperties.put("driver", "com.mysql.cj.jdbc.Driver")
    var jobDf: DataFrame = spark.read.jdbc(jdbcUrl, "job", connectionProperties)
    var userDf: DataFrame = spark.read.jdbc(jdbcUrl, "user", connectionProperties)
    userDf = userDf.select("userId", "preference")
    jobDf = jobDf.select("jobId", "jobName")

    //  val splitString = udf((s: String) => s.split(","))
    // 将DataFrame中的字符串列转换为整数数组列
    userDf = userDf.withColumn("preference", regexp_replace(col("preference"), "[\\[\\]']", ""))
      .withColumn("preferenceList", split(col("preference"), ","))
      .withColumn("preferenceList", expr("transform(preferenceList, x -> cast(x as int))"))
      .select("userId", "preferenceList")
    userDf.show(10)
    jobDf.show(10)
    //    print($"jobDf.jobId")
    // Generate the rating dataframe
    val ratingDf = generateRatingDf(spark, userDf, jobDf)
    ratingDf.show(10)
//    val Array(training, test) = ratingDf.randomSplit(Array(0.8, 0.2))
    val als = new ALS()
      .setRank(10)
      .setMaxIter(10)
      .setRegParam(0.01)
      .setUserCol("userId")
      .setItemCol("jobId")
      .setRatingCol("rating")
    val model = als.fit(ratingDf)
    //    val recommendationDf= model.recommendForAllUsers(4)
//    val predictions = model.transform(test)
//    val evaluator = new RegressionEvaluator()
//      .setMetricName("rmse")
//      .setLabelCol("rating")
//      .setPredictionCol("prediction")
//    val rmse = evaluator.evaluate(predictions)
//    println(s"均方根误差 = $rmse")
//    predictions.show(100)
    val recommendation = model.recommendForAllUsers(20)
    recommendation.show(20)
    val explodedDf = recommendation.select($"userId", explode($"recommendations").alias("rec"))
    val jobidDf = explodedDf.select($"userId", $"rec.jobId".alias("jobId"))
      .groupBy($"userId")
      .agg(collect_list($"jobId").alias("jobIds"))
      .withColumn("jobIdsStr", concat_ws(",", $"jobIds"))
      .select($"userId", $"jobIdsStr")
    jobidDf.show()
    val userInfo = jobidDf.withColumnRenamed("jobIdsStr", "alsRecommendation")
      .filter(col("alsRecommendation").isNotNull)
      .select("userId", "alsRecommendation")

    userInfo.repartition(100).rdd.foreachPartition { rows =>
      val conn = java.sql.DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
      val stmt = conn.prepareStatement("UPDATE user SET alsRecommendation = ? WHERE userId = ?")
      rows.foreach { row =>
        stmt.setString(1, row.getAs[String]("alsRecommendation"))
        stmt.setInt(2, row.getAs[Int]("userId"))
        stmt.executeUpdate()
      }
      stmt.close()
      conn.close()
    }
    spark.close()

    //    model.save("")
    //    val accuracy = predictions.filter($"rating" === $"prediction").count() / predictions.count()
    //    println(s"准确率 = $accuracy")
    //
    //    val recall = predictions.filter($"rating" === $"prediction" && $"rating" > 0).count() /
    //      predictions.filter($"rating" > 0).count()
    //    println(s"召回率 = $recall")
    //
    //    val f1 = 2 * accuracy * recall / (accuracy + recall)
    //    println(s"F1 score = $f1")

  }

  def generateRatingDf(spark: SparkSession, userDf: DataFrame, jobDf: DataFrame): DataFrame = {
    import spark.implicits._

    // Generate a dataframe with userId, jobId, and corresponding rating
    val userJobPairs = userDf.as("u")
      .join(jobDf.as("j"), array_contains($"u.preferenceList", $"j.jobId"))
      .withColumn("rating", lit(1)) // Compute the rating based on your logic)

    // Convert the dataframe columns to the correct data types
    val ratingDf = userJobPairs.select(
      $"userId".cast("integer"),
      $"jobId".cast("integer"),
      $"rating".cast("integer")
    )

    ratingDf
  }
}
