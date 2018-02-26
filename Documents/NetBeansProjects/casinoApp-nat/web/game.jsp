
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Game Page</title>
        <link href="index.css"  rel="stylesheet" />
        <link rel="icon" href="./img/favicon.ico" type="image/x-icon" >
    </head>
    <body>
        <div class="main">
            <div class="tittle">
                <h1>Choose a game</h1>
            </div>     
        </div>
        <div class="inner">
        <div class="welcome">
                Name: ${user.name}<br>
                Balance: $${user.balance}
            </div>
                <div class="choices">
                    <a href="poker.jsp">
                        <div class="choice">
                            Poker
                            <br>
                            <br>
                            <img src="./img/Ace of Spades.png" />
                        </div>
                    </a>
                    <a href="craps.jsp">
                        <div class="choice">
                            Craps
                            <br>
                            <br>
                            <img src="./img/5.jpg" />
                        </div>
                    </a>
                    <a href="bingo.jsp">
                        <div class="choice">
                            Bingo
                            <br>
                            <br>
                            <img src="./img/King of Spades.png" />
                        </div>
                    </a>
                    <a href="hi-lo.jsp">
                        <div class="choice">
                            Hi-Lo
                            <br>
                            <br>
                            <img src="./img/Queen of Spades.png" />
                        </div>
                    </a>
                </div>
        </div>
    </body>
</html>
