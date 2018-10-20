<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="500: Internal Server Error">
        <title>ShoppingList</title>
        <!-- Bootstrap core CSS -->
         <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" crossorigin="anonymous">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.8/css/all.css" crossorigin="anonymous">
        <link rel="stylesheet" href="css/floating-labels.css">
        <link rel="stylesheet" href="css/forms.css">
        <style>
            /* Error Page Inline Styles */
            body {
                padding-top: 20px;
            }
            /* Layout */
            .jumbotron {
                font-size: 21px;
                font-weight: 200;
                line-height: 2.1428571435;
                color: inherit;
                padding: 10px 0px;
            }
            /* Everything but the jumbotron gets side spacing for mobile-first views */
            .masthead, .body-content {
                padding-left: 15px;
                padding-right: 15px;
            }
            /* Main marketing message and sign up button */
            .jumbotron {
                text-align: center;
                background-color: transparent;
            }
            .jumbotron .btn {
                font-size: 21px;
                padding: 14px 24px;
            }
            /* Colors */
            .green {color:#5cb85c;}
            .orange {color:#f0ad4e;}
            .red {color:#d9534f;}
            .col-md-6{
                background-color: grey;
                max-width: 49%;
                float:left;
                margin:2px;
                border-radius: 10px;
            }
            .row{
                display:inline; 
            }
        </style>
        <script type="text/javascript">
            function loadDomain() {
                var display = document.getElementById("display-domain");
                display.innerHTML = document.domain;
            }
        </script>
    </head>
    <body onload="javascript:loadDomain();">
        <!-- Error Page Content -->
        <div class="container">
            <!-- Jumbotron -->
            <div class="jumbotron">
                <h1><span class="glyphicon glyphicon-fire red"></span> 500: Internal Server Error</h1>
                <p class="lead">The web server is returning an internal error for <em><span id="display-domain"></span></em>.</p>
                <a href="javascript:document.location.reload(true);" class="btn btn-default btn-lg text-center"><span class="green">Try This Page Again</span></a>
            </div>
        </div>
        <div class="container">
            <div class="body-content">
                <div class="row">
                    <div class="col-md-6">
                        <h2>What happened?</h2>
                        <p class="lead">A 500 error status implies there is a problem with the web server's software causing it to malfunction.</p>
                        <%
                            if (exception != null) {
                        %>
                        <p class="lead">Server returned: <%=exception.getCause() != null ? exception.getCause() : exception%>: <%=exception.getMessage()%></p>
                        <%
                            if (exception.getCause() != null) {
                                %>
                                <p>Cause: <%=exception.getCause()%></p>
                                <%
                            }
                        %>
                        <%
                            }
                        %>
                    </div>
                    <div class="col-md-6">
                        <h2>What can I do?</h2>
                        <p class="lead">If you're a site visitor</p>
                        <p> Nothing you can do at the moment. If you need immediate assistance, please send us an email instead. We apologize for any inconvenience.</p>
                        <p class="lead">If you're the site owner</p>
                        <p>This error can only be fixed by server admins, please contact your website provider.</p>
                    </div>
                </div>
            </div>
        </div>
        <!-- End Error Page Content -->
        <!--Scripts-->
        <!-- jQuery library -->
        <script src="https://code.jquery.com/jquery-3.2.1.min.js" crossorigin="anonymous"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" crossorigin="anonymous"></script>
    </body>
</html>