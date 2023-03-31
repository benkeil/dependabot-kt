package de.benkeil.dependabotkt.serialization.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.DayOfWeek

class DayOfWeekJsonSerializer : JsonSerializer<DayOfWeek>() {
  override fun serialize(value: DayOfWeek?, gen: JsonGenerator, serializers: SerializerProvider) {
    value?.let { gen.writeString(it.name.lowercase()) }
  }

  override fun handledType(): Class<DayOfWeek> = DayOfWeek::class.java
}
