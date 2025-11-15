package com.restaurant.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * The interface Mapper util.
 */
public interface MapperUtil {

    /**
     * The constant logback.
     */
    Logger log = LoggerFactory.getLogger(MapperUtil.class);
    /**
     * The constant dateFormat.
     */
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Object mapper
     */
    ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .addHandler(
                    new DeserializationProblemHandler() {
                        @Override
                        public Object handleWeirdStringValue(DeserializationContext context, Class<?> targetType, String valueToConvert, String failureMsg) {
                            return null;
                        }

                        @Override
                        public Object handleWeirdNumberValue(DeserializationContext context, Class<?> targetType, Number valueToConvert, String failureMsg) {
                            return null;
                        }
                    }
            )
            .setDateFormat(dateFormat)
            .findAndRegisterModules();


    /**
     * Write value as string
     *
     * @param value object
     * @return string string
     * @throws JsonProcessingException the json processing exception
     */
    static String writeValueAsString(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    /**
     * Write value as string
     *
     * @param value         object
     * @param defaultString the default string
     * @return string string
     */
    static String writeValueAsStringOrDefault(Object value, String defaultString) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return defaultString;
        }
    }

    /**
     * Write value as string
     *
     * @param value object
     * @return string string
     */
    public static String writeValueAsStringOrDefault(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    /**
     * convertValue
     *
     * @param <T>         type
     * @param fromValue   fromValue
     * @param toValueType toValueType
     * @return value t
     */
    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return mapper.convertValue(fromValue, toValueType);
    }

    /**
     * convertValue
     *
     * @param <T>           type
     * @param fromValue     fromValue
     * @param typeReference the type reference
     * @return value t
     */
    static <T> T convertValue(Object fromValue, TypeReference<T> typeReference) {
        return mapper.convertValue(fromValue, typeReference);
    }

    /**
     * convertValue
     *
     * @param <T>             type
     * @param fromStringValue the from string value
     * @param toValueType     toValueType
     * @return value t
     */
    static <T> T readValue(String fromStringValue, Class<T> toValueType) {
        try {
            return mapper.readValue(fromStringValue, toValueType);
        } catch (JsonProcessingException e) {
            log.error("Read value error. Error: ", e);
            return null;
        }
    }

    /**
     * Read value t.
     *
     * @param <T>             the type parameter
     * @param fromStringValue the from string value
     * @param toValueType     the to value type
     * @return the t
     */
    static <T> T readValue(String fromStringValue, TypeReference<T> toValueType) {
        try {
            return mapper.readValue(fromStringValue, toValueType);
        } catch (JsonProcessingException e) {
            log.error("Read value error. Error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Read value t.
     *
     * @param <T>             the type parameter
     * @param fromStringValue the from string value
     * @param toValueType     the to value type
     * @return the t
     */
    static <T> T readValue(byte[] fromStringValue, Class<T> toValueType) {
        try {
            return mapper.readValue(fromStringValue, toValueType);
        } catch (Exception e) {
            log.error("Read value error. Error: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Read tree json node.
     *
     * @param content the content
     * @return the json node
     */
    static JsonNode readTree(String content) {
        try { // since 2.10 remove "impossible" IOException as per [databind#1675]
            return mapper.readTree(content);
        } catch (Exception e) { // shouldn't really happen but being declared need to
            log.error("readTree error. Error: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * convertValue
     *
     * @param <T>          type
     * @param fromValue    fromValue
     * @param toValueType  toValueType
     * @param defaultValue defaultValue
     * @return value t
     */
    static <T> T convertValue(Object fromValue, Class<T> toValueType, T defaultValue) {
        if (fromValue == null) {
            return defaultValue;
        }
        return mapper.convertValue(fromValue, toValueType);
    }

    /**
     * Map list list.
     *
     * @param <S>         source class
     * @param <T>         target class
     * @param source      source
     * @param targetClass targetClass
     * @return list after convert
     */
    static <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source.stream().map(e -> mapper.convertValue(e, targetClass)).toList();
    }

    /**
     * To string string.
     *
     * @param serializableObject object
     * @return string string
     * @throws JsonProcessingException the json processing exception
     */
    static String toString(Object serializableObject) throws JsonProcessingException {
        return mapper.writeValueAsString(serializableObject);
    }

    /**
     * Serialize
     *
     * @param obj Object
     * @return byte[] byte [ ]
     */
    static byte[] serialize(Object obj) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            mapper.writeValue(os, obj);
        } catch (IOException e) {
            return new byte[0];
        }
        return os.toByteArray();
    }

    /**
     * Deserialize
     *
     * @param data byte[]
     * @return Object object
     */
    static Object deserialize(byte[] data) {
        try {
            return deserialize(data, Object.class);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Deserialize
     *
     * @param <T>   Response class
     * @param data  byte[]
     * @param clazz Response class
     * @return object t
     * @throws IOException exception
     */
    static <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        return mapper.readValue(data, clazz);
    }

    /**
     * Gets type factory to convert list.
     *
     * @param <T>  the type parameter
     * @param type the type
     * @return the type factory to convert list
     */
    static <T> CollectionType getTypeFactoryToConvertList(Class<T> type) {
        return mapper.getTypeFactory().constructCollectionType(List.class, type);
    }

    /**
     * Convert value list.
     *
     * @param <T>                      the type parameter
     * @param cached                   the cached
     * @param typeFactoryToConvertList the type factory to convert list
     * @return the list
     */
    static <T> List<T> convertValue(Object cached, CollectionType typeFactoryToConvertList) {
        return mapper.convertValue(cached, typeFactoryToConvertList);
    }
}
