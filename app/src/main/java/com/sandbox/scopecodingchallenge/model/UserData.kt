package com.sandbox.scopecodingchallenge.model

data class UserData (
	val userid : Long,
	val owner : Owner,
	val vehicles : List<Vehicle>
)