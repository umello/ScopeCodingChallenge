package com.sandbox.scopecodingchallenge.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Owner (
	val name : String,
	val surname : String,
	val foto : String
) {
	@PrimaryKey(autoGenerate = true)
	var uuid: Long = 0
	var userId: Long = 0
}