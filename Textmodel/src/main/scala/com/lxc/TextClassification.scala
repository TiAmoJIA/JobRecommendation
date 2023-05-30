package com.lxc

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.spark.SparkConf
import org.apache.spark.ml.feature.{Word2Vec, Word2VecModel}
import org.apache.spark.ml.feature.RegexTokenizer
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession, functions}
import org.apache.spark.ml.feature.Normalizer
import org.apache.spark.ml.feature.BucketedRandomProjectionLSH
import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.seg.common.Term
import org.apache.log4j.{Level, Logger}


object TextClassification {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    //  def getTextClassification(inputText: String) {
    val userIdStr = args(0)
    val userId = userIdStr.toInt

//    val inputText = "我是一个常州大学大数据专业的大四学生，我希望能在苏州或南通等地区找一份月薪5-7k的工作，比如Java工程师、数据工程师、宠物美容等工作，我的项目经历是：1.足彩网站数据爬取与分析2. 基于社会网络好友推荐3.基于大数据的淘宝用户行为分析。"
    val conf = new SparkConf()
      .set("spark.some.config.option", "some-value")
      .setMaster("local")
    val spark = SparkSession
      .builder()
      .config(conf)
      //      .enableHiveSupport()
      .appName("text_similar")
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
    val df: DataFrame = spark.read.jdbc(jdbcUrl, "job", connectionProperties)
    //用户信息
    val userDf:DataFrame = spark.read.jdbc(jdbcUrl, "user", connectionProperties)
    val userInfo = userDf.filter($"userId"===userId)
    userInfo.show()
    val info=userInfo.first()
    val inputText="我是一名"+info.getAs[String]("school")+"的"+info.getAs[String]("major")+"专业的毕业生，最高学历为"+info.getAs[String]("degree")+"，来自"+info.getAs[String]("hometown")+",我希望能在"+info.getAs[String]("desiredCity")+"找到一份月薪大概"+info.getAs[String]("salary")+"的工作，如："+info.getAs[String]("desiredJob")+",技能是"+info.getAs[String]("skills")+"，兴趣爱好是"+info.getAs[String]("hobby")+",项目经历为："+info.getAs[String]("projectExperience")
    print(inputText)
    val updateDf = df.withColumn("jobLabels", regexp_replace(col("jobLabels"), "[\\[\\]']", ""))
      .withColumn("skills", regexp_replace(col("skills"), "[\\[\\]']", ""))
      .withColumn("welfareList", regexp_replace(col("welfareList"), "[\\[\\]']", ""))
    val sentenceDf = updateDf.select("jobId", "jobName", "salaryDesc", "jobLabels", "skills", "jobExperience", "jobDegree", "cityName", "areaDistrict", "businessDistrict", "brandName", "brandStageName", "brandIndustry", "brandScaleName", "welfareList")
    val sentence = sentenceDf.withColumn("text", concat_ws(",", col("jobName"), col("salaryDesc"), col("jobLabels"), col("skills"), col("jobExperience"), col("jobDegree"), col("cityName"), col("areaDistrict"), col("businessDistrict"), col("brandName"), col("brandStageName"), col("brandIndustry"), col("brandScaleName"), col("welfareList")))
      .select("jobId", "text")
//    sentence.show(10);
    val textDF=sentence
    // 定义分词函数
    val tokenize = functions.udf { sentence: String =>
      HanLP.segment(sentence)
        .toArray()
        .map(_.toString)
        .toSeq
    }
    val tokenizedDF = textDF.withColumn("words", tokenize(functions.col("text")))

    val word2Vec = new Word2Vec()
      .setInputCol("words")
      .setOutputCol("vectors")
      .setVectorSize(100)
    val model = word2Vec.fit(tokenizedDF)
    val vectorizedDF = model.transform(tokenizedDF)
    val normalizer = new Normalizer()
      .setInputCol("vectors")
      .setOutputCol("normalizedVectors")
      .setP(2.0)
    val normalizedDF = normalizer.transform(vectorizedDF)
    normalizedDF.show(10)
    // Configure LSH algorithm
    val brp = new BucketedRandomProjectionLSH()
      .setInputCol("normalizedVectors")
      .setOutputCol("hashes")
      .setNumHashTables(3)
      .setBucketLength(0.1)

    // Fit the LSH model on the normalized vectors
    val lshModel = brp.fit(normalizedDF)

    // Find the most similar texts to the input text
    //    val inputText = "your input text"
    val inputDF = Seq((inputText)).toDF("text")
    val tokenizedInputDF = inputDF.withColumn("words", tokenize(functions.col("text")))
    val vectorizedInputDF = model.transform(tokenizedInputDF)
    val normalizedInputDF = normalizer.transform(vectorizedInputDF)
    val hashInputDF = lshModel.transform(normalizedInputDF)

    val similarTexts = lshModel.approxSimilarityJoin(normalizedDF, hashInputDF, 10, "distance")
//      .filter(col("datasetA.text") =!= inputText)
      .select(col("datasetA.jobId"),col("datasetA.text"), col("distance"))
      .orderBy(col("distance"))
      .limit(30)
    similarTexts.show(30)
    val jobIdsList = similarTexts.select(collect_list("jobId").alias("jobIds")).limit(30).head().getAs[Seq[Int]](0)
    val jobIdString = jobIdsList.mkString("[", ",", "]")
    print(jobIdString)
    val connection: Connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
    val query = "UPDATE user SET textRecommendation = ? WHERE userId = ?"
    val statement: PreparedStatement = connection.prepareStatement(query)
    statement.setString(1,jobIdString)
    statement.setInt(2,userId)
    statement.executeUpdate()
    statement.close()
    connection.close()
    spark.stop()

  }

}