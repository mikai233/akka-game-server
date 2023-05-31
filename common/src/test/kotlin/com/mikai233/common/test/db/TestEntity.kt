package com.mikai233.common.test.db

import com.mikai233.common.entity.FieldTrackableEntity
import org.springframework.data.annotation.Id

data class TestEntity(
    @Id val id: Int,
    var name: String,
    var age: Int
) : FieldTrackableEntity<Int> {
    override fun key(): Int {
        return id
    }
}