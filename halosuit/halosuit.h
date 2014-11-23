typedef enum _Relay {
	LIGHTS,            	//GPIO 66
	LIGHTS_AUTO,       	//GPIO 67
	HEADLIGHTS_WHITE,	//GPIO 68  
	HEADLIGHTS_RED,		//GPIO 69
	HEAD_FANS,			//GPIO 44
	WATER_PUMP,			//GPIO 45
	WATER_FAN,			//GPIO 26
	PELTIER				//GPIO 46
} Relay;

typedef enum _PinState {
	LOW,
	HIGH
} PinState;

typedef enum _Location {
	HEAD,
	ARMPITS,
	CROTCH,
	WATER
} Location;

void halosuit_init(); //sets up the file descriptors
void halosuit_exit(); //closes the file descriptors

//on success returns 0, -1 on failure
int halosuit_relay_switch(Relay, PinState);

//changes value to relays value and returns 0 on success and -1 on failure
int halosuit_relay_value(Relay, int *value);

int halosuit_temperature_value(Location, double *temp);