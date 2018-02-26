<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Casino App</title>
         <link href="index.css"  rel="stylesheet" />
         <link rel="icon" href="./img/favicon.ico" type="image/x-icon">
    </head>
    <body>
        <div class="main">
            <div id="title" class="tittle">
                <h1>Casino App</h1>
                <form action="login" method="post">
                    <label>Name:</label><input name="name" type="text"  required />
                    <br><br>
                    <label>Date of Birth:</label><input name="dob" type="date" required />
                    <br><br>
                    <label>UserName:</label><input name="username" type="text" required  />
                    <br><br>
                    <label>Password:</label><input name="password" type="password" required />
                    <br><br>
                    <label>Image:</label>
                        <select name="quantity">
                            <option value="1" style="background-image:url(./img/male.jpg);">Male</option>
                            <option value="2" style="background-image:url(./img/male1.jpg);">Male</option>
                            <option value="3" style="background-image:url(./img/female.png);">Female</option>
                            <option value="4" style="background-image:url(./img/female1.png);">Female</option>
                        </select>
                    <br><br>
                    <input type="hidden" name="action" value="new" />
                    <input type="submit" value="Create User!" />
                </form>
                <br><br>
                <div id="board" class="board"></div>
                Already a user? Login <a href="login.jsp">Here!</a>
                <br><br>
                Admin Login <a href="admin.jsp">Here!</a>
            </div>
        </div>
        
    </body>
</html>
