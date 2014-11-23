all:
	arm-linux-gnueabihf-gcc -o ~/cmpt433/public/test test.c halosuit/halosuit.c
clean:
	rm ~/cmpt433/public/test