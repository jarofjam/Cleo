## Cleo - Simple chat room
### General product vision
<p>Cleo is the realization of my desire to create a network application in pure java.
A simple server, a simple client - this project has not yet moved further from the mvp stage.</p>
<p>And whether its development will be continued or not is still questionable. 
The reason is that it is necessary to hold a balance between own implementation of basic functionality for the sake of interest
and unnecessary reinvention of the wheel. In other words, to the increasingly frequently appearing question
"Why do I keep spending my time on core functionality, when I could just use the framework 
and spend ttime on creating my own cool features?", I want to have the answer "Because it's interesting", and not "Really, why?"</p>

### Implemented functionality
<p>As already mentioned, Cleo is an mvp. The server starts, waits for incoming connections and processes messages from them.
The client connects to the server and provides the user with the ability to exchange messages with other users.
The whole chat consists of one room. Each user can choose any unallocated name 
and mention other users by names in his messages, which will highlight the message text on the client of the mentioned user.</p>
<p>And that is all for now.</p>

### Development plans
<p>The closest Cleo development options I see in the upgrading of the user interface and the creation of clearer user profile.
What in aggregate will result, for example, in personal and group messages with a fixed set of users.</p>
