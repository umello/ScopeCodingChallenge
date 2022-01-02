package com.sandbox.scopecodingchallenge.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData (
	@PrimaryKey
	val userid : Long?,
	var owner : Owner?,
	var vehicles : List<Vehicle>
)