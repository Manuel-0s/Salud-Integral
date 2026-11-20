package com.example.healthtrack.core.utils

import com.example.healthtrack.domain.model.AlertLevel

object HealthEvaluator {

    private const val DEFAULT_MAX_GLUCOSE = 140
    private const val DEFAULT_MAX_HEART_RATE = 100
    private const val DEFAULT_MAX_SYSTOLIC = 130
    private const val DEFAULT_MAX_DIASTOLIC = 85

    fun getGlucoseStatus(value: Double): String = when {
        value < 70 -> "Baja — Tus niveles de azúcar están por debajo del rango recomendado. Intenta consumir un carbohidrato de rápida acción."
        value < 100 -> "Normal — ¡Excelente! Tus niveles de glucosa se encuentran en el rango óptimo y saludable."
        value < 126 -> "Prediabetes — Tus niveles están ligeramente elevados. Es una buena oportunidad para ajustar tus hábitos diarios."
        else -> "Alta — Nivel de azúcar elevado. Recuerda monitorear tu alimentación y mantener una hidratación adecuada."
    }

    fun getPressureStatus(systolic: Int, diastolic: Int): String = when {
        systolic < 120 && diastolic < 80 -> "Normal — Tu presión arterial está en un rango óptimo. Tu corazón bombea sangre con total comodidad."
        systolic < 130 && diastolic < 80 -> "Elevada — Tu presión sistólica está un poco por encima de lo ideal. Intenta moderar el consumo de sal."
        systolic < 140 || diastolic < 90 -> "Hipertensión N1 — Tus niveles muestran una presión moderadamente alta."
        else -> "Hipertensión N2 — Presión arterial significativamente alta. Evita esfuerzos intensos y consulta las indicaciones de tu médico."
    }

    fun getHeartRateStatus(value: Double): String = when {
        value < 60 -> "Bajo (Bradicardia) — Tu ritmo cardíaco es menor al promedio en reposo. Común en atletas, pero vigila si sientes fatiga."
        value <= 100 -> "Normal — Tu corazón late a un ritmo constante, ideal y saludable para un estado de reposo."
        else -> "Alto (Taquicardia) — Tu frecuencia cardíaca está acelerada. Puede deberse a actividad reciente, estrés, cafeína o deshidratación."
    }

    fun getIMCStatus(value: Double): String = when {
        value < 18.5 -> "Bajo peso — Tu peso se encuentra por debajo del rango recomendado para tu estatura. Prioriza nutrientes de calidad."
        value < 25.0 -> "Normal — ¡Felicidades! Tienes una relación excelente entre tu peso y tu estatura. Mantén la consistencia."
        value < 30.0 -> "Sobrepeso — Tu peso está ligeramente por encima de lo ideal. Estás a tiempo de realizar pequeños ajustes en tu rutina."
        else -> "Obesidad — Rango de peso elevado que incrementa el esfuerzo de tu cuerpo. Enfócate en metas de salud graduales y seguras."
    }

    fun evaluateGlucose(value: Double, maxGlucose: Int? = null): AlertLevel {
        val threshold = if (maxGlucose != null && maxGlucose > 0) maxGlucose.toDouble() else DEFAULT_MAX_GLUCOSE.toDouble()
        return when {
            value < 54.0 || value > 300.0 -> AlertLevel.CRITICAL
            value < 70.0 || value > threshold -> AlertLevel.WARNING
            else -> AlertLevel.NONE
        }
    }

    fun evaluateHeartRate(value: Double, maxHeartRate: Int? = null): AlertLevel {
        val threshold = if (maxHeartRate != null && maxHeartRate > 0) maxHeartRate.toDouble() else DEFAULT_MAX_HEART_RATE.toDouble()
        return when {
            value < 40.0 || value > 130.0 -> AlertLevel.CRITICAL
            value < 60.0 || value > threshold -> AlertLevel.WARNING
            else -> AlertLevel.NONE
        }
    }

    fun evaluatePressure(
        systolic: Int,
        diastolic: Int,
        maxSystolic: Int? = null,
        maxDiastolic: Int? = null
    ): AlertLevel {
        val sysThreshold = if (maxSystolic != null && maxSystolic > 0) maxSystolic else DEFAULT_MAX_SYSTOLIC
        val diaThreshold = if (maxDiastolic != null && maxDiastolic > 0) maxDiastolic else DEFAULT_MAX_DIASTOLIC
        return when {
            systolic >= 180 || diastolic >= 120 || systolic < 70 || diastolic < 40 -> AlertLevel.CRITICAL
            systolic > sysThreshold || diastolic > diaThreshold -> AlertLevel.WARNING
            else -> AlertLevel.NONE
        }
    }
}