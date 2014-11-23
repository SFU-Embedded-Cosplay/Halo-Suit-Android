/* **********************************
 * beagleblue.h
 *
 */

#define TIMEOUT_SEC 5
#define TIMEOUT_USEC 0
#define BUFFER_SIZE 1024

int beagleblue_glass_send(char *); //sends char buffer with int specifying the number of characters returns the number of bytes sent
int beagleblue_android_send(char *);
void beagleblue_init(void (*on_receive)(char *)); //takes callback function as value which gets performed when something is received
void beagleblue_exit(); //stops threads
void beagleblue_join(); //waits for threads to join

/* **********************************
 * Hey guys let me know what you think of this given functionality
 * I can easily change anything
 */