/*
 * Copyright (C) 2016 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.qaware.chronix.spark.api.java

import de.qaware.chronix.spark.api.java.spark.api.java.ChronixRDD
import de.qaware.chronix.spark.api.java.spark.api.java.ChronixSparkContext
import de.qaware.chronix.timeseries.MetricTimeSeries
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.common.SolrDocument
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext
import org.junit.Assert
import spock.lang.Ignore
import spock.lang.Specification

class TestChronixSparkContext extends Specification {

    @Ignore
    def "testSolrQuery"() {
        given:
        SparkConf conf = new SparkConf().setMaster(TestConfiguration.SPARK_MASTER).setAppName(TestConfiguration.APP_NAME)
        JavaSparkContext sc = new JavaSparkContext(conf)
        ChronixSparkContext csc = new ChronixSparkContext(sc)
        SolrQuery query = new SolrQuery("logMessage:*570*")
        when:
        JavaRDD<SolrDocument> result = csc.querySolr(query, TestConfiguration.ZK_HOST)
        then:
        List<SolrDocument> docs = result.take(1)
        docs.size() == 1
    }


    def "testChronixQuery"() {
        SparkConf conf = new SparkConf().setMaster(TestConfiguration.SPARK_MASTER).setAppName(TestConfiguration.APP_NAME)
        JavaSparkContext sc = new JavaSparkContext(conf)
        ChronixSparkContext csc = new ChronixSparkContext(sc);
        SolrQuery query = new SolrQuery("metric:\"MXBean(java.lang:type=Memory).NonHeapMemoryUsage.used\" AND type:RECORD")
        ChronixRDD result = csc.queryChronix(query, TestConfiguration.ZK_HOST)
        List<MetricTimeSeries> timeSeries = result.take(5)
        Assert.assertTrue(timeSeries.size() == 5)
        for (MetricTimeSeries ts : timeSeries) {
            System.out.println(ts.toString());
        }
    }
}