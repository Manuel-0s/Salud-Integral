package com.example.healthtrack.domain.model

sealed class SaveMetricResult {
    data object Loading : SaveMetricResult()
    data class Success(
        val alertLevel: AlertLevel = AlertLevel.NONE,
        val diagnosticTitle: String = "",
        val diagnosticMessage: String = ""
    ) : SaveMetricResult()
    data class Error(
        val message: String,
        val field: MetricsField = MetricsField.NONE
    ) : SaveMetricResult()
}