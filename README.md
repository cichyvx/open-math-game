# open-math-game
## In short
A real-time multiplayer math game where players race to correctly solve simple arithmetic problems.
## Gameplay description
In this fast-paced multiplayer game, players compete to be the first to correctly answer a simple math problem. 
The game displays a random arithmetic problem, and the first player to submit the correct answer scores a point. 
Problems involve addition, subtraction, multiplication, and division of numbers between -10 and 10. 
The game lasts for one minute, and a new problem is only shown after a correct answer is submitted.
## rules
- The game is a real-time multiplayer competition.
- Players are presented with a simple arithmetic problem.
- The first player to type the correct answer scores a point.
- Problems involve addition, subtraction, multiplication, and division of numbers between -10 and 10.
- A new problem is displayed only after a correct answer is submitted.
-  The game lasts for one minute.

# Get started
### requirements
- java 21 or higher

### Run Open-Math locally
1. Go to Backend folder.
2. Run in console ``./mvnw clean install spring-boot:run``.
3. To stop it just ``ctrl + c`` on console.

### connecting
By default, game start at port ``8080``.</br>
To connect on your local machine you should us [http://localhost:8080/websocket](http://localhost:8080/websocket)</br>

### Running on production
You probably don't want to have debug logs.
To disable them you can run application using ``./mvnw clean install spring-boot:run -D"spring-boot.run.profiles"=default``

# Game flow

## Before start
Before you start reading next section you should know that all request that you send to Backend must be packed like that:
```
{
    "path": "path for operation",
    "data": {
        curent data that you want send...
    }
}
```

All request <strong>must</strong> have his path for cleaner distinction of what the player wants to do <!--- TODO add link to all paths-->

You should also note that basically in any moment, backend can send you request like
```
{
    "state": "SOME STATUS"
}
```
That mean you current game status has changed <!--- TODO add link to all status-->

## player registration
After You connected backend starting to storing your session.

#### sending content
For being able to actual playing you should create username. 
For that you should send request like this:
```
{
    "path": "/connect",
    "data": {
        "username": "your_username"
    }
}
```

#### receiving content
<strong> at this moment, game will not send you any data except status change</strong>


after that, game will save your username and provide you to start matchmaking for found your game.</br>
<strong>NOTE that at this moment your username is not showed in any moment of game, but that should change in future</strong></br>
<p>If you want, you can change your username by sending same request with different username. You can do it only when
you're not already in game nor in active matchmaking. Also if you ended your game, and not starting searched another, you also provied
to change username.
After this request, if is successful you can expect status changed for <strong>CONNECTED</strong>, otherwise your request isn't valid</p>

## Matchmaking
<p>When you choose your username you probably want to start playing. Game have matchmaking system that connect you to some
Other waiting player</p>

#### sending content
For start matchmaking you should send request wing empty data property.
```
{
    "path": "/matchmaking",
    "data": {
        
    }
}
```

#### receiving content
After successful change status to `MATCHMAKING`, you need to wait util game send `WAITING_FOR_GAME_START`. Then
you should receive sometking like:
```
{
    "enemyPlayerUsername": "014b7a23-ce80-ab2f-508c-e02b4289019d",
    "gameStart": "2025-01-12T18:13:54.853378800Z"
}
```

At this moment `enemyPlayerUsername` is random uuid,
`gameStart` its date-time telling you when your game will start.


## Playing

By default, one match took about one minute. And it's possible to play only one vs one.
Game also not sending who win, but send only score after each answer, so actual winner should by choose by last sent score after match end.

#### sending content
At this point, only data that you send is answers.
```
{
    "path": "/answer",
    "data": {
        "answer" : 1
    }
}
```

Property `answer` its float type so, it can be set as `1` or `1.0` etc.

#### receiving content
In game you can receive three type of response</br>
First is game change status:
```
{
    "state": "IN_GAME"
}
```
that say, game is actually started. 
Second is equation and as name say it represents current question:
```
{
    "equation": "6 - 2"
}
```
And last is sent only after one side answer correctly:
```
{
    "playerScore": 1,
    "enemyScore": 0
}
```

After game end your status is changed to `CONNECTED`
```
{
"state": "CONNECTED"
}
```

<strong>Note that backend sending request to client asynchronous, so you shouldn't put on that you receive request in order as above.</strong>

# Additional game info

## status

<table>
   <tr>
      <th>Status</th>
      <th>Description</th>
   </tr>
   <tr>
      <td>CONNECTED</td>
      <td>Player is connected, and waiting for action</td>
   </tr>
   <tr>
      <td>MATCHMAKING</td>
      <td>Player is in matchmaking queue</td>
   </tr>
   <tr>
      <td>WAITING_FOR_GAME_START</td>
      <td>Player found room, and waiting for game start</td>
   </tr>
   <tr>
      <td>IN_GAME</td>
      <td>Player is playing</td>
   </tr>
</table>

## paths

<table>
   <tr>
      <th>Path</th>
      <th>Description</th>
   </tr>
   <tr>
      <td>/connect</td>
      <td>path allowing user to set their username and prepare before game</td>
   </tr>
   <tr>
      <td>/answer</td>
      <td>Answering for question in game</td>
   </tr>
   <tr>
      <td>/matchmaking</td>
      <td>Join matchmaking queue</td>
   </tr>
</table>

# Contributing
Fell free to create your PR.
If you're looking for some ideas look for some change requests:<br>
[Backend should pack request similarly as it receiving them](https://github.com/cichyvx/open-math-game/issues/1)<br>
[Too complicated equation](https://github.com/cichyvx/open-math-game/issues/2)<br>
[setting equation difficulty](https://github.com/cichyvx/open-math-game/issues/3)<br>
[visable username](https://github.com/cichyvx/open-math-game/issues/4)<br>
[Connection closing](https://github.com/cichyvx/open-math-game/issues/5)<br>

Looking for something for Frontend? Fell free for creating it from scratch