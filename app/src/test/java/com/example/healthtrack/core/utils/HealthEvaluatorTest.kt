package com.example.healthtrack.core.utils

import com.example.healthtrack.domain.model.AlertLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class HealthEvaluatorTest {

    // =========================================================================
    // PRUEBAS GLUCOSA
    // =========================================================================

    @Test
    fun evaluateGlucose_whenNormal_returnsNone() {
        val result = HealthEvaluator.evaluateGlucose(value = 95.0, maxGlucose = 140)
        assertEquals(AlertLevel.NONE, result)
    }

    @Test
    fun evaluateGlucose_whenThresholdIsZero_usesDefaultValue() {
        // Con umbral 0 (no configurado), se usa DEFAULT_MAX_GLUCOSE = 140
        val resultNormal = HealthEvaluator.evaluateGlucose(value = 100.0, maxGlucose = 0)
        assertEquals(AlertLevel.NONE, resultNormal)

        val resultWarning = HealthEvaluator.evaluateGlucose(value = 145.0, maxGlucose = 0)
        assertEquals(AlertLevel.WARNING, resultWarning)
    }

    @Test
    fun evaluateGlucose_whenThresholdIsNull_usesDefaultValue() {
        val resultWarning = HealthEvaluator.evaluateGlucose(value = 145.0, maxGlucose = null)
        assertEquals(AlertLevel.WARNING, resultWarning)
    }

    @Test
    fun evaluateGlucose_whenExceedsUserThreshold_returnsWarning() {
        // Usuario configuró umbral máximo de 120
        val result = HealthEvaluator.evaluateGlucose(value = 130.0, maxGlucose = 120)
        assertEquals(AlertLevel.WARNING, result)
    }

    @Test
    fun evaluateGlucose_whenLowWarning_returnsWarning() {
        // Glucosa entre 54.0 y 70.0 es advertencia de hipoglucemia leve/moderada
        val result = HealthEvaluator.evaluateGlucose(value = 65.0, maxGlucose = 140)
        assertEquals(AlertLevel.WARNING, result)
    }

    @Test
    fun evaluateGlucose_whenCriticalHigh_returnsCritical() {
        val result = HealthEvaluator.evaluateGlucose(value = 310.0, maxGlucose = 140)
        assertEquals(AlertLevel.CRITICAL, result)
    }

    @Test
    fun evaluateGlucose_whenCriticalLow_returnsCritical() {
        val result = HealthEvaluator.evaluateGlucose(value = 50.0, maxGlucose = 140)
        assertEquals(AlertLevel.CRITICAL, result)
    }

    @Test
    fun evaluateGlucose_boundaryValues() {
        assertEquals(AlertLevel.WARNING, HealthEvaluator.evaluateGlucose(54.0))
        assertEquals(AlertLevel.CRITICAL, HealthEvaluator.evaluateGlucose(53.9))
        assertEquals(AlertLevel.WARNING, HealthEvaluator.evaluateGlucose(300.0))
        assertEquals(AlertLevel.CRITICAL, HealthEvaluator.evaluateGlucose(300.1))
        assertEquals(AlertLevel.NONE, HealthEvaluator.evaluateGlucose(140.0, 140))
        assertEquals(AlertLevel.WARNING, HealthEvaluator.evaluateGlucose(140.1, 140))
    }

    // =========================================================================
    // PRUEBAS FRECUENCIA CARDÍACA
    // =========================================================================

    @Test
    fun evaluateHeartRate_whenNormal_returnsNone() {
        val result = HealthEvaluator.evaluateHeartRate(value = 75.0, maxHeartRate = 100)
        assertEquals(AlertLevel.NONE, result)
    }

    @Test
    fun evaluateHeartRate_whenThresholdIsZero_usesDefaultValue() {
        // Con umbral 0, se usa DEFAULT_MAX_HEART_RATE = 100
        val resultNormal = HealthEvaluator.evaluateHeartRate(value = 80.0, maxHeartRate = 0)
        assertEquals(AlertLevel.NONE, resultNormal)

        val resultWarning = HealthEvaluator.evaluateHeartRate(value = 105.0, maxHeartRate = 0)
        assertEquals(AlertLevel.WARNING, resultWarning)
    }

    @Test
    fun evaluateHeartRate_whenExceedsUserThreshold_returnsWarning() {
        val result = HealthEvaluator.evaluateHeartRate(value = 95.0, maxHeartRate = 90)
        assertEquals(AlertLevel.WARNING, result)
    }

    @Test
    fun evaluateHeartRate_whenLowWarning_returnsWarning() {
        val result = HealthEvaluator.evaluateHeartRate(value = 50.0, maxHeartRate = 100)
        assertEquals(AlertLevel.WARNING, result)
    }

    @Test
    fun evaluateHeartRate_whenCriticalHigh_returnsCritical() {
        val result = HealthEvaluator.evaluateHeartRate(value = 140.0, maxHeartRate = 100)
        assertEquals(AlertLevel.CRITICAL, result)
    }

    @Test
    fun evaluateHeartRate_whenCriticalLow_returnsCritical() {
        val result = HealthEvaluator.evaluateHeartRate(value = 35.0, maxHeartRate = 100)
        assertEquals(AlertLevel.CRITICAL, result)
    }

    @Test
    fun evaluateHeartRate_boundaryValues() {
        assertEquals(AlertLevel.WARNING, HealthEvaluator.evaluateHeartRate(40.0))
        assertEquals(AlertLevel.CRITICAL, HealthEvaluator.evaluateHeartRate(39.9))
        assertEquals(AlertLevel.WARNING, HealthEvaluator.evaluateHeartRate(130.0))
        assertEquals(AlertLevel.CRITICAL, HealthEvaluator.evaluateHeartRate(130.1))
    }

    // =========================================================================
    // PRUEBAS PRESIÓN ARTERIAL
    // =========================================================================

    @Test
    fun evaluatePressure_whenNormal_returnsNone() {
        val result = HealthEvaluator.evaluatePressure(
            systolic = 115,
            diastolic = 75,
            maxSystolic = 130,
            maxDiastolic = 85
        )
        assertEquals(AlertLevel.NONE, result)
    }

    @Test
    fun evaluatePressure_whenThresholdIsZero_usesDefaultValues() {
        // Con umbrales 0, se usan DEFAULT_MAX_SYSTOLIC = 130 y DEFAULT_MAX_DIASTOLIC = 85
        val resultNormal = HealthEvaluator.evaluatePressure(120, 80, maxSystolic = 0, maxDiastolic = 0)
        assertEquals(AlertLevel.NONE, resultNormal)

        val resultWarning = HealthEvaluator.evaluatePressure(135, 80, maxSystolic = 0, maxDiastolic = 0)
        assertEquals(AlertLevel.WARNING, resultWarning)
    }

    @Test
    fun evaluatePressure_whenSystolicExceedsUserThreshold_returnsWarning() {
        val result = HealthEvaluator.evaluatePressure(125, 75, maxSystolic = 120, maxDiastolic = 80)
        assertEquals(AlertLevel.WARNING, result)
    }

    @Test
    fun evaluatePressure_whenDiastolicExceedsUserThreshold_returnsWarning() {
        val result = HealthEvaluator.evaluatePressure(115, 90, maxSystolic = 130, maxDiastolic = 85)
        assertEquals(AlertLevel.WARNING, result)
    }

    @Test
    fun evaluatePressure_whenCriticalHighSystolic_returnsCritical() {
        val result = HealthEvaluator.evaluatePressure(180, 80)
        assertEquals(AlertLevel.CRITICAL, result)
    }

    @Test
    fun evaluatePressure_whenCriticalHighDiastolic_returnsCritical() {
        val result = HealthEvaluator.evaluatePressure(130, 120)
        assertEquals(AlertLevel.CRITICAL, result)
    }

    @Test
    fun evaluatePressure_whenCriticalLowSystolic_returnsCritical() {
        val result = HealthEvaluator.evaluatePressure(65, 50)
        assertEquals(AlertLevel.CRITICAL, result)
    }

    @Test
    fun evaluatePressure_whenCriticalLowDiastolic_returnsCritical() {
        val result = HealthEvaluator.evaluatePressure(100, 35)
        assertEquals(AlertLevel.CRITICAL, result)
    }

    @Test
    fun evaluatePressure_boundaryValues() {
        assertEquals(AlertLevel.WARNING, HealthEvaluator.evaluatePressure(179, 119))
        assertEquals(AlertLevel.CRITICAL, HealthEvaluator.evaluatePressure(180, 119))
        assertEquals(AlertLevel.CRITICAL, HealthEvaluator.evaluatePressure(179, 120))
    }
}
