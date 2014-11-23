#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#include "beagleblue/beagleblue.h"
#include "halosuit/halosuit.h"

void callback(char *buf)
{
	printf("%s", buf);
	fflush(stdout);
	if (strncmp(buf, "exit", 4) == 0) {
		beagleblue_exit();
	}
}

int main()
{
	char buf[1024] = { 0 };
	beagleblue_init(&callback);
	halosuit_init();
	double temp0;
	while (1) {
		halosuit_temperature_value(HEAD, &temp0);
		sprintf(buf, "{\"temperature\":{\"temp0\":%.5f,\"temp1\":0.12345}}\n", (double)temp0);
		beagleblue_android_send(buf);
		memset(buf, 0, sizeof(buf));
		sleep(1);
	}
	return 0;
}