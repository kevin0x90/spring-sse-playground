package com.dbi15.einsendeaufgabe.repository

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class IpAddressRepository(private val mongoTemplate: MongoTemplate) {

    companion object {
        const val COLLECTION_NAME = "ipAddresses"

        val last10IpsSearchQuery = Query()
                .limit(10)
                .with(Sort.by(Sort.Direction.DESC, "lastModified"))

        val ipExistsQuery = { ip: String -> Query(Criteria.where("ip").`is`(ip)) }
    }

    fun getLast10Ips(): List<BasicDBObject> {
        return mongoTemplate.find(last10IpsSearchQuery, BasicDBObject::class.java, COLLECTION_NAME)
    }

    fun addIpIfNotExists(ip: String): Boolean {
        if (mongoTemplate.exists(ipExistsQuery(ip), COLLECTION_NAME)) {
            return false
        }

        mongoTemplate.save(BasicDBObjectBuilder()
                .add("ip", ip)
                .add("lastModified", LocalDateTime.now())
                .get(), COLLECTION_NAME)

        return true
    }
}