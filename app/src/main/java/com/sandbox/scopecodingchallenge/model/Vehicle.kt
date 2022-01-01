package com.sandbox.scopecodingchallenge.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vehicle (
	@PrimaryKey
	val vehicleid : Long,
	val make : String,
	val model : String,
	val year : Int,
	val color : String,
	val vin : String,
	val foto : String
) {
	var ownerId: Long = 0
}