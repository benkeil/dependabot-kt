package de.benkeil.dependabotkt.serialization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import de.benkeil.dependabotkt.dsl.Dependabot
import de.benkeil.dependabotkt.serialization.jackson.DayOfWeekJsonSerializer

internal val mapper =
    YAMLFactory.builder()
        .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        .enable(YAMLGenerator.Feature.ALLOW_LONG_KEYS)
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        .build()
        .let {
          ObjectMapper(it)
              .registerKotlinModule()
              .registerModule(SimpleModule().apply { addSerializer(DayOfWeekJsonSerializer()) })
              .registerModule(JavaTimeModule())
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .setSerializationInclusion(JsonInclude.Include.NON_NULL)
              .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
        }

fun Dependabot.toYaml(): String = mapper.writeValueAsString(this)
