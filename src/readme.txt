/*******************************************************************************
 *                               CactusBase.java                               *
 ******************************************************************************/

Author: Jacob Van Buren
Version: 2.1.1
Organization: PHS Computer Robotics club


/*******************************************************************************
 * General Info                                                                *
 ******************************************************************************/

CactusBase is an abstract class that provides no functionality on its own.
Rather, it is intended to be used as a base class for any programs designed for
Cactus the robot. 


/*******************************************************************************
 * Constructor                                                                 *
 ******************************************************************************/

There should not be multiple instances of the base program for the robot, so
no constructor is included.


/*******************************************************************************
 * Instance Variables                                                          *
 ******************************************************************************/

None: There is only one robot, so everything is static.


/*******************************************************************************
 * Static (Class) Variables                                                    *
 ******************************************************************************/

CactusBase provides a set of class variables that correspond to the
various sensors on cactus at the moment (subject to change as cactus is
modified, of course). All of these are instantiated when the VM is started.
CactusBase also provides the mutable protected variable stdout, which is the
output stream used by all of the helper methods.


/*******************************************************************************
 * Instance Methods                                                            *
 ******************************************************************************/

None (see Instance Variables).

/*******************************************************************************
 * Static (Class) Methods                                                      *
 ******************************************************************************/

CactusBase has a variety of helper methods. It has an overloaded print()
function that simplifies output by catching exceptions and printing to stdout,
which is by default initialized to the display on top of cactus.
It also provides a relatively precise sleepFor Method, which will make the
thread sleep for a specified duration, ignoring any interrupts while using the
least amount of CPU power possible. leftCM and rightCM help deliver easy access
to the front-facing IR distance sensors, and play provides a simple interface to
play sounds.

/*******************************************************************************
 * Nested Motor class                                                          *
 ******************************************************************************/

The Servo class that comes with RoboJDE does not account for Cactus' continuous
servo motors, so Motor abstracts that away and provides a nice interface for
making Cactus move without dealing with the annoyances of the inaccurate Servo
API.
