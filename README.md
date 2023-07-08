<a name="br1"></a> 

Jake Anderson

05/03/23

**Proposal - Personal Health Planer**

Specification

The application I propose on making is a weekly reminder for each health related activity

(mainly targeted at running) the user plans on doing for that week. The app extends to prior

weeks in order for the user to view previous workouts they have done and even into the future

for the user to plan ahead. The app aims to provide a solid base for personal fitness analytics.

The user assigns a day and a type as well as any extra notes associated with the workout and if

applicable a time (i.e. the time it took to complete).

Types include -

\-

\-

\-

Track

Long run

Gym

These are then distinguished by different groupings on the main page. By having these tags it

also allows for faster filtering of the main page.

Notifications will be sent in the morning (8 am, based on users current time zone) as a reminder

of the task. A further notification will be sent at 7pm to confirm the task has been completed

(assuming sessions will have all been completed by the evening). By tapping this notification it

will add a tick to the session name to show that this session is now completed.

By clicking on any session within the main screen it will give the user the option to view session

details which they can decide to share the session to another app, from here we can return or

decide to edit said session. The edit session screen is reused in order to create new sessions

too.

Finally we have the option to configure the user's view by using the settings page.



<a name="br2"></a> 

Jake Anderson

05/03/23

Feature overview -

Feature type

Session

Screen

Main

Feature

Implementation

Add sessions

Delete sessions

Inserting to database

Session

Main

Removing record

from database

Session

Session

Session

View

Edit

View sessions

Edit session

Filter session

Selecting from

database

Updating record from

database

Main

Select session name

where the name

contains input.

Notifications

N/A (mobile)

Get notification

reminders for

sessions

Using

NotificationCompat

API when day from

database is current

day

User Experience

User Experience

Settings

Settings

Light / dark mode

Brightness

By inheriting the

DayNight theme in

styles.xml

By having a

changeWriteSettings

Permission override

in Manifest.xml so

users can change

device settings.

Administration

Functional

Social

Settings

Remove / add

features

Conditions stating if

ticked don't display.

For unique names

use UNIQUE in SQL.

Week selector

View

View session / month Selecting session

analytics

information from

database for weeks

in that month

Share session

Using Android

Sharesheet to send

session information

to apps such as

message



<a name="br3"></a> 

Jake Anderson

05/03/23

Screen overview (state diagram):



<a name="br4"></a> 

Jake Anderson

05/03/23

Weekly sessions screen:

\-

\-

Click on session name to view details

Search bar to filter sessions

\-

Filter by name as well as tag

\-

\-

\-

Pressing the cross on any of the sessions will delete session permanently

Pressing the + icon will take us to the edit session screen

Click cog to go to settings page



<a name="br5"></a> 

Jake Anderson

05/03/23

Edit session screen:

\-

\-

Use session name box to enter name

Use day to select the day of the week it is

Dropdown

Use Session type to specify which type of session it is

Dropdown

Use the stopwatch to add a time

\-

\-

\-

\-

\-

\-

Buttons to control underneath

If time recorded it is saved here else 00:00:00

\-

\-

\-

Use the notes section to add related notes

Use the tick to apply changes

Use ← to return to previous screen



<a name="br6"></a> 

Jake Anderson

05/03/23

View session screen:

\-

\-

\-

← to return to week view sessions page

The button in the bottom right is used to edit the current session

Click the button in the top left to share the session to other apps



<a name="br7"></a> 

Jake Anderson

05/03/23

Monthly session view screen:

\-

\-

\-

Click on week in order to show week view screen for the given week

Use ← to show weeks associated with the previous month

Use → to show weeks associated with the next month



<a name="br8"></a> 

Jake Anderson

05/03/23

Settings screen:

\-

Click light mode in order enable light mode

Vice versa for dark mode

\-

\-

\-

Adjustable bar for brightness of screen

Tick the box to keep

\-

\-

\-

Monthly summary

\-

In the month view

Session times

\-

In the session view

Unique names

\-

For each session

\-

\-

\-

Tick *‘Remove all sessions’* to remove all sessions save (reset)

← to return to the session week view

Click on the floppy disk to apply setting changes

