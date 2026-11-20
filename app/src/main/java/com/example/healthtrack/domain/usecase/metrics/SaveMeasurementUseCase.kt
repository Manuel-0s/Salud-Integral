package com.example.healthtrack.domain.usecase.metrics

import com.example.healthtrack.core.utils.HealthEvaluator
import com.example.healthtrack.domain.model.*
import com.example.healthtrack.domain.repository.AuthRepository
import com.example.healthtrack.domain.repository.MeasurementRepository
import com.example.healthtrack.domain.repository.UserRepository
import com.google.firebase.Timestamp

class SaveMeasurementUseCase(
    private val repository: MeasurementRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(type: MetricsField, val1: String, val2: String?): SaveMetricResult {
        val userId = authRepository.getCurrentUserUid()
            ?: return SaveMetricResult.Error("No se pudo obtener el ID del usuario")

        val n1 = val1.toDoubleOrNull() ?: return SaveMetricResult.Error("El valor principal no es un número válido")

        val errorResult = validateBusinessRules(type, n1, val2)
        if (errorResult != null) return errorResult

        val timestamp = Timestamp.now()
        val user = userRepository.getUserData()

        return when (type) {
            MetricsField.BLOOD_PRESSURE -> {
                val diastolicValue = val2?.toIntOrNull() ?: 0
                val systolicValue = n1.toInt()
                val systolic = Measurement.SystolicPressure(userId, timestamp, MetricsField.SYSTOLIC_PRESSURE.label, systolicValue)
                val diastolic = Measurement.DiastolicPressure(userId, timestamp, MetricsField.DIASTOLIC_PRESSURE.label, diastolicValue)

                val resSystolic = repository.saveMeasurement(systolic)
                if (resSystolic is SaveMetricResult.Error) return resSystolic

                val resDiastolic = repository.saveMeasurement(diastolic)
                if (resDiastolic is SaveMetricResult.Error) return resDiastolic

                val level = HealthEvaluator.evaluatePressure(
                    systolicValue, diastolicValue,
                    user?.maxPressureSistolica, user?.maxPressureDiastolica
                )
                val statusMsg = HealthEvaluator.getPressureStatus(systolicValue, diastolicValue)
                SaveMetricResult.Success(level, "Alerta de Presión Arterial ($systolicValue/$diastolicValue)", statusMsg)
            }
            MetricsField.IMC -> {
                val heightValue = val2?.toDoubleOrNull() ?: 1.0
                val imcValue = n1 / (heightValue * heightValue)

                val imcMeasurement = Measurement.IMC(userId, timestamp, type.label, imcValue)
                val res = repository.saveMeasurement(imcMeasurement)
                if (res is SaveMetricResult.Error) return res

                val level = if (imcValue < 18.5 || imcValue >= 25.0) AlertLevel.WARNING else AlertLevel.NONE
                val statusMsg = HealthEvaluator.getIMCStatus(imcValue)
                SaveMetricResult.Success(level, "Diagnóstico de IMC (${String.format("%.1f", imcValue)})", statusMsg)
            }
            MetricsField.GLUCOSE -> {
                val glucose = Measurement.Glucose(userId, timestamp, type.label, n1)
                val res = repository.saveMeasurement(glucose)
                if (res is SaveMetricResult.Error) return res

                val level = HealthEvaluator.evaluateGlucose(n1, user?.maxGlucose)
                val statusMsg = HealthEvaluator.getGlucoseStatus(n1)
                SaveMetricResult.Success(level, "Alerta de Glucosa ($n1 mg/dL)", statusMsg)
            }
            MetricsField.FREQUENCY -> {
                val heartRate = Measurement.HeartRate(userId, timestamp, type.label, n1)
                val res = repository.saveMeasurement(heartRate)
                if (res is SaveMetricResult.Error) return res

                val level = HealthEvaluator.evaluateHeartRate(n1, user?.maxHeartRate)
                val statusMsg = HealthEvaluator.getHeartRateStatus(n1)
                SaveMetricResult.Success(level, "Alerta de Ritmo Cardíaco (${n1.toInt()} BPM)", statusMsg)
            }
            else -> SaveMetricResult.Error("Tipo de métrica no soportado")
        }
    }
    private fun validateBusinessRules(type: MetricsField, n1: Double, val2: String?): SaveMetricResult.Error? {
        return when (type) {
            MetricsField.BLOOD_PRESSURE -> {
                val n2 = val2?.toIntOrNull()
                if (n1 < 50 || n1 > 250) SaveMetricResult.Error("La presión sistólica debe estar entre 50 y 250 mmHg", MetricsField.SYSTOLIC_PRESSURE)
                else if (n2 == null) SaveMetricResult.Error("Se requiere el valor de la presión diastólica", MetricsField.DIASTOLIC_PRESSURE)
                else if (n2 < 30 || n2 > 150) SaveMetricResult.Error("La presión diastólica debe estar entre 30 y 150 mmHg", MetricsField.DIASTOLIC_PRESSURE)
                else null
            }
            MetricsField.IMC -> {
                val n2 = val2?.toDoubleOrNull()
                if (n1 < 2.0 || n1 > 500.0) SaveMetricResult.Error("El peso debe estar entre 2 y 500 kg", MetricsField.WEIGHT)
                else if (n2 == null) SaveMetricResult.Error("Se requiere la estatura", MetricsField.HEIGHT)
                else if (n2 < 0.5 || n2 > 2.5) SaveMetricResult.Error("La estatura debe estar entre 0.5 y 2.5 metros", MetricsField.HEIGHT)
                else null
            }
            MetricsField.GLUCOSE -> {
                if (n1 < 20.0 || n1 > 600.0) SaveMetricResult.Error("El nivel de glucosa debe estar entre 20 y 600 mg/dL", MetricsField.GLUCOSE)
                else null
            }
            MetricsField.FREQUENCY -> {
                if (n1 < 30.0 || n1 > 250.0) SaveMetricResult.Error("La frecuencia cardíaca debe estar entre 30 y 250 BPM", MetricsField.FREQUENCY)
                else null
            }
            else -> null
        }
    }
}
