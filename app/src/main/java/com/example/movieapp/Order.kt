package com.example.movieapp


class Order(
     var date: String?,
     var theater: String?,
     var adultTickets: Int,
     var childTickets: Int,
) {

    fun validateOrder() : Boolean {
        return !date.isNullOrEmpty() && !theater.isNullOrEmpty() && (adultTickets > 0 || childTickets > 0)
    }
}