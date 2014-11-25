/*
    serializer.c
*/

#include <stdlib.h>
#include <stdio.h> 
#include <string.h>

#include <serializer.h>
#include <json.h>
#include <json-builder.h>
#include <beagleblue.h>
#include <halosuit.h>

#define ON "on"
#define OFF "off"
#define AUTO "auto"

static void serializer_buildjson(json_value *object)
{
    int value = 0;
    // lights
    if (halosuit_relay_value(LIGHTS, &value) != 0) {
	printf("ERROR: LIGHTS READ VALUE FAILURE\n");
    }	
    if (value == 1) {
	json_object_push(object, "lights", json_string_new(ON));
    }
    else if (value == 0) {
	json_object_push(object, "lights", json_string_new(OFF));
    }
    
    // auto lights
    if (halosuit_relay_value(LIGHTS_AUTO, &value) != 0) {
	printf("ERROR: LIGHTS_AUTO READ VALUE FAILURE\n");
    }	
    if (value == 1) {
	json_object_push(object, "lights", json_string_new(AUTO));
    }
    
    // head lights white
    if (halosuit_relay_value(HEADLIGHTS_WHITE, &value) != 0) {
	printf("ERROR: HEADLIGHTS_WHITE READ VALUE FAILURE\n");
    }	
    if (value == 1) {
	json_object_push(object, "head lights white", json_string_new(ON));
    }
    else if (value == 0) {
	json_object_push(object, "head lights white", json_string_new(OFF));
    }

    // head lights red
    if (halosuit_relay_value(HEADLIGHTS_RED, &value) != 0) {
	printf("ERROR: HEADLIGHTS RED READ VALUE FAILURE\n");
    }	
    if (value == 1) {
	json_object_push(object, "head lights red", json_string_new(ON));
    }
    else if (value == 0) {
	json_object_push(object, "head lights red", json_string_new(OFF));
    }
    
    // head fans
    if (halosuit_relay_value(HEAD_FANS, &value) != 0) {
	printf("ERROR: HEAD_FANS READ VALUE FAILURE\n");
    }	
    if (value == 1) {
	json_object_push(object, "head fans", json_string_new(ON));
    }
    else if (value == 0) {
	json_object_push(object, "head fans", json_string_new(OFF));
    }
    
    // water pump
    if (halosuit_relay_value(WATER_PUMP, &value) != 0) {
	printf("ERROR: WATER_PUMP READ VALUE FAILURE\n");
    }	
    if (value == 1) {
	json_object_push(object, "water pump", json_string_new(ON));
    }
    else if (value == 0) {
	json_object_push(object, "water pump", json_string_new(OFF));
    }

    // water fan
    if (halosuit_relay_value(WATER_FAN, &value) != 0) {
	printf("ERROR: WATER_FAN READ VALUE FAILURE\n");
    }	
    if (value == 1) {
	json_object_push(object, "water fan", json_string_new(ON));
    }
    else if (value == 0) {
	json_object_push(object, "water fan", json_string_new(OFF));
    }
    
    // peltier
    if (halosuit_relay_value(PELTIER, &value) != 0) {
	printf("ERROR: PELTIER READ VALUE FAILURE\n");
    }	
    if (value == 1) {
	json_object_push(object, "peltier", json_string_new(ON));
    }
    else if (value == 0) {
	json_object_push(object, "peltier", json_string_new(OFF));
    }

    // temperature readings 
    double temperature = 0;
    
    // head temperature
    if (halosuit_temperature_value(HEAD, &temperature) != 0) {
	printf("ERROR: HEAD TEMPERATURE READ FAILURE\n");
    }
    json_object_push(object, "head temperature", json_double_new(temperature)); 

    // armpits temperature 
    if (halosuit_temperature_value(ARMPITS, &temperature) != 0) {
	printf("ERROR: ARMPITS TEMPERATURE READ FAILURE\n");
    }
    json_object_push(object, "armpits temperature", json_double_new(temperature));

    // crotch temperature 
    if (halosuit_temperature_value(CROTCH, &temperature) != 0) {
	printf("ERROR: CROTCH TEMPERATURE READ FAILURE\n");
    }
    json_object_push(object, "crotch temperature", json_double_new(temperature));

    // water temperature 
    if (halosuit_temperature_value(WATER, &temperature) != 0) {
	printf("ERROR: WATER TEMPERATURE READ FAILURE\n");
    }
    json_object_push(object, "water temperature", json_double_new(temperature));
}

void serializer_serialize()
{ 
    json_value *object = json_object_new(0);

    // push suit status values into object
    serializer_buildjson(object);

    char *buf = malloc(json_measure(object));
    json_serialize(buf, object);

    if (beagleblue_glass_send(buf) != 0) {
        printf("ERROR: GLASS SEND FAILURE\n");
    }
    if (beagleblue_android_send(buf) != 0) {
        printf("ERROR: ANDROID SEND FAILURE\n");
    }	

    free(buf);
    json_builder_free(object);
}
