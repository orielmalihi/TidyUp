package com.example.choreapp.data

import com.example.choreapp.domain.model.Comment

object DefaultComments {
    fun getGoodJobComments(): List<Comment> = listOf(
        Comment(
            textEn = "Good job!",
            textHe = "עבודה נהדרת!",
            category = "good_job"
        ),
        Comment(
            textEn = "Awesome work!",
            textHe = "מדהים!",
            category = "good_job"
        ),
        Comment(
            textEn = "Keep it up!",
            textHe = "תמשיך ככה!",
            category = "good_job"
        ),
        Comment(
            textEn = "Fantastic!",
            textHe = "פנטסטי!",
            category = "good_job"
        ),
        Comment(
            textEn = "You're a star!",
            textHe = "אתה כוכב!",
            category = "good_job"
        ),
        Comment(
            textEn = "Amazing!",
            textHe = "מעולה!",
            category = "good_job"
        ),
        Comment(
            textEn = "Excellent work!",
            textHe = "עבודה מעולה!",
            category = "good_job"
        ),
        Comment(
            textEn = "You rock!",
            textHe = "אתה בטירוף!",
            category = "good_job"
        ),
        Comment(
            textEn = "Super job!",
            textHe = "עבודה סופר!",
            category = "good_job"
        ),
        Comment(
            textEn = "Well done!",
            textHe = "יפה מאוד!",
            category = "good_job"
        )
    )
}
