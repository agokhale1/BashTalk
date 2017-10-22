# BashTalk
A networked chat system written in Java.

# Setup
The BashTalk network consists of multiple client applications connected to a single central server. These instructions cover setting up a client. Further instructions on setting up a server and creating a network can be found below.

1. Download latest versino of BashTalkClient.zip from the build folder of this repository. (link needed)
2. Extract the files.
3. Inside the extracted folder, run client.bat (Windows systems) or client.sh (Mac or Unix-like systems)
These files run the command:
```
java -jar BashTalkClient.jar
```
On Windows, this command can easily be made into a shortcut as well (instructions below).
4. After running the jar file, a login interface will appear.
5. In the respective fields, enter a personal username, the IP address of the desired BashTalk server, and the port on which that server is listening.
6. Upon connection and validation of the username, a welcome message, any cached messages, and a list of active users will appear.
7. From here, chatting can begin!

## Where art thou, Java?
### Installation
If you don't already have Java installed, please download and install it from (Java's download page)[https://www.java.com/en/download/].

### Already installed?
If you already have Java installed and are not able to run the client, try following the steps provided by (this)[https://javatutorial.net/set-java-home-windows-10] article on adding Java to your PATH variable (Windows). This problem does not usually present itself to Mac or Linux users, but in the case that it does, (this)[https://www.java.com/en/download/help/path.xml] article by Oracle includes more advanced instructions for most versions of Windows, Mac, and Linux.

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

### /clear_cache
Clear the server's message history. To run successfully, this command requires password authentication. While the server's message cache will be cleared for future users, active user's screens will not be cleared. Until a user logs out or calls /clear on their own screen, they will still be able to see their screen history.
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



