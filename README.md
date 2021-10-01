# TibetanBuddhistPracticeDates
A calendar with Tibetan Buddhist practice dates, written in Java with Spring Boot and Spring Native. 

## Description
This project implements a web-based calendar which calculates and shows Buddhist practice dates based on the Tibetan luni-solar calendar.

Practice dates can be displayed or downloaded as an .ics file.

A live version of the application is available at https://practicedates.christian-steinert.de

## Implementation
The application is implemented with and Java Spring Boot and compiled into a linux binary with Spring Native and GraalVM.
The project folder is an IntelliJ IDEA project but the build process is based on maven and can be compiled without any IDE.

To trigger the native build process it is enough to have GraalVM and maven installed and run build-native-image.sh
The build process creates both a standalone native binary that can be run without separate Java VM and also a docker image that can be run inside a docker container.

There are also some scripts in the bin/ folder that show how the resulting application can be run as a systemd service either as standalone application or as docker container.

The calendar computations for converting western to Tibetan dates are done with some older Java classes that are based on work of Dr. Alexander Berzin which was ported to Java.

All practice dates are then derived from the Tibetan dates according to simple rules that are hard-coded inside the application.


## Used calendar library
The calendar display primarily uses TUI Calendar https://ui.toast.com/tui-calendar and a little bit of jQuery.

## License
All code that is specific to this application is available under GPL Version 3.
Included components like TUI Calendar, jQuery or Spring have their own respective licenses.
