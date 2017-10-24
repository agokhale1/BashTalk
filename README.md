# BashTalk
A networked chat system written in Java.

# Setup
The BashTalk network consists of multiple client applications connected to a single central server. These instructions cover setting up a client. Further instructions on setting up a server and creating a network can be found below.

1. Download latest version of BashTalkClient.zip from the build folder of this repository. (link needed)
2. Extract the files.
3. Inside the extracted folder, run client.bat (Windows systems) or client.sh (Mac or Unix-like systems)
These files run the command:
```
java -jar BashTalkClient.jar
```
4. After running the jar file, a login interface will appear.
5. In the respective fields, enter a personal username, the IP address of the desired BashTalk server, and the port on which that server is listening.
6. Upon connection and validation of the username, a welcome message, any cached messages, and a list of active users will appear.
7. From here, chatting can begin!

## Where art thou, Java?
### Installation
If you don't already have Java installed, please download and install it from [Java's download page](https://www.java.com/en/download/).

### Already installed?
If you already have Java installed and are not able to run the client, try following the steps provided by [this](https://javatutorial.net/set-java-home-windows-10) article on adding Java to your PATH variable (Windows). This problem does not usually present itself to Mac or Linux users, but in the case that it does, [this](https://www.java.com/en/download/help/path.xml) article by Oracle includes more advanced instructions for most versions of Windows, Mac, and Linux.

# Chat Commands
BashTalk syntax is very simple and similar to that of many IRC clients. Commands always start with the "/" character followed by the necessary arguments. As a template,
```
/command argument1 argument2
```
would be the form of a typical command. The following commands can currently be used.

### /users
List the usernames of all users currently logged into the server.
```
/users
```

### /pmsg
Send a private message to a logged-on user. This command DOES NOT create a private channel. In order to have a private conversation, the /pmsg command must be used at the beginning of every message between the two clients. 
```
/pmsg <recipient> <message>
/pmsg fakeUser0 Howdy there.
/pmsg fakeUser1 Got eeeeem!
```

### /mute
Mute a user on the server. The user is still allowed to private message.
```
/mute annoyingPerson1
[00:00] <peaceAndQuiet> muted <annoyingPerson1>.
```

### /unmute
Unmute a user on the server.
```
/unmute lessAnnoyingPerson2
[00:00] <forgivenessIsKey> unmuted <lessAnnoyingPerson2>.
```

### /clear
Clear all content from the local display. This will not clear messages on other user's displays or on the server.
```
/clear
```

### /exit
Logout of the server and close the client application.
```
/exit
```
The active users on the server will be notified, but the notification will not be cached or visible to users who log on in the future.

## Admin Commands
To run successfully, these commands require password authentication.

### /boot
Boot a user from the current session. The group is notified that the user has been booted.
/boot reallyAnnoyingPerson
[00:00] <# server #> Enter password:
thisIsTheWrongPassword
[00:00] <# server #> Authentication failed.
/boot reallyAnnoyingPerson
thisIsTheRightPassword
[00:00] <reallyAnnoyingPerson> was booted from the server.

### /clear_cache
Clear the server's message history. While the server's message cache will be cleared for future users, active user's screens will not be cleared. Until a user logs out or calls /clear on their own screen, they will still be able to see their screen history.
```
/clear_cache
[00:00] <# server #> Enter password:
thisIsTheWrongPassword
[00:00] <# server #> Authentication failed.
/clear_cache
thisIsTheRightPassword
[00:00] <# server #> Cache cleared.
```
Take care to read the server's instructions as entering the password at the wrong time could result in it being broadcast to the entire group.

# Setting Up a Server
The BashTalk server is the centerpiece (literally) of the entire network. Without a working server, each client is useless. Initially setting up the server is very similar to setting up the client.
1. Download latest version of BashTalkServer.zip from the build folder of this repository. (link needed)
2. Extract the files.
3. Inside the extracted folder, run server.bat (Windows systems) or server.sh (Mac or Unix-like systems)
These files run the command:
```
java -jar BashTalkServer.jar
```
4. After running the jar file, the server will display its address and port information.
5. Users can now connect using this information.

## Testing locally
Got the server up? Take it for a spin locally.
1. Run the client on the same computer as the server.
2. Connect using the address 127.0.0.1 and the port 9898.
3. The server should indicate a successful join.

## Network Address
To consistently connect to the server, ensure that its IP will not change. This can be achieved through DHCP Reservation, Static IP assignment, or simply never turning the server off.

## Connecting from a shared subnet
If both the server and client are on a shared local network (i.e. a simple one-router home network), clients can connect using the information listed under "Local:" on the server boot screen.

## Connecting from the outside world
Connecting from the world outside a local subnet requires exposing the server to that world. This exposure can be exploited. Please be careful.
1. Locate the network router's configuration settings (try visiting 192.168.1.1 or 192.168.1.254 in a web browser).
2. Configure the router to forward port 9898 of the BashTalk server to an outside port (can be any port not already in use).
3. Connect to the server using the "External: " IP address from the server boot screen as the address and the port number to which you forwarded 9898 as the port.

# Creating a Shortcut on Windows
A shortcut to launch either the client or server can be created and copied on a Windows computer.
1. Copy the path to the BashTalk(server/client).bat file.
1. Right click on the Desktop.
2. Select *New -> Shortcut* from the context menu.
3. Paste the full path to the BashTalk(server/client).bat file.
4. Select *Next*, name the shortcut, and click *Finish*.
5. Copy the shortcut to the desired location. Copying the shortcut to the path below, for example, will add BashTalk to the Start Menu program list.
```
C:\Users\thisIsAWindowsUsername\AppData\Roaming\Microsoft\Windows\Start Menu\Programs
```

# Contact
[Aneesh Gokhale](https://github.com/agokhale1), [Jack McKernan](https://github.com/jmcker), and [Neel Patel](https://github.com/patelneel55)
