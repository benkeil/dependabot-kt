package de.benkeil.dependabotkt.serialization.kotlinx

import java.util.TimeZone
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class TimeZoneKSerializer : KSerializer<TimeZone> {
  override val descriptor: SerialDescriptor
    get() = PrimitiveSerialDescriptor("TimeZone", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): TimeZone {
    TODO("Not yet implemented")
  }

  override fun serialize(encoder: Encoder, value: TimeZone) {
    encoder.encodeString(value.toZoneId().id)
  }
}
