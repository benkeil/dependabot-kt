package de.benkeil.dependabotkt.serialization.kotlinx

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class LocalTimeKSerializer : KSerializer<LocalTime> {
  private val formatter = DateTimeFormatter.ofPattern("KK:mm")
  override val descriptor: SerialDescriptor
    get() = PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): LocalTime {
    TODO("Not yet implemented")
  }

  override fun serialize(encoder: Encoder, value: LocalTime) {
    encoder.encodeString(value.format(formatter))
  }
}
