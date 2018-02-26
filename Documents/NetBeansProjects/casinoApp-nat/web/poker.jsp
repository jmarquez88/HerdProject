<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Poker Page</title>
        <link href="index.css"  rel="stylesheet" />
        <link rel="icon" href="./img/favicon.ico" type="image/x-icon" >
    </head>
    <body>
        <div class="main">
            <div class="inner">
                <div class="choices">
                    <div class="choice">
                        <form action="PokerGame" method="post">
                            <input type="hidden" name="action" value="" />
                            <input type="submit" /> 
                        </form>
                    </div>
                    <div class="choice">
                        <form action="PokerGame" method="post">
                            <input type="hidden" name="action" value="" />
                            <input type="submit" /> 
                        </form>
                    </div>
                    <div class="choice">
                        <form action="PokerGame" method="post">
                            <input type="hidden" name="action" value="" />
                            <input type="submit" value="" /> 
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
